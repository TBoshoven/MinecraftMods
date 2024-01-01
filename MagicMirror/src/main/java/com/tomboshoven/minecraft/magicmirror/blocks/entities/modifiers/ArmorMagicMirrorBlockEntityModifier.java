package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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

    public ArmorMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, CompoundTag nbt) {
        super(modifier, nbt);
        replacementArmor.read(nbt);
    }

    @Override
    protected ItemStack getItemStackOldNbt(CompoundTag nbt) {
        return new ItemStack(Items.ARMOR_STAND);
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {
        return replacementArmor.write(super.write(nbt));
    }

    @Override
    public void remove(Level world, BlockPos pos) {
        super.remove(world, pos);
        replacementArmor.spawn(world, pos);
    }

    @Override
    public boolean tryPlayerActivate(MagicMirrorCoreBlockEntity blockEntity, Player playerIn, InteractionHand hand) {
        if (coolingDown()) {
            return false;
        }

        // Can only be done on server side.
        if (!(playerIn instanceof ServerPlayer)) {
            return false;
        }

        // First try to equip the held armor item. If that didn't work, swap armor.
        if (!tryEquipArmor(blockEntity, playerIn, hand)) {
            swapArmor(blockEntity, playerIn);
        }

        return true;
    }

    /**
     * Attempt to equip the mirror with the held item as armor.
     * This should be called on the server side.
     *
     * @param blockEntity The mirror block entity.
     * @param playerIn    The player entity holding the armor.
     * @param hand        Which hand the player is holding the item in.
     * @return Whether the held item was successfully equipped as armor.
     */
    private boolean tryEquipArmor(MagicMirrorCoreBlockEntity blockEntity, Player playerIn, InteractionHand hand) {
        ItemStack heldItem = playerIn.getItemInHand(hand);
        if (heldItem.getItem() instanceof ArmorItem) {
            EquipmentSlot equipmentSlotType = Mob.getEquipmentSlotForItem(heldItem);
            if (equipmentSlotType.getType() == EquipmentSlot.Type.ARMOR) {
                int slotIndex = equipmentSlotType.getIndex();
                if (replacementArmor.isEmpty(slotIndex)) {
                    BlockPos pos = blockEntity.getBlockPos();
                    Level world = blockEntity.getLevel();
                    if (world != null) {
                        MessageEquip message = new MessageEquip(pos, slotIndex, heldItem);
                        PacketDistributor.PacketTarget mirrorTarget = PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos));
                        Network.CHANNEL.send(mirrorTarget, message);
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
     * @param playerIn    The player entity to swap armor with.
     */
    private void swapArmor(MagicMirrorCoreBlockEntity blockEntity, Player playerIn) {
        // Send two individual messages, to cover the situation where a player is tracked but the mirror isn't and vice
        // versa.
        BlockPos pos = blockEntity.getBlockPos();
        Level world = blockEntity.getLevel();
        if (world != null) {
            MessageSwapMirror mirrorMessage = new MessageSwapMirror(blockEntity, playerIn);
            PacketDistributor.PacketTarget mirrorTarget = PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos));
            Network.CHANNEL.send(mirrorTarget, mirrorMessage);
        }
        MessageSwapPlayer playerMessage = new MessageSwapPlayer(this, playerIn);
        PacketDistributor.PacketTarget playerTarget = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerIn);
        Network.CHANNEL.send(playerTarget, playerMessage);

        // Swap on the server side.
        replacementArmor.swap(playerIn);
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
        private final NonNullList<ItemStack> replacementInventory;

        ReplacementArmor() {
            replacementInventory = NonNullList.withSize(4, ItemStack.EMPTY);
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
            for (int i = 0; i < 4; ++i) {
                ItemStack original = inventory.get(i);
                ItemStack replacement = replacementInventory.get(i);
                if (EnchantmentHelper.hasBindingCurse(original) || EnchantmentHelper.hasBindingCurse(replacement)) {
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
            for (int i = 0; i < 4; ++i) {
                ItemStack playerArmor = player.getInventory().armor.get(i);
                ItemStack replacement = replacementInventory.get(i);
                if (EnchantmentHelper.hasBindingCurse(playerArmor) || EnchantmentHelper.hasBindingCurse(replacement)) {
                    // Cannot swap armor with curse of binding
                    continue;
                }
                if (player instanceof ServerPlayer) {
                    // Make sure to do this on the client side as well.
                    ((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(-2, 0, i + 36, replacement));
                }
                replacementInventory.set(i, playerArmor);
                player.getInventory().armor.set(i, replacement);
            }
        }

        /**
         * Write the inventory out to an NBT tag compound.
         *
         * @param nbt The NBT tag compound to write to.
         * @return The input compound, for chaining.
         */
        CompoundTag write(CompoundTag nbt) {
            ContainerHelper.saveAllItems(nbt, replacementInventory, true);
            return nbt;
        }

        /**
         * Load inventory from an NBT tag.
         *
         * @param nbt The NBT tag compound to read from.
         */
        void read(CompoundTag nbt) {
            ContainerHelper.loadAllItems(nbt, replacementInventory);
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
    public static class MessageEquip {
        final int slotIdx;
        final ItemStack armor;
        final BlockPos mirrorPos;

        MessageEquip(BlockPos mirrorPos, int slotIdx, ItemStack armor) {
            this.mirrorPos = mirrorPos;
            this.slotIdx = slotIdx;
            this.armor = armor.copy();
        }

        /**
         * Decode a packet into an instance of the message.
         *
         * @param buf The buffer to read from.
         * @return The created message instance.
         */
        public static MessageEquip decode(FriendlyByteBuf buf) {
            return new MessageEquip(buf.readBlockPos(), buf.readInt(), buf.readItem());
        }

        /**
         * Encode the message into a packet buffer.
         *
         * @param buf The buffer to write to.
         */
        public void encode(FriendlyByteBuf buf) {
            buf.writeBlockPos(mirrorPos);
            buf.writeInt(slotIdx);
            buf.writeItem(armor);
        }
    }

    /**
     * Base class for messages describing players swapping armor with the mirror.
     */
    static class MessageSwap {
        final ReplacementArmor armor;

        MessageSwap() {
            armor = new ReplacementArmor();
        }

        MessageSwap(Iterable<ItemStack> armor) {
            this.armor = new ReplacementArmor(armor);
        }

        void decodeArmor(FriendlyByteBuf buf) {
            for (int i = 0; i < 4; ++i) {
                armor.set(i, buf.readItem());
            }
        }

        void encodeArmor(FriendlyByteBuf buf) {
            for (ItemStack stack : armor.replacementInventory) {
                buf.writeItem(stack);
            }
        }
    }

    /**
     * Message describing players swapping armor with the mirror (mirror side).
     */
    public static class MessageSwapMirror extends MessageSwap {
        BlockPos mirrorPos;

        @SuppressWarnings({"unused", "WeakerAccess"})
        public MessageSwapMirror() {
        }

        MessageSwapMirror(MagicMirrorCoreBlockEntity magicMirrorBase, Player player) {
            super(player.getArmorSlots());
            mirrorPos = magicMirrorBase.getBlockPos();
        }

        /**
         * Decode a packet into an instance of the message.
         *
         * @param buf The buffer to read from.
         * @return The created message instance.
         */
        public static MessageSwapMirror decode(FriendlyByteBuf buf) {
            MessageSwapMirror result = new MessageSwapMirror();
            result.decodeArmor(buf);
            result.mirrorPos = buf.readBlockPos();
            return result;
        }

        /**
         * Encode the message into a packet buffer.
         *
         * @param buf The buffer to write to.
         */
        public void encode(FriendlyByteBuf buf) {
            encodeArmor(buf);
            buf.writeBlockPos(mirrorPos);
        }
    }

    /**
     * Message describing players swapping armor with the mirror (player side).
     */
    public static class MessageSwapPlayer extends MessageSwap {
        int entityId;

        @SuppressWarnings({"unused", "WeakerAccess"})
        public MessageSwapPlayer() {
        }

        MessageSwapPlayer(ArmorMagicMirrorBlockEntityModifier armorModifier, Player player) {
            super(armorModifier.getReplacementArmor().replacementInventory);
            entityId = player.getId();
        }

        /**
         * Decode a packet into an instance of the message.
         *
         * @param buf The buffer to read from.
         * @return The created message instance.
         */
        public static MessageSwapPlayer decode(FriendlyByteBuf buf) {
            MessageSwapPlayer result = new MessageSwapPlayer();
            result.decodeArmor(buf);
            result.entityId = buf.readInt();
            return result;
        }

        /**
         * Encode the message into a packet buffer.
         *
         * @param buf The buffer to write to.
         */
        public void encode(FriendlyByteBuf buf) {
            encodeArmor(buf);
            buf.writeInt(entityId);
        }
    }

    /**
     * Handler for messages describing players equipping the mirror with armor.
     */
    public static void onMessageEquip(MessageEquip message, NetworkEvent.Context ctx) {
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
                    world.playLocalSound(message.mirrorPos, item.getMaterial().getEquipSound(), SoundSource.BLOCKS, 1, 1, false);
                }
            }
        });
        ctx.setPacketHandled(true);
    }

    /**
     * Handler for messages describing players swapping armor with the mirror.
     */
    public static void onMessageSwapMirror(MessageSwapMirror message, NetworkEvent.Context ctx) {
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
        ctx.setPacketHandled(true);
    }

    /**
     * Handler for messages describing players swapping armor with the mirror.
     */
    public static void onMessageSwapPlayer(MessageSwapPlayer message, NetworkEvent.Context ctx) {
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
        ctx.setPacketHandled(true);
    }
}
