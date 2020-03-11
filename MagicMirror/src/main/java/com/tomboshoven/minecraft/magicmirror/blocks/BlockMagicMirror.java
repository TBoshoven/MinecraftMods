package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorBase;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorCore;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorPart;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockMagicMirror extends HorizontalBlock {
    /**
     * Property describing whether the mirror is completely constructed.
     */
    public static final BooleanProperty COMPLETE = BooleanProperty.create("complete");

    /**
     * Property describing which part of the mirror is being represented by this block.
     */
    public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);

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
     * Create a new Magic Mirror block.
     */
    BlockMagicMirror(Block.Properties properties) {
        super(properties);

        // By default, we're the bottom part of a broken mirror
        setDefaultState(
                stateContainer.getBaseState()
                        .with(COMPLETE, Boolean.FALSE)
                        .with(PART, EnumPartType.BOTTOM)
        );
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
            MessageAttachModifier message = new MessageAttachModifier(pos, heldItem, modifier);
            PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_CHUNK.with(() -> worldIn.getChunkAt(pos));
            Network.CHANNEL.send(target, message);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        // Try to complete a mirror by looking for an incomplete mirror above or below.
        BlockState blockBelow = worldIn.getBlockState(pos.down());
        BlockState blockAbove = worldIn.getBlockState(pos.up());
        if (blockBelow.getBlock() == this && !blockBelow.get(COMPLETE) && blockBelow.get(HORIZONTAL_FACING) == state.get(HORIZONTAL_FACING)) {
            worldIn.setBlockState(pos.down(), blockBelow.with(COMPLETE, true).with(PART, EnumPartType.BOTTOM));
            worldIn.setBlockState(pos, state.with(COMPLETE, true).with(PART, EnumPartType.TOP));
        } else if (blockAbove.getBlock() == this && !blockAbove.get(COMPLETE) && blockAbove.get(HORIZONTAL_FACING) == state.get(HORIZONTAL_FACING)) {
            worldIn.setBlockState(pos.up(), blockAbove.with(COMPLETE, true).with(PART, EnumPartType.TOP));
            worldIn.setBlockState(pos, state.with(COMPLETE, true).with(PART, EnumPartType.BOTTOM));
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        // Break the mirror if the other part is broken.
        if (state.get(COMPLETE)) {
            if (
                    state.get(PART) == EnumPartType.TOP && worldIn.getBlockState(pos.down()).getBlock() != this ||
                            state.get(PART) == EnumPartType.BOTTOM && worldIn.getBlockState(pos.up()).getBlock() != this
            ) {
                worldIn.setBlockState(pos, state.with(COMPLETE, false));
            }
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, COMPLETE, PART);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IEnviromentBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
        // Only the opposite face is default
        return state.get(HORIZONTAL_FACING).getOpposite() == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IEnviromentBlockReader source, BlockPos pos) {
        return BOUNDING_BOX[state.get(HORIZONTAL_FACING).getHorizontalIndex()];
    }

    @Override
    public EnumBlockRenderType getRenderType(BlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.get(COMPLETE);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        // The bottom part is the core of the mirror which has all the logic; the top part just uses the results.
        if (state.get(PART) == EnumPartType.BOTTOM) {
            return new TileEntityMagicMirrorCore();
        }
        return new TileEntityMagicMirrorPart();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
    }

    @Override
    public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        // Make sure the mirror is facing the right way when placed
        return getDefaultState().with(HORIZONTAL_FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction direction, float hitX, float hitY, float hitZ) {
        // The mirror will only do anything if it's used from the front.
        if (state.get(HORIZONTAL_FACING) == direction) {
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
    public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
        if (state.get(COMPLETE)) {
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
    public static class MessageAttachModifier {
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

        /**
         * Decode a packet into an instance of the message.
         *
         * @param buf The buffer to read from.
         *
         * @return The created message instance.
         */
        public static MessageAttachModifier decode(PacketBuffer buf) {
            MessageAttachModifier result = new MessageAttachModifier();
            result.mirrorPos = buf.readBlockPos();
            result.usedItemStack = buf.readItemStack();
            result.modifierName = buf.readString();
            return result;
        }


        /**
         * Encode the message into a packet buffer.
         *
         * @param buf The buffer to write to.
         */
        public void encode(PacketBuffer buf) {
            buf.writeBlockPos(mirrorPos);
            buf.writeItemStack(usedItemStack);
            buf.writeString(modifierName);
        }
    }

    /**
     * Handler for messages describing modifiers being attached to mirrors.
     */
    public static void onMessageAttachModifier(MessageAttachModifier message, Supplier<NetworkEvent.Context> contextSupplier) {
        ClientWorld world = Minecraft.getInstance().world;
        TileEntity te = world.getTileEntity(message.mirrorPos);
        if (te instanceof TileEntityMagicMirrorBase) {
            MagicMirrorModifier modifier = MagicMirrorModifier.getModifier(message.modifierName);
            if (modifier == null) {
                ModMagicMirror.LOGGER.error("Received a request to add modifier \"{}\" which does not exist.", message.modifierName);
            }
            attachModifier(world, message.mirrorPos, message.usedItemStack, modifier);
        }
    }
}
