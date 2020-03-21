package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorBase;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierArmor;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierArmorClient;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorTileEntityModifierArmor extends MagicMirrorTileEntityModifier {
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
    private ReflectionModifierArmor reflectionModifier;

    /**
     * @param modifier The modifier that applied this object to the tile entity.
     */
    public MagicMirrorTileEntityModifierArmor(MagicMirrorModifier modifier) {
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
    public void activate(TileEntityMagicMirrorBase tileEntity) {
        Reflection reflection = tileEntity.getReflection();
        if (reflection != null) {
            reflectionModifier = createReflectionModifier();
            reflection.addModifier(reflectionModifier);
        }
    }

    private ReflectionModifierArmor createReflectionModifier() {
        return DistExecutor.runForDist(
                () -> () -> new ReflectionModifierArmorClient(replacementArmor),
                () -> () -> new ReflectionModifierArmor(replacementArmor)
        );
    }

    @Override
    public void deactivate(TileEntityMagicMirrorBase tileEntity) {
        if (reflectionModifier != null) {
            Reflection reflection = tileEntity.getReflection();
            if (reflection != null) {
                reflection.removeModifier(reflectionModifier);
            }
        }
    }

    @Override
    public boolean tryPlayerActivate(TileEntityMagicMirrorBase tileEntity, PlayerEntity playerIn, Hand hand) {
        if (coolingDown()) {
            return false;
        }

        // Can only be done on server side.
        if (!(playerIn instanceof ServerPlayerEntity)) {
            return false;
        }

        // Send two individual messages, to cover the situation where a player is tracked but the mirror isn't and vice
        // versa.
        BlockPos pos = tileEntity.getPos();
        MessageSwapMirror mirrorMessage = new MessageSwapMirror(tileEntity, playerIn);
        PacketDistributor.PacketTarget mirrorTarget = PacketDistributor.TRACKING_CHUNK.with(() -> tileEntity.getWorld().getChunkAt(pos));
        Network.CHANNEL.send(mirrorTarget, mirrorMessage);
        MessageSwapPlayer playerMessage = new MessageSwapPlayer(this, playerIn);
        PacketDistributor.PacketTarget playerTarget = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerIn);
        Network.CHANNEL.send(playerTarget, playerMessage);

        // Swap on the server side.
        replacementArmor.swap(playerIn);
        ModMagicMirror.LOGGER.debug("Swapped inventory of mirror");

        setCooldown(COOLDOWN_TICKS);
        tileEntity.markDirty();
        return true;
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
            if (inventory != null) {
                for (int i = 0; i < 4; ++i) {
                    ItemStack replacement = replacementInventory.get(i);
                    replacementInventory.set(i, inventory.get(i));
                    inventory.set(i, replacement);
                }
            }
        }

        /**
         * Swap the current stored armor inventory with a player's.
         *
         * @param player The player to swap armor with.
         */
        void swap(PlayerEntity player) {
            for (int i = 0; i < 4; ++i) {
                if (player instanceof ServerPlayerEntity) {
                    // Make sure to do this on the client side as well.
                    ((ServerPlayerEntity) player).connection.sendPacket(new SSetSlotPacket(-2, i + 36, replacementInventory.get(i)));
                }
                ItemStack replacement = replacementInventory.get(i);
                replacementInventory.set(i, player.inventory.armorInventory.get(i));
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

        void swap(MagicMirrorTileEntityModifierArmor modifier) {
            ModMagicMirror.LOGGER.info("Swapping with mirror");
            swap(modifier.getReplacementArmor().replacementInventory);
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

        public void encodeArmor(PacketBuffer buf) {
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

        @SuppressWarnings("unused")
        public MessageSwapMirror() {
        }

        MessageSwapMirror(TileEntityMagicMirrorBase magicMirrorBase, PlayerEntity player) {
            super(player.getArmorInventoryList());
            mirrorPos = magicMirrorBase.getPos();
        }

        /**
         * Decode a packet into an instance of the message.
         *
         * @param buf The buffer to read from.
         *
         * @return The created message instance.
         */
        public static MessageSwapMirror decode(PacketBuffer buf) {
            MessageSwapMirror result = new MessageSwapMirror();
            result.decodeArmor(buf);
            result.mirrorPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
            return result;
        }

        /**
         * Encode the message into a packet buffer.
         *
         * @param buf The buffer to write to.
         */
        public void encode(PacketBuffer buf) {
            encodeArmor(buf);
            buf.writeInt(mirrorPos.getX());
            buf.writeInt(mirrorPos.getY());
            buf.writeInt(mirrorPos.getZ());
        }
    }

    /**
     * Message describing players swapping armor with the mirror (player side).
     */
    public static class MessageSwapPlayer extends MessageSwap {
        int entityId;

        @SuppressWarnings("unused")
        public MessageSwapPlayer() {
        }

        MessageSwapPlayer(MagicMirrorTileEntityModifierArmor armorModifier, PlayerEntity player) {
            super(armorModifier.getReplacementArmor().replacementInventory);
            entityId = player.getEntityId();
        }

        /**
         * Decode a packet into an instance of the message.
         *
         * @param buf The buffer to read from.
         *
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
     * Handler for messages describing players swapping armor with the mirror.
     */
    public static void onMessageSwapMirror(MessageSwapMirror message, Supplier<NetworkEvent.Context> contextSupplier) {
        TileEntity te = Minecraft.getInstance().world.getTileEntity(message.mirrorPos);
        if (te instanceof TileEntityMagicMirrorBase) {
            ((TileEntityMagicMirrorBase) te).getModifiers().stream()
                    .filter(modifier -> modifier instanceof MagicMirrorTileEntityModifierArmor).findFirst()
                    .ifPresent(modifier -> message.armor.swap((MagicMirrorTileEntityModifierArmor) modifier));
        }
    }

    /**
     * Handler for messages describing players swapping armor with the mirror.
     */
    public static void onMessageSwapPlayer(MessageSwapPlayer message, Supplier<NetworkEvent.Context> contextSupplier) {
        Entity entity = Minecraft.getInstance().world.getEntityByID(message.entityId);

        if (entity instanceof PlayerEntity) {
            message.armor.swap((PlayerEntity) entity);

            entity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, .8f, .4f);
            Random random = new Random();
            for (int i = 0; i < SWAP_PARTICLE_COUNT; ++i) {
                entity.getEntityWorld().addParticle(
                        ParticleTypes.PORTAL,
                        entity.posX + random.nextGaussian() / 4,
                        entity.posY + 2 * random.nextDouble(),
                        entity.posZ + random.nextGaussian() / 4,
                        random.nextGaussian() / 2,
                        random.nextDouble(),
                        random.nextGaussian() / 2
                );
            }
        }
    }
}
