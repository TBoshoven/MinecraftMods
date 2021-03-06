package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ArmorReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ArmorReflectionModifierClient;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArmorMagicMirrorTileEntityModifier extends MagicMirrorTileEntityModifier {
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
     * The object that modifies the reflection in the mirror to show the replacement armor.
     */
    @Nullable
    private ArmorReflectionModifier reflectionModifier;

    /**
     * @param modifier The modifier that applied this object to the tile entity.
     */
    public ArmorMagicMirrorTileEntityModifier(MagicMirrorModifier modifier) {
        super(modifier);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        return replacementArmor.write(super.write(nbt));
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        replacementArmor.read(nbt);
    }

    @Override
    public void remove(World world, BlockPos pos) {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.ARMOR_STAND));
        replacementArmor.spawn(world, pos);
    }

    @Override
    public void activate(MagicMirrorBaseTileEntity tileEntity) {
        Reflection reflection = tileEntity.getReflection();
        if (reflection != null) {
            reflectionModifier = createReflectionModifier();
            reflection.addModifier(reflectionModifier);
        }
    }

    private ArmorReflectionModifier createReflectionModifier() {
        return DistExecutor.runForDist(
                () -> () -> new ArmorReflectionModifierClient(replacementArmor),
                () -> () -> new ArmorReflectionModifier(replacementArmor)
        );
    }

    @Override
    public void deactivate(MagicMirrorBaseTileEntity tileEntity) {
        if (reflectionModifier != null) {
            Reflection reflection = tileEntity.getReflection();
            if (reflection != null) {
                reflection.removeModifier(reflectionModifier);
            }
        }
    }

    @Override
    public boolean tryPlayerActivate(MagicMirrorBaseTileEntity tileEntity, PlayerEntity playerIn, Hand hand) {
        if (coolingDown()) {
            return false;
        }

        // Can only be done on server side.
        if (!(playerIn instanceof ServerPlayerEntity)) {
            return false;
        }

        // First try to equip the held armor item. If that didn't work, swap armor.
        if (!tryEquipArmor(tileEntity, playerIn, hand)) {
            swapArmor(tileEntity, playerIn);
        }

        return true;
    }

    /**
     * Attempt to equip the mirror with the held item as armor.
     * This should be called on the server side.
     *
     * @param tileEntity The mirror tile entity.
     * @param playerIn The player entity holding the armor.
     * @param hand Which hand the player is holding the item in.
     *
     * @return Whether the held item was successfully equipped as armor.
     */
    private boolean tryEquipArmor(MagicMirrorBaseTileEntity tileEntity, PlayerEntity playerIn, Hand hand) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (heldItem.getItem() instanceof ArmorItem) {
            EquipmentSlotType equipmentSlotType = MobEntity.getSlotForItemStack(heldItem);
            if (equipmentSlotType.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                int slotIndex = equipmentSlotType.getIndex();
                if (replacementArmor.isEmpty(slotIndex)) {
                    BlockPos pos = tileEntity.getPos();
                    World world = tileEntity.getWorld();
                    if (world != null) {
                        MessageEquip message = new MessageEquip(pos, slotIndex, heldItem);
                        PacketDistributor.PacketTarget mirrorTarget = PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos));
                        Network.CHANNEL.send(mirrorTarget, message);
                    }

                    // Server side
                    replacementArmor.set(slotIndex, heldItem.copy());
                    heldItem.setCount(0);
                    tileEntity.markDirty();
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
     * @param tileEntity The mirror tile entity.
     * @param playerIn The player entity to swap armor with.
     */
    private void swapArmor(MagicMirrorBaseTileEntity tileEntity, PlayerEntity playerIn) {
        // Send two individual messages, to cover the situation where a player is tracked but the mirror isn't and vice
        // versa.
        BlockPos pos = tileEntity.getPos();
        World world = tileEntity.getWorld();
        if (world != null) {
            MessageSwapMirror mirrorMessage = new MessageSwapMirror(tileEntity, playerIn);
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
        tileEntity.markDirty();
    }

    /**
     * The replacement armor as stored in the mirror.
     */
    private ReplacementArmor getReplacementArmor() {
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
         * They inventories should have the same size.
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
        void swap(PlayerEntity player) {
            for (int i = 0; i < 4; ++i) {
                ItemStack playerArmor = player.inventory.armorInventory.get(i);
                ItemStack replacement = replacementInventory.get(i);
                if (EnchantmentHelper.hasBindingCurse(playerArmor) || EnchantmentHelper.hasBindingCurse(replacement)) {
                    // Cannot swap armor with curse of binding
                    continue;
                }
                if (player instanceof ServerPlayerEntity) {
                    // Make sure to do this on the client side as well.
                    ((ServerPlayerEntity) player).connection.sendPacket(new SSetSlotPacket(-2, i + 36, replacement));
                }
                replacementInventory.set(i, playerArmor);
                player.inventory.armorInventory.set(i, replacement);
            }
        }

        /**
         * Write the inventory out to an NBT tag compound.
         *
         * @param nbt The NBT tag compound to write to.
         * @return The input compound, for chaining.
         */
        CompoundNBT write(CompoundNBT nbt) {
            ItemStackHelper.saveAllItems(nbt, replacementInventory, true);
            return nbt;
        }

        /**
         * Load inventory from an NBT tag.
         *
         * @param nbt The NBT tag compound to read from.
         */
        void read(CompoundNBT nbt) {
            ItemStackHelper.loadAllItems(nbt, replacementInventory);
        }

        /**
         * Spawn the inventory items as entities into the world.
         * <p>
         * This removes them from this inventory.
         *
         * @param world The world in which to spawn the item entities.
         * @param pos   The location to spawn the item entities in.
         */
        void spawn(World world, BlockPos pos) {
            for (ItemStack itemStack : replacementInventory) {
                if (!itemStack.isEmpty()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                    itemStack.setCount(0);
                }
            }
        }

        void swap(ArmorMagicMirrorTileEntityModifier modifier) {
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
        public static MessageEquip decode(PacketBuffer buf) {
            return new MessageEquip(buf.readBlockPos(), buf.readInt(), buf.readItemStack());
        }

        /**
         * Encode the message into a packet buffer.
         *
         * @param buf The buffer to write to.
         */
        public void encode(PacketBuffer buf) {
            buf.writeBlockPos(mirrorPos);
            buf.writeInt(slotIdx);
            buf.writeItemStack(armor);
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

        void decodeArmor(PacketBuffer buf) {
            for (int i = 0; i < 4; ++i) {
                armor.set(i, buf.readItemStack());
            }
        }

        void encodeArmor(PacketBuffer buf) {
            for (ItemStack stack : armor.replacementInventory) {
                buf.writeItemStack(stack);
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

        MessageSwapMirror(MagicMirrorBaseTileEntity magicMirrorBase, PlayerEntity player) {
            super(player.getArmorInventoryList());
            mirrorPos = magicMirrorBase.getPos();
        }

        /**
         * Decode a packet into an instance of the message.
         *
         * @param buf The buffer to read from.
         * @return The created message instance.
         */
        public static MessageSwapMirror decode(PacketBuffer buf) {
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
        public void encode(PacketBuffer buf) {
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

        MessageSwapPlayer(ArmorMagicMirrorTileEntityModifier armorModifier, PlayerEntity player) {
            super(armorModifier.getReplacementArmor().replacementInventory);
            entityId = player.getEntityId();
        }

        /**
         * Decode a packet into an instance of the message.
         *
         * @param buf The buffer to read from.
         * @return The created message instance.
         */
        public static MessageSwapPlayer decode(PacketBuffer buf) {
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
        public void encode(PacketBuffer buf) {
            encodeArmor(buf);
            buf.writeInt(entityId);
        }
    }

    /**
     * Handler for messages describing players equipping the mirror with armor.
     */
    public static void onMessageEquip(MessageEquip message, Supplier<? extends NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world != null) {
                TileEntity te = world.getTileEntity(message.mirrorPos);
                if (te instanceof MagicMirrorBaseTileEntity) {
                    ((MagicMirrorBaseTileEntity) te).getModifiers().stream()
                            .filter(modifier -> modifier instanceof ArmorMagicMirrorTileEntityModifier).findFirst()
                            .map(ArmorMagicMirrorTileEntityModifier.class::cast)
                            .ifPresent(modifier -> modifier.replacementArmor.set(message.slotIdx, message.armor));
                    ArmorItem item = (ArmorItem) message.armor.getItem();
                    world.playSound(message.mirrorPos, item.getArmorMaterial().getSoundEvent(), SoundCategory.BLOCKS, 1, 1, false);
                }
            }
        }));
        ctx.setPacketHandled(true);
    }

    /**
     * Handler for messages describing players swapping armor with the mirror.
     */
    public static void onMessageSwapMirror(MessageSwapMirror message, Supplier<? extends NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world != null) {
                TileEntity te = world.getTileEntity(message.mirrorPos);
                if (te instanceof MagicMirrorBaseTileEntity) {
                    ((MagicMirrorBaseTileEntity) te).getModifiers().stream()
                            .filter(modifier -> modifier instanceof ArmorMagicMirrorTileEntityModifier).findFirst()
                            .ifPresent(modifier -> message.armor.swap((ArmorMagicMirrorTileEntityModifier) modifier));
                }
            }
        }));
        ctx.setPacketHandled(true);
    }

    /**
     * Handler for messages describing players swapping armor with the mirror.
     */
    public static void onMessageSwapPlayer(MessageSwapPlayer message, Supplier<? extends NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world != null) {
                Entity entity = world.getEntityByID(message.entityId);

                if (entity instanceof PlayerEntity) {
                    message.armor.swap((PlayerEntity) entity);

                    entity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, .8f, .4f);
                    Random random = new Random();
                    for (int i = 0; i < SWAP_PARTICLE_COUNT; ++i) {
                        entity.getEntityWorld().addParticle(
                                ParticleTypes.PORTAL,
                                entity.getPosX() + random.nextGaussian() / 4,
                                entity.getPosY() + 2 * random.nextDouble(),
                                entity.getPosZ() + random.nextGaussian() / 4,
                                random.nextGaussian() / 2,
                                random.nextDouble(),
                                random.nextGaussian() / 2
                        );
                    }
                }
            }
        }));
        ctx.setPacketHandled(true);
    }
}
