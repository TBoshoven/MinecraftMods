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
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
     * Handler for messages of players equipping the mirror with armor.
     */
    @SuppressWarnings("PublicField")
    @SidedProxy(
            clientSide = "com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor$MessageHandlerEquipClient",
            serverSide = "com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor$MessageHandlerEquipServer"
    )
    public static IMessageHandler<MessageEquip, IMessage> messageHandlerEquip;
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
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        return replacementArmor.writeToNBT(super.writeToNBT(nbt));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        replacementArmor.readFromNBT(nbt);
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
    public boolean tryPlayerActivate(TileEntityMagicMirrorBase tileEntity, EntityPlayer playerIn, EnumHand hand) {
        if (coolingDown()) {
            return false;
        }

        // Can only be done on server side.
        if (!(playerIn instanceof EntityPlayerMP)) {
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
    private boolean tryEquipArmor(TileEntityMagicMirrorBase tileEntity, EntityPlayer playerIn, EnumHand hand) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (heldItem.getItem() instanceof ItemArmor) {
            EntityEquipmentSlot entityEquipmentSlot = EntityMob.getSlotForItemStack(heldItem);
            if (entityEquipmentSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                int slotIndex = entityEquipmentSlot.getIndex();
                if (replacementArmor.isEmpty(slotIndex)) {
                    BlockPos pos = tileEntity.getPos();
                    World world = tileEntity.getWorld();
                    MessageEquip message = new MessageEquip(pos, slotIndex, heldItem);
                    Network.sendToAllTracking(message, world, pos);

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
    private void swapArmor(TileEntityMagicMirrorBase tileEntity, EntityPlayer playerIn) {
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
        void swap(EntityPlayer player) {
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
        NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            ItemStackHelper.saveAllItems(nbt, replacementInventory, true);
            return nbt;
        }

        /**
         * Load inventory from an NBT tag.
         *
         * @param nbt The NBT tag compound to read from.
         */
        void readFromNBT(NBTTagCompound nbt) {
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
     * Message describing players equipping the mirror with armor.
     */
    public static class MessageEquip implements IMessage {
        int slotIdx;
        ItemStack armor;
        BlockPos mirrorPos;

        public MessageEquip() {
            armor = ItemStack.EMPTY;
            mirrorPos = BlockPos.ORIGIN;
        }

        MessageEquip(BlockPos mirrorPos, int slotIdx, ItemStack armor) {
            this.mirrorPos = mirrorPos;
            this.slotIdx = slotIdx;
            this.armor = armor.copy();
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            mirrorPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
            slotIdx = buf.readInt();
            armor = ByteBufUtils.readItemStack(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(mirrorPos.getX());
            buf.writeInt(mirrorPos.getY());
            buf.writeInt(mirrorPos.getZ());
            buf.writeInt(slotIdx);
            ByteBufUtils.writeItemStack(buf, armor);
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

        MessageSwapMirror(TileEntityMagicMirrorBase magicMirrorBase, EntityPlayer player) {
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

        MessageSwapPlayer(MagicMirrorTileEntityModifierArmor armorModifier, EntityPlayer player) {
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
     * Handler for messages describing players equipping the mirror with armor.
     */
    public static class MessageHandlerEquipClient implements IMessageHandler<MessageEquip, IMessage> {
        @Nullable
        @Override
        public IMessage onMessage(MessageEquip message, MessageContext ctx) {
            WorldClient world = Minecraft.getMinecraft().world;
            TileEntity te = world.getTileEntity(message.mirrorPos);
            if (te instanceof TileEntityMagicMirrorBase) {
                ((TileEntityMagicMirrorBase) te).getModifiers().stream()
                        .filter(modifier -> modifier instanceof MagicMirrorTileEntityModifierArmor).findFirst()
                        .map(MagicMirrorTileEntityModifierArmor.class::cast)
                        .ifPresent(modifier -> modifier.replacementArmor.set(message.slotIdx, message.armor));
                ItemArmor item = (ItemArmor)message.armor.getItem();
                world.playSound(message.mirrorPos, item.getArmorMaterial().getSoundEvent(), SoundCategory.BLOCKS, 1, 1, false);
            }
            return null;
        }
    }

    /**
     * Handler for messages describing players swapping armor with the mirror (mirror side, client).
     */
    @SideOnly(Side.CLIENT)
    public static class MessageHandlerSwapMirrorClient implements IMessageHandler<MessageSwapMirror, IMessage> {
        @Nullable
        @Override
        public IMessage onMessage(MessageSwapMirror message, MessageContext ctx) {
            TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.mirrorPos);
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
    @SideOnly(Side.CLIENT)
    public static class MessageHandlerSwapPlayerClient implements IMessageHandler<MessageSwapPlayer, IMessage> {
        @Nullable
        @Override
        public IMessage onMessage(MessageSwapPlayer message, MessageContext ctx) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);

            if (entity instanceof EntityPlayer) {
                message.armor.swap((EntityPlayer) entity);

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
