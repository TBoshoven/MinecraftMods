package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorCoreTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorPartTileEntity;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorBlock extends HorizontalBlock {
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
    private static final VoxelShape[] SHAPES = {
            // South
            makeCuboidShape(0, 0, 0, 16, 16, 2),
            // West
            makeCuboidShape(14, 0, 0, 16, 16, 16),
            // North
            makeCuboidShape(0, 0, 14, 16, 16, 16),
            // East
            makeCuboidShape(0, 0, 0, 2, 16, 16),
    };

    /**
     * Create a new Magic Mirror block.
     */
    MagicMirrorBlock(Block.Properties properties) {
        super(properties);

        // By default, we're the bottom part of a broken mirror
        setDefaultState(
                stateContainer.getBaseState()
                        .with(COMPLETE, Boolean.FALSE)
                        .with(PART, EnumPartType.BOTTOM)
        );
    }

    /**
     * Attach a modifier to the mirror at the specified position (server version).
     * This sends a message to clients as well.
     *
     * @param worldIn  The world containing the mirror.
     * @param pos      The position of the mirror in the world.
     * @param heldItem The item used to attach the modifier to the mirror.
     * @param modifier The modifier to attach to the mirror.
     */
    private static void attachModifier(World worldIn, BlockPos pos, ItemStack heldItem, MagicMirrorModifier modifier) {
        modifier.apply(worldIn, pos, heldItem);
        MessageAttachModifier message = new MessageAttachModifier(pos, heldItem, modifier);
        PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_CHUNK.with(() -> worldIn.getChunkAt(pos));
        Network.CHANNEL.send(target, message);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, COMPLETE, PART);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.get(HORIZONTAL_FACING).getHorizontalIndex()];
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
            return new MagicMirrorCoreTileEntity();
        }
        return new MagicMirrorPartTileEntity();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        // Make sure the mirror is facing the right way when placed
        Direction horizontalDirection = Direction.NORTH;
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                horizontalDirection = direction;
                break;
            }
        }
        return getDefaultState().with(HORIZONTAL_FACING, horizontalDirection.getOpposite());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        // The mirror will only do anything if it's used from the front.
        if (state.get(HORIZONTAL_FACING) == hit.getFace()) {
            if (!worldIn.isRemote) {
                // First, see if we can add a modifier
                ItemStack heldItem = player.getHeldItem(hand);
                if (!heldItem.isEmpty()) {
                    for (MagicMirrorModifier modifier : MagicMirrorModifier.getModifiers()) {
                        if (modifier.canModify(worldIn, pos, heldItem)) {
                            attachModifier(worldIn, pos, heldItem, modifier);
                            return ActionResultType.SUCCESS;
                        }
                    }
                }

                // Then, see if any existing modifier can do something.
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (tileEntity instanceof MagicMirrorBaseTileEntity) {
                    if (((MagicMirrorBaseTileEntity) tileEntity).tryActivate(player, hand)) {
                        return ActionResultType.SUCCESS;
                    }
                }
            }
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.get(COMPLETE)) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof MagicMirrorBaseTileEntity) {
                ((MagicMirrorBaseTileEntity) tileEntity).removeModifiers(worldIn, pos);
            }

            // Change the other part to incomplete
            BlockPos otherPos = state.get(PART) == EnumPartType.TOP ? pos.down() : pos.up();
            BlockState otherState = worldIn.getBlockState(otherPos);
            if (otherState.getBlock() == this) {
                worldIn.setBlockState(otherPos, otherState.with(COMPLETE, false));
            }
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    /**
     * The mirror has two parts: top and bottom.
     */
    public enum EnumPartType implements IStringSerializable {
        TOP("top"),
        BOTTOM("bottom"),
        ;

        private final String name;

        /**
         * @param name The name of the part.
         */
        EnumPartType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
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
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ClientWorld world = Minecraft.getInstance().world;
            TileEntity te = world.getTileEntity(message.mirrorPos);
            if (te instanceof MagicMirrorBaseTileEntity) {
                MagicMirrorModifier modifier = MagicMirrorModifier.getModifier(message.modifierName);
                if (modifier == null) {
                    MagicMirrorMod.LOGGER.error("Received a request to add modifier \"{}\" which does not exist.", message.modifierName);
                    return;
                }
                modifier.apply(world, message.mirrorPos, message.usedItemStack);
                world.playSound(message.mirrorPos, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.BLOCKS, .6f, .6f, true);
            }
        }));
        ctx.setPacketHandled(true);
    }
}
