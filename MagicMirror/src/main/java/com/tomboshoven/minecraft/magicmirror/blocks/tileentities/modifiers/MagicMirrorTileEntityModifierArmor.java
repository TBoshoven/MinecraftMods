package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorBase;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierArmor;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierArmor.Factory;
import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

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
    /**
     * Handler for messages of players swapping armor with the mirror (mirror side).
     */
    @SuppressWarnings("PublicField")
    @SidedProxy(
            clientSide = "com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor$MessageHandlerSwapMirrorClient",
            serverSide = "com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor$MessageHandlerSwapMirrorServer"
    )
    public static IMessageHandler<MessageSwapMirror, IMessage> messageHandlerSwapMirror;
    /**
     * Handler for messages of players swapping armor with the mirror (player side).
     */
    @SuppressWarnings("PublicField")
    @SidedProxy(
            clientSide = "com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor$MessageHandlerSwapPlayerClient",
            serverSide = "com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor$MessageHandlerSwapPlayerServer"
    )
    public static IMessageHandler<MessageSwapPlayer, IMessage> messageHandlerSwapPlayer;
    /**
     * Factory for creating the reflection modifier.
     */
    @SidedProxy(
            serverSide = "com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierArmor$Factory",
            clientSide = "com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierArmorClient$Factory"
    )
    private static Factory reflectionModifierFactory;
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
            reflectionModifier = tileEntity.getWorld().isRemote ? reflectionModifierFactory.createClient(replacementArmor) : reflectionModifierFactory.createServer(replacementArmor);
            reflection.addModifier(reflectionModifier);
        }
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
        if (!(playerIn instanceof EntityPlayerMP)) {
            return false;
        }

        // Send two individual messages, to cover the situation where a player is tracked but the mirror isn't and vice
        // versa.
        BlockPos pos = tileEntity.getPos();
        IMessage mirrorMessage = new MessageSwapMirror(tileEntity, playerIn);
        Network.sendToAllTracking(mirrorMessage, tileEntity.getWorld(), pos);
        IMessage playerMessage = new MessageSwapPlayer(this, playerIn);
        Network.sendTo(playerMessage, (EntityPlayerMP) playerIn);
        Network.sendToAllTracking(playerMessage, playerIn);

        // Swap on the server side.
        replacementArmor.swap(playerIn);
        ModMagicMirror.logger.debug("Swapped inventory of mirror");

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
                if (player instanceof EntityPlayerMP) {
                    // Usually the case for EntityPlayerMP, so server-side stuff.
                    ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-2, i + 36, replacementInventory.get(i)));
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
            ModMagicMirror.logger.info("Swapping with mirror");
            swap(modifier.getReplacementArmor().replacementInventory);
        }
    }

    /**
     * Base class for messages describing players swapping armor with the mirror.
     */
    static class MessageSwap implements IMessage {
        final ReplacementArmor armor;

        MessageSwap() {
            armor = new ReplacementArmor();
        }

        MessageSwap(Iterable<ItemStack> armor) {
            this.armor = new ReplacementArmor(armor);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            for (int i = 0; i < 4; ++i) {
                armor.set(i, ByteBufUtils.readItemStack(buf));
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            for (ItemStack stack : armor.replacementInventory) {
                ByteBufUtils.writeItemStack(buf, stack);
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

        @Override
        public void fromBytes(ByteBuf buf) {
            super.fromBytes(buf);
            mirrorPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        }

        @Override
        public void toBytes(ByteBuf buf) {
            super.toBytes(buf);
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

        @Override
        public void fromBytes(ByteBuf buf) {
            super.fromBytes(buf);
            entityId = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            super.toBytes(buf);
            buf.writeInt(entityId);
        }
    }

    /**
     * Handler for messages describing players swapping armor with the mirror (mirror side, client).
     */
    @OnlyIn(Dist.CLIENT)
    public static class MessageHandlerSwapMirrorClient implements IMessageHandler<MessageSwapMirror, IMessage> {
        @Nullable
        @Override
        public IMessage onMessage(MessageSwapMirror message, MessageContext ctx) {
            TileEntity te = Minecraft.getInstance().world.getTileEntity(message.mirrorPos);
            if (te instanceof TileEntityMagicMirrorBase) {
                ((TileEntityMagicMirrorBase) te).getModifiers().stream()
                        .filter(modifier -> modifier instanceof MagicMirrorTileEntityModifierArmor).findFirst()
                        .ifPresent(modifier -> message.armor.swap((MagicMirrorTileEntityModifierArmor) modifier));
            }
            return null;
        }
    }

    /**
     * Handler for messages describing players swapping armor with the mirror (mirror side, server).
     */
    @SideOnly(Side.SERVER)
    public static class MessageHandlerSwapMirrorServer implements IMessageHandler<MessageSwapMirror, IMessage> {
        @Nullable
        @Override
        public IMessage onMessage(MessageSwapMirror message, MessageContext ctx) {
            return null;
        }
    }

    /**
     * Handler for messages describing players swapping armor with the mirror (player side, client).
     */
    @OnlyIn(Dist.CLIENT)
    public static class MessageHandlerSwapPlayerClient implements IMessageHandler<MessageSwapPlayer, IMessage> {
        @Nullable
        @Override
        public IMessage onMessage(MessageSwapPlayer message, MessageContext ctx) {
            Entity entity = Minecraft.getInstance().world.getEntityByID(message.entityId);

            if (entity instanceof PlayerEntity) {
                message.armor.swap((PlayerEntity) entity);

                entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, .8f, .4f);
                Random random = new Random();
                for (int i = 0; i < SWAP_PARTICLE_COUNT; ++i) {
                    entity.getEntityWorld().spawnParticle(
                            EnumParticleTypes.PORTAL,
                            entity.posX + random.nextGaussian() / 4,
                            entity.posY + 2 * random.nextDouble(),
                            entity.posZ + random.nextGaussian() / 4,
                            random.nextGaussian() / 2,
                            random.nextDouble(),
                            random.nextGaussian() / 2
                    );
                }
            }
            return null;
        }
    }

    /**
     * Handler for messages describing players swapping armor with the mirror (player side, server).
     */
    @SideOnly(Side.SERVER)
    public static class MessageHandlerSwapPlayerServer implements IMessageHandler<MessageSwapPlayer, IMessage> {
        @Nullable
        @Override
        public IMessage onMessage(MessageSwapPlayer message, MessageContext ctx) {
            return null;
        }
    }
}
