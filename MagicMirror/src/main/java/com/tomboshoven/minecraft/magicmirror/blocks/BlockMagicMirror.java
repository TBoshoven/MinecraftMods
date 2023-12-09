package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorBase;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorCore;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorPart;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockMagicMirror extends BlockHorizontal {
    /**
     * Property describing whether the mirror is completely constructed.
     */
    public static final PropertyBool COMPLETE = PropertyBool.create("complete");

    /**
     * Property describing which part of the mirror is being represented by this block.
     */
    public static final PropertyEnum<EnumPartType> PART = PropertyEnum.create("part", EnumPartType.class);

    /**
     * The bounding boxes of the various orientations of this block; should be indexed by facing.horizontalIndex()
     */
    private static final AxisAlignedBB[] BOUNDING_BOX = {
            // South
            new AxisAlignedBB(0, 0, 0, 1, 1, 0.125),
            // West
            new AxisAlignedBB(0.875, 0, 0, 1, 1, 1),
            // North
            new AxisAlignedBB(0, 0, 0.875, 1, 1, 1),
            // East
            new AxisAlignedBB(0, 0, 0, 0.125, 1, 1),
    };

    /**
     * Handler for messages describing modifiers being attached to mirrors.
     */
    @SuppressWarnings("PublicField")
    @SidedProxy(
            clientSide = "com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror$MessageHandlerAttachModifierClient",
            serverSide = "com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror$MessageHandlerAttachModifierServer"
    )
    public static IMessageHandler<MessageAttachModifier, IMessage> messageHandlerAttachModifier;

    /**
     * Create a new Magic Mirror block.
     * This is typically not necessary. Use Blocks.blockMagicMirror instead.
     */
    BlockMagicMirror() {
        super(new Material(MapColor.GRAY));

        // By default, we're the bottom part of a broken mirror
        setDefaultState(
                blockState.getBaseState()
                        .withProperty(COMPLETE, Boolean.FALSE)
                        .withProperty(PART, EnumPartType.BOTTOM)
        );

        setHardness(.8f);
        setSoundType(SoundType.GLASS);
    }

    /**
     * Attach a modifier to the mirror at the specified position.
     *
     * @param worldIn  The world containing the mirror.
     * @param pos      The position of the mirror in the world.
     * @param heldItem The item used to attach the modifier to the mirror.
     * @param modifier The modifier to attach to the mirror.
     */
    private static void attachModifier(World worldIn, BlockPos pos, ItemStack heldItem, MagicMirrorModifier modifier) {
        modifier.apply(worldIn, pos, heldItem);
        if (worldIn.isRemote) {
            worldIn.playSound(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.BLOCKS, .6f, .6f, true);
        } else {
            IMessage mirrorMessage = new MessageAttachModifier(pos, heldItem, modifier);
            Network.sendToAllTracking(mirrorMessage, worldIn, pos);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        // Try to complete a mirror by looking for an incomplete mirror above or below.
        IBlockState blockBelow = worldIn.getBlockState(pos.down());
        IBlockState blockAbove = worldIn.getBlockState(pos.up());
        if (blockBelow.getBlock() == this && !blockBelow.getValue(COMPLETE) && blockBelow.getValue(FACING) == state.getValue(FACING)) {
            worldIn.setBlockState(pos.down(), blockBelow.withProperty(COMPLETE, true).withProperty(PART, EnumPartType.BOTTOM));
            worldIn.setBlockState(pos, state.withProperty(COMPLETE, true).withProperty(PART, EnumPartType.TOP));
        } else if (blockAbove.getBlock() == this && !blockAbove.getValue(COMPLETE) && blockAbove.getValue(FACING) == state.getValue(FACING)) {
            worldIn.setBlockState(pos.up(), blockAbove.withProperty(COMPLETE, true).withProperty(PART, EnumPartType.TOP));
            worldIn.setBlockState(pos, state.withProperty(COMPLETE, true).withProperty(PART, EnumPartType.BOTTOM));
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        // Break the mirror if the other part is broken.
        if (state.getValue(COMPLETE)) {
            if (
                    state.getValue(PART) == EnumPartType.TOP && worldIn.getBlockState(pos.down()).getBlock() != this ||
                            state.getValue(PART) == EnumPartType.BOTTOM && worldIn.getBlockState(pos.up()).getBlock() != this
            ) {
                worldIn.setBlockState(pos, state.withProperty(COMPLETE, false));
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, COMPLETE, PART);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex()
                | (state.getValue(COMPLETE) ? 1 : 0) << 2
                | state.getValue(PART).getValue() << 3;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
                .withProperty(COMPLETE, (meta & 1 << 2) != 0)
                .withProperty(PART, (meta & 1 << 3) == 0 ? EnumPartType.BOTTOM : EnumPartType.TOP);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        // Only the opposite face is default
        return state.getValue(FACING).getOpposite() == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX[state.getValue(FACING).getHorizontalIndex()];
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(COMPLETE);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        // The bottom part is the core of the mirror which has all the logic; the top part just uses the results.
        if (state.getValue(PART) == EnumPartType.BOTTOM) {
            return new TileEntityMagicMirrorCore();
        }
        return new TileEntityMagicMirrorPart();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        // Make sure the mirror is facing the right way when placed
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // The mirror will only do anything if it's used from the front.
        if (state.getValue(FACING) == facing) {
            if (!worldIn.isRemote) {
                // First, see if we can add a modifier
                ItemStack heldItem = playerIn.getHeldItem(hand);
                if (!heldItem.isEmpty()) {
                    for (MagicMirrorModifier modifier : MagicMirrorModifier.getModifiers()) {
                        if (modifier.canModify(worldIn, pos, heldItem)) {
                            attachModifier(worldIn, pos, heldItem, modifier);
                            return true;
                        }
                    }
                }

                // Then, see if any existing modifier can do something.
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (tileEntity instanceof TileEntityMagicMirrorBase) {
                    if (((TileEntityMagicMirrorBase) tileEntity).tryActivate(playerIn, hand)) {
                        return true;
                    }
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getValue(COMPLETE)) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityMagicMirrorBase) {
                ((TileEntityMagicMirrorBase) tileEntity).removeModifiers(worldIn, pos);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    /**
     * The mirror has two parts: top and bottom.
     */
    public enum EnumPartType implements IStringSerializable {
        TOP("top", 0),
        BOTTOM("bottom", 1),
        ;

        private final String name;
        private final int value;

        /**
         * @param name  The name of the part.
         * @param value The integer value of the part; used for setting block metadata.
         */
        EnumPartType(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        /**
         * @return The integer value of the part; used for setting block metadata.
         */
        int getValue() {
            return value;
        }
    }

    /**
     * Message describing the action of attaching a new modifier to a mirror.
     */
    public static class MessageAttachModifier implements IMessage {
        /**
         * The position of the mirror in the world.
         */
        BlockPos mirrorPos;
        /**
         * The item used to attach the modifier to the mirror.
         */
        ItemStack usedItemStack;
        /**
         * The name of the modifier this is being attached.
         */
        String modifierName;

        @SuppressWarnings("unused")
        public MessageAttachModifier() {
        }

        /**
         * @param mirrorPos     The position of the mirror in the world.
         * @param usedItemStack The item used to attach the modifier to the mirror.
         * @param modifier      The modifier this is being attached.
         */
        MessageAttachModifier(BlockPos mirrorPos, ItemStack usedItemStack, MagicMirrorModifier modifier) {
            this.mirrorPos = mirrorPos;
            this.usedItemStack = usedItemStack;
            modifierName = modifier.getName();
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            mirrorPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
            usedItemStack = ByteBufUtils.readItemStack(buf);
            modifierName = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(mirrorPos.getX());
            buf.writeInt(mirrorPos.getY());
            buf.writeInt(mirrorPos.getZ());
            ByteBufUtils.writeItemStack(buf, usedItemStack);
            ByteBufUtils.writeUTF8String(buf, modifierName);
        }
    }

    /**
     * Handler for messages describing modifiers being attached to mirrors (client side).
     */
    @SideOnly(Side.CLIENT)
    public static class MessageHandlerAttachModifierClient implements IMessageHandler<MessageAttachModifier, IMessage> {
        @Nullable
        @Override
        public IMessage onMessage(MessageAttachModifier message, MessageContext ctx) {
            WorldClient world = Minecraft.getMinecraft().world;
            TileEntity te = world.getTileEntity(message.mirrorPos);
            if (te instanceof TileEntityMagicMirrorBase) {
                MagicMirrorModifier modifier = MagicMirrorModifier.getModifier(message.modifierName);
                if (modifier == null) {
                    ModMagicMirror.logger.error("Received a request to add modifier \"{}\" which does not exist.", message.modifierName);
                    return null;
                }
                attachModifier(world, message.mirrorPos, message.usedItemStack, modifier);
            }
            return null;
        }
    }

    /**
     * Handler for messages describing modifiers being attached to mirrors (server side).
     */
    @SideOnly(Side.SERVER)
    public static class MessageHandlerAttachModifierServer implements IMessageHandler<MessageAttachModifier, IMessage> {
        @Nullable
        @Override
        public IMessage onMessage(MessageAttachModifier message, MessageContext ctx) {
            return null;
        }
    }
}
