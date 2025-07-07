package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.random.RandomGenerator;

public class ArmorMagicMirrorBlockEntityModifier extends ItemBasedMagicMirrorBlockEntityModifier {
    /**
     * The number of ticks this modifier needs to cool down.
     */
    private static final int COOLDOWN_TICKS = 20;
    /**
     * The number of particles to spawn around a player that swaps armor with the mirror.
     */
    private static final int SWAP_PARTICLE_COUNT = 64;

    private final ReplacementArmor replacementArmor = new ReplacementArmor();

    /**
     * @param modifier The modifier that applied this object to the block entity.
     */
    public ArmorMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ItemStack item) {
        super(modifier, item);
    }

    public ArmorMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ValueInput input) {
        super(modifier, input);
        replacementArmor.read(input);
    }

    @Override
    public void save(ValueOutput output) {
        super.save(output);
        replacementArmor.save(output);
    }

    @Override
    public void remove(@Nullable Level world, BlockPos pos) {
        super.remove(world, pos);
        if (world != null) {
            replacementArmor.spawn(world, pos);
        }
    }

    @Override
    public InteractionResult useWithoutItem(MagicMirrorCoreBlockEntity blockEntity, Player player) {
        if (isCoolingDown()) {
            return InteractionResult.FAIL;
        }

        if (player instanceof ServerPlayer) {
            swapArmor(blockEntity, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useWithItem(MagicMirrorCoreBlockEntity blockEntity, Player player, ItemStack heldItem) {
        if (isCoolingDown()) {
            return InteractionResult.FAIL;
        }

        if (player instanceof ServerPlayer) {
            if (!tryEquipArmor(blockEntity, heldItem)) {
                swapArmor(blockEntity, player);
            }
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Attempt to equip the mirror with the held item as armor.
     * This should be called on the server side.
     *
     * @param blockEntity The mirror block entity.
     * @param heldItem    The item held by the player.
     * @return Whether the held item was successfully equipped as armor.
     */
    private boolean tryEquipArmor(MagicMirrorCoreBlockEntity blockEntity, ItemStack heldItem) {
        Equippable equippableComponent = heldItem.get(DataComponents.EQUIPPABLE);
        if (equippableComponent != null) {
            EquipmentSlot equipmentSlot = equippableComponent.slot();
            if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                if (replacementArmor.isEmpty(equipmentSlot)) {
                    BlockPos pos = blockEntity.getBlockPos();
                    Level level = blockEntity.getLevel();
                    if (level instanceof ServerLevel serverLevel) {
                        CustomPacketPayload message = new MessageEquip(pos, equipmentSlot, heldItem.copy());
                        PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(pos), message);
                    }

                    // Server side
                    replacementArmor.set(equipmentSlot, heldItem.copy());
                    heldItem.setCount(0);
                    blockEntity.setChanged();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Swap armor with the player.
     * This should be called on the server side.
     *
     * @param blockEntity The mirror block entity.
     * @param player      The player entity to swap armor with.
     */
    private void swapArmor(MagicMirrorCoreBlockEntity blockEntity, Player player) {
        // Send two individual messages, to cover the situation where a player is tracked but the mirror isn't and vice
        // versa.
        BlockPos pos = blockEntity.getBlockPos();
        Level world = blockEntity.getLevel();
        if (world instanceof ServerLevel serverLevel) {
            CustomPacketPayload mirrorMessage = new MessageSwapMirror(blockEntity, player);
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(pos), mirrorMessage);
        }
        CustomPacketPayload playerMessage = new MessageSwapPlayer(this, player);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, playerMessage);

        // Swap on the server side.
        replacementArmor.swap(player);
        MagicMirrorMod.LOGGER.debug("Swapped inventory of mirror");

        setCooldown(COOLDOWN_TICKS);
        blockEntity.setChanged();
    }

    /**
     * The replacement armor as stored in the mirror.
     */
    public ReplacementArmor getReplacementArmor() {
        return replacementArmor;
    }

    /**
     * Container class for a swapped-out inventory.
     */
    public static class ReplacementArmor {
        /**
         * Slot numbers of the equipment slots we're targeting.
         */
        private static final int[] SLOTS = Inventory.EQUIPMENT_SLOT_MAPPING.int2ObjectEntrySet().stream().filter(e -> e.getValue().getType() == EquipmentSlot.Type.HUMANOID_ARMOR).mapToInt(Int2ObjectMap.Entry::getIntKey).sorted().toArray();
        /**
         * Reverse lookup for equipment slot to index in our array.
         */
        private static final Object2IntMap<EquipmentSlot> INDEX_BY_SLOT = new Object2IntArrayMap<>();

        static {
            for (int i = 0; i < SLOTS.length; ++i) {
                EquipmentSlot equipmentSlot = Inventory.EQUIPMENT_SLOT_MAPPING.get(SLOTS[i]);
                if (equipmentSlot != null) {
                    INDEX_BY_SLOT.put(equipmentSlot, i);
                }
            }
        }

        static final StreamCodec<RegistryFriendlyByteBuf, ReplacementArmor> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public ReplacementArmor decode(RegistryFriendlyByteBuf byteBuf) {
                NonNullList<ItemStack> inventory = NonNullList.withSize(SLOTS.length, ItemStack.EMPTY);
                for (int i = 0; i < SLOTS.length; ++i) {
                    int finalI = i;
                    ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(byteBuf).ifPresent(itemStack -> inventory.set(finalI, itemStack));
                }
                return new ReplacementArmor(inventory);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf o, ReplacementArmor replacementArmor) {
                for (ItemStack itemStack : replacementArmor.replacementInventory) {
                    Optional<ItemStack> optionalStack = itemStack.isEmpty() ? Optional.empty() : Optional.of(itemStack);
                    ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(o, optionalStack);
                }
            }
        };

        // The actual inventory. Use SLOTS and INDEX_BY_SLOT for index lookups.
        private final NonNullList<ItemStack> replacementInventory;

        ReplacementArmor() {
            this(NonNullList.withSize(SLOTS.length, ItemStack.EMPTY));
        }

        private ReplacementArmor(NonNullList<ItemStack> replacementInventory) {
            this.replacementInventory = replacementInventory;
        }

        /**
         * Create a replacement armor instance from a player's inventory.
         * This does not also remove it from the player.
         *
         * @param player The player to get the armor info from.
         * @return The replacement armor instance.
         */
        static ReplacementArmor fromPlayer(Player player) {
            Inventory playerInventory = player.getInventory();
            NonNullList<ItemStack> items = NonNullList.withSize(SLOTS.length, ItemStack.EMPTY);
            for (int i = 0; i < SLOTS.length; ++i) {
                items.set(i, playerInventory.getItem(SLOTS[i]));
            }
            return new ReplacementArmor(items);
        }

        /**
         * @param slot The slot to check.
         * @return Whether the slot is empty.
         */
        boolean isEmpty(EquipmentSlot slot) {
            int idx = INDEX_BY_SLOT.getOrDefault(slot, -1);
            if (idx == -1) {
                return false;
            }
            return replacementInventory.get(idx).isEmpty();
        }

        /**
         * Set a slot to a specific item.
         *
         * @param slot  The slot to update.
         * @param stack The item to put in the slot.
         */
        void set(EquipmentSlot slot, ItemStack stack) {
            int idx = INDEX_BY_SLOT.getOrDefault(slot, -1);
            if (idx == -1) {
                return;
            }
            replacementInventory.set(idx, stack);
        }

        /**
         * Get the proper armor piece by equipment slot.
         *
         * @param slot The slot to read from.
         * @return The armor item in that slot, or EMPTY if the slot has no armor in it.
         */
        public final ItemStack get(EquipmentSlot slot) {
            // Find the slot
            int idx = INDEX_BY_SLOT.getOrDefault(slot, -1);
            if (idx == -1) {
                return ItemStack.EMPTY;
            }
            return replacementInventory.get(idx);
        }

        /**
         * Swap the current inventory with another.
         * The inventories should have the same size.
         *
         * @param inventory The inventory to swap with.
         */
        void swap(NonNullList<ItemStack> inventory) {
            for (int i = 0; i < SLOTS.length; ++i) {
                ItemStack original = inventory.get(i);
                ItemStack replacement = replacementInventory.get(i);
                if (EnchantmentHelper.has(original, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || EnchantmentHelper.has(replacement, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
                    // Cannot swap armor with curse of binding
                    continue;
                }
                replacementInventory.set(i, original);
                inventory.set(i, replacement);
            }
        }

        /**
         * Swap the current stored armor inventory with a player's.
         *
         * @param player The player to swap armor with.
         */
        void swap(Player player) {
            Inventory playerInventory = player.getInventory();
            for (int i = 0; i < SLOTS.length; ++i) {
                ItemStack playerArmor = playerInventory.getItem(SLOTS[i]);
                ItemStack replacement = replacementInventory.get(i);
                if (EnchantmentHelper.has(playerArmor, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || EnchantmentHelper.has(replacement, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
                    // Cannot swap armor with curse of binding
                    continue;
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    // Make sure to do this on the client side as well.
                    serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, SLOTS[i], replacement));
                }
                replacementInventory.set(i, playerArmor);
                playerInventory.setItem(SLOTS[i], replacement);
            }
        }

        /**
         * Write the inventory out to a value output.
         *
         * @param output The value output to write to.
         */
        void save(ValueOutput output) {
            ContainerHelper.saveAllItems(output, replacementInventory);
        }

        /**
         * Load inventory from a value input.
         *
         * @param input The NBT tag compound to read from.
         */
        void read(ValueInput input) {
            ContainerHelper.loadAllItems(input, replacementInventory);
        }

        /**
         * Spawn the inventory items as entities into the world.
         * <p>
         * This removes them from this inventory.
         *
         * @param world The world in which to spawn the item entities.
         * @param pos   The location to spawn the item entities in.
         */
        void spawn(Level world, BlockPos pos) {
            for (ItemStack itemStack : replacementInventory) {
                if (!itemStack.isEmpty()) {
                    Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                    itemStack.setCount(0);
                }
            }
        }

        void swap(ArmorMagicMirrorBlockEntityModifier modifier) {
            MagicMirrorMod.LOGGER.info("Swapping with mirror");
            swap(modifier.getReplacementArmor().replacementInventory);
        }
    }

    /**
     * Message describing players equipping the mirror with armor.
     */
    public record MessageEquip(BlockPos mirrorPos, EquipmentSlot slot, ItemStack armor) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<MessageEquip> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MagicMirrorMod.MOD_ID, "equip"));

        public static final StreamCodec<RegistryFriendlyByteBuf, MessageEquip> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, MessageEquip::mirrorPos, ByteBufCodecs.STRING_UTF8.map(EquipmentSlot::byName, EquipmentSlot::getName), MessageEquip::slot, ItemStack.STREAM_CODEC, MessageEquip::armor, MessageEquip::new);

        @Override
        public CustomPacketPayload.Type<MessageEquip> type() {
            return TYPE;
        }
    }

    /**
     * Message describing players swapping armor with the mirror (mirror side).
     */
    public record MessageSwapMirror(ReplacementArmor armor, BlockPos mirrorPos) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<MessageSwapMirror> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MagicMirrorMod.MOD_ID, "swap_mirror"));

        MessageSwapMirror(MagicMirrorCoreBlockEntity magicMirrorBase, Player player) {
            this(ReplacementArmor.fromPlayer(player), magicMirrorBase.getBlockPos());
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, MessageSwapMirror> STREAM_CODEC = StreamCodec.composite(ReplacementArmor.STREAM_CODEC, MessageSwapMirror::armor, BlockPos.STREAM_CODEC, MessageSwapMirror::mirrorPos, MessageSwapMirror::new);

        @Override
        public CustomPacketPayload.Type<MessageSwapMirror> type() {
            return TYPE;
        }
    }

    /**
     * Message describing players swapping armor with the mirror (player side).
     */
    public record MessageSwapPlayer(ReplacementArmor armor, int entityId) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<MessageSwapPlayer> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MagicMirrorMod.MOD_ID, "swap_player"));

        MessageSwapPlayer(ArmorMagicMirrorBlockEntityModifier armorModifier, EntityAccess player) {
            this(new ReplacementArmor(armorModifier.getReplacementArmor().replacementInventory), player.getId());
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, MessageSwapPlayer> STREAM_CODEC = StreamCodec.composite(ReplacementArmor.STREAM_CODEC, MessageSwapPlayer::armor, ByteBufCodecs.INT, MessageSwapPlayer::entityId, MessageSwapPlayer::new);

        @Override
        public CustomPacketPayload.Type<MessageSwapPlayer> type() {
            return TYPE;
        }
    }

    /**
     * Handler for messages describing players equipping the mirror with armor.
     */
    public static void onMessageEquip(MessageEquip message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                BlockEntity te = world.getBlockEntity(message.mirrorPos);
                if (te instanceof MagicMirrorCoreBlockEntity) {
                    ((MagicMirrorCoreBlockEntity) te).getModifiers().stream()
                            .filter(modifier -> modifier instanceof ArmorMagicMirrorBlockEntityModifier).findFirst()
                            .map(ArmorMagicMirrorBlockEntityModifier.class::cast)
                            .ifPresent(modifier -> modifier.replacementArmor.set(message.slot, message.armor));
                    Item item = message.armor.getItem();
                    // Play appropriate equip sound
                    Equippable equippable = item.components().get(DataComponents.EQUIPPABLE);
                    if (equippable != null) {
                        world.playLocalSound(message.mirrorPos, equippable.equipSound().value(), SoundSource.BLOCKS, 1, 1, false);
                    }
                }
            }
        });
    }

    /**
     * Handler for messages describing players swapping armor with the mirror.
     */
    public static void onMessageSwapMirror(MessageSwapMirror message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                BlockEntity te = world.getBlockEntity(message.mirrorPos);
                if (te instanceof MagicMirrorCoreBlockEntity) {
                    ((MagicMirrorCoreBlockEntity) te).getModifiers().stream()
                            .filter(modifier -> modifier instanceof ArmorMagicMirrorBlockEntityModifier).findFirst()
                            .ifPresent(modifier -> message.armor.swap((ArmorMagicMirrorBlockEntityModifier) modifier));
                }
            }
        });
    }

    /**
     * Handler for messages describing players swapping armor with the mirror.
     */
    public static void onMessageSwapPlayer(MessageSwapPlayer message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                Entity entity = world.getEntity(message.entityId);

                if (entity instanceof Player) {
                    message.armor.swap((Player) entity);

                    entity.playSound(SoundEvents.ENDERMAN_TELEPORT, .8f, .4f);
                    RandomGenerator random = new Random();
                    for (int i = 0; i < SWAP_PARTICLE_COUNT; ++i) {
                        world.addParticle(
                                ParticleTypes.PORTAL,
                                entity.getX() + random.nextGaussian() / 4,
                                entity.getY() + 2 * random.nextDouble(),
                                entity.getZ() + random.nextGaussian() / 4,
                                random.nextGaussian() / 2,
                                random.nextDouble(),
                                random.nextGaussian() / 2
                        );
                    }
                }
            }
        });
    }
}
