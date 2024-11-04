package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;
import java.util.Random;

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

    public ArmorMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        super(modifier, nbt, lookupProvider);
        replacementArmor.read(nbt, lookupProvider);
    }

    @Override
    public CompoundTag write(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        return replacementArmor.write(super.write(nbt, lookupProvider), lookupProvider);
    }

    @Override
    public void remove(Level world, BlockPos pos) {
        super.remove(world, pos);
        replacementArmor.spawn(world, pos);
    }

    @Override
    public InteractionResult useWithoutItem(MagicMirrorCoreBlockEntity blockEntity, Player player) {
        if (coolingDown()) {
            return InteractionResult.FAIL;
        }

        if (player instanceof ServerPlayer) {
            swapArmor(blockEntity, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useWithItem(MagicMirrorCoreBlockEntity blockEntity, Player player, ItemStack heldItem) {
        if (coolingDown()) {
            return InteractionResult.FAIL;
        }

        if (player instanceof ServerPlayer) {
            if (!tryEquipArmor(blockEntity, player, heldItem)) {
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
    private boolean tryEquipArmor(MagicMirrorCoreBlockEntity blockEntity, LivingEntity player, ItemStack heldItem) {
        if (heldItem.getItem() instanceof ArmorItem) {
            EquipmentSlot equipmentSlotType = player.getEquipmentSlotForItem(heldItem);
            if (equipmentSlotType.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                int slotIndex = equipmentSlotType.getIndex();
                if (replacementArmor.isEmpty(slotIndex)) {
                    BlockPos pos = blockEntity.getBlockPos();
                    Level level = blockEntity.getLevel();
                    if (level instanceof ServerLevel serverLevel) {
                        MessageEquip message = new MessageEquip(pos, slotIndex, heldItem.copy());
                        PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(pos), message);
                    }

                    // Server side
                    replacementArmor.set(slotIndex, heldItem.copy());
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
            MessageSwapMirror mirrorMessage = new MessageSwapMirror(blockEntity, player);
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(pos), mirrorMessage);
        }
        MessageSwapPlayer playerMessage = new MessageSwapPlayer(this, player);
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
        private static final int NUM_SLOTS = 4;

        public static final StreamCodec<RegistryFriendlyByteBuf, ReplacementArmor> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public ReplacementArmor decode(RegistryFriendlyByteBuf byteBuf) {
                NonNullList<ItemStack> inventory = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
                for (int i = 0; i < NUM_SLOTS; ++i) {
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

        private final NonNullList<ItemStack> replacementInventory;

        ReplacementArmor() {
            replacementInventory = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
        }

        ReplacementArmor(Iterable<ItemStack> armor) {
            replacementInventory = NonNullList.create();
            armor.forEach(replacementInventory::add);
        }

        boolean isEmpty(int i) {
            return i >= 0 && i < replacementInventory.size() && replacementInventory.get(i).isEmpty();
        }

        void set(int i, ItemStack stack) {
            replacementInventory.set(i, stack);
        }

        /**
         * Swap the current inventory with another.
         * The inventories should have the same size.
         *
         * @param inventory The inventory to swap with.
         */
        public void swap(NonNullList<ItemStack> inventory) {
            for (int i = 0; i < NUM_SLOTS; ++i) {
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
            NonNullList<ItemStack> armorInventory = player.getInventory().armor;
            for (int i = 0; i < NUM_SLOTS; ++i) {
                ItemStack playerArmor = armorInventory.get(i);
                ItemStack replacement = replacementInventory.get(i);
                if (EnchantmentHelper.has(playerArmor, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || EnchantmentHelper.has(replacement, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
                    // Cannot swap armor with curse of binding
                    continue;
                }
                if (player instanceof ServerPlayer) {
                    // Make sure to do this on the client side as well.
                    ((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(-2, 0, i + 36, replacement));
                }
                replacementInventory.set(i, playerArmor);
                armorInventory.set(i, replacement);
            }
        }

        /**
         * Write the inventory out to an NBT tag compound.
         *
         * @param nbt            The NBT tag compound to write to.
         * @param lookupProvider The holder lookup provider for serializing the item stacks.
         * @return The input compound, for chaining.
         */
        CompoundTag write(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
            ContainerHelper.saveAllItems(nbt, replacementInventory, lookupProvider);
            return nbt;
        }

        /**
         * Load inventory from an NBT tag.
         *
         * @param nbt            The NBT tag compound to read from.
         * @param lookupProvider The holder lookup provider for deserializing the item stacks.
         */
        void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
            ContainerHelper.loadAllItems(nbt, replacementInventory, lookupProvider);
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
    public record MessageEquip(BlockPos mirrorPos, int slotIdx, ItemStack armor) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<MessageEquip> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MagicMirrorMod.MOD_ID, "equip"));

        public static final StreamCodec<RegistryFriendlyByteBuf, MessageEquip> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, MessageEquip::mirrorPos, ByteBufCodecs.INT, MessageEquip::slotIdx, ItemStack.STREAM_CODEC, MessageEquip::armor, MessageEquip::new);

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
            this(new ReplacementArmor(player.getArmorSlots()), magicMirrorBase.getBlockPos());
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

        MessageSwapPlayer(ArmorMagicMirrorBlockEntityModifier armorModifier, Player player) {
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
                            .ifPresent(modifier -> modifier.replacementArmor.set(message.slotIdx, message.armor));
                    ArmorItem item = (ArmorItem) message.armor.getItem();
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
                    Random random = new Random();
                    for (int i = 0; i < SWAP_PARTICLE_COUNT; ++i) {
                        entity.getCommandSenderWorld().addParticle(
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
