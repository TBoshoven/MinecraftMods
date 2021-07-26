package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorCoreTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorPartTileEntity;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
public class MagicMirrorBlock extends HorizontalDirectionalBlock {
    /**
     * Property describing whether the mirror is completely constructed.
     */
    private static final BooleanProperty COMPLETE = BooleanProperty.create("complete");

    /**
     * Property describing which part of the mirror is being represented by this block.
     */
    public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);

    /**
     * The bounding boxes of the various orientations of this block; should be indexed by facing.horizontalIndex()
     */
    private static final VoxelShape[] SHAPES = {
            // South
            box(0, 0, 0, 16, 16, 2),
            // West
            box(14, 0, 0, 16, 16, 16),
            // North
            box(0, 0, 14, 16, 16, 16),
            // East
            box(0, 0, 0, 2, 16, 16),
    };

    /**
     * Create a new Magic Mirror block.
     */
    MagicMirrorBlock(Block.Properties properties) {
        super(properties);

        // By default, we're the bottom part of a broken mirror
        registerDefaultState(
                stateDefinition.any()
                        .setValue(COMPLETE, Boolean.FALSE)
                        .setValue(PART, EnumPartType.BOTTOM)
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
    private static void attachModifier(Level worldIn, BlockPos pos, ItemStack heldItem, MagicMirrorModifier modifier) {
        // Item stack may change by attaching
        ItemStack originalHeldItem = heldItem.copy();
        modifier.apply(worldIn, pos, heldItem);
        MessageAttachModifier message = new MessageAttachModifier(pos, originalHeldItem, modifier);
        PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_CHUNK.with(() -> worldIn.getChunkAt(pos));
        Network.CHANNEL.send(target, message);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);

        // Try to complete a mirror by looking for an incomplete mirror above or below.
        BlockState blockBelow = worldIn.getBlockState(pos.below());
        BlockState blockAbove = worldIn.getBlockState(pos.above());
        if (blockBelow.getBlock() == this && !blockBelow.getValue(COMPLETE) && blockBelow.getValue(FACING) == state.getValue(FACING)) {
            worldIn.setBlockAndUpdate(pos.below(), blockBelow.setValue(COMPLETE, true).setValue(PART, EnumPartType.BOTTOM));
            worldIn.setBlockAndUpdate(pos, state.setValue(COMPLETE, true).setValue(PART, EnumPartType.TOP));
        } else if (blockAbove.getBlock() == this && !blockAbove.getValue(COMPLETE) && blockAbove.getValue(FACING) == state.getValue(FACING)) {
            worldIn.setBlockAndUpdate(pos.above(), blockAbove.setValue(COMPLETE, true).setValue(PART, EnumPartType.TOP));
            worldIn.setBlockAndUpdate(pos, state.setValue(COMPLETE, true).setValue(PART, EnumPartType.BOTTOM));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COMPLETE, PART);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(COMPLETE);
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        // The bottom part is the core of the mirror which has all the logic; the top part just uses the results.
        if (state.getValue(PART) == EnumPartType.BOTTOM) {
            return new MagicMirrorCoreTileEntity();
        }
        return new MagicMirrorPartTileEntity();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Make sure the mirror is facing the right way when placed
        Direction horizontalDirection = Direction.NORTH;
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                horizontalDirection = direction;
                break;
            }
        }
        return defaultBlockState().setValue(FACING, horizontalDirection.getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        // The mirror will only do anything if it's used from the front.
        if (state.getValue(FACING) == hit.getDirection()) {
            if (!worldIn.isClientSide) {
                // First, see if we can add a modifier
                ItemStack heldItem = player.getItemInHand(handIn);
                if (!heldItem.isEmpty()) {
                    for (MagicMirrorModifier modifier : MagicMirrorModifier.getModifiers()) {
                        if (modifier.canModify(worldIn, pos, heldItem)) {
                            attachModifier(worldIn, pos, heldItem, modifier);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }

                // Then, see if any existing modifier can do something.
                BlockEntity tileEntity = worldIn.getBlockEntity(pos);
                if (tileEntity instanceof MagicMirrorBaseTileEntity) {
                    if (((MagicMirrorBaseTileEntity) tileEntity).tryActivate(player, handIn)) {
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getValue(COMPLETE)) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof MagicMirrorBaseTileEntity) {
                ((MagicMirrorBaseTileEntity) tileEntity).removeModifiers(worldIn, pos);
            }

            // Change the other part to incomplete
            BlockPos otherPos = state.getValue(PART) == EnumPartType.TOP ? pos.below() : pos.above();
            BlockState otherState = worldIn.getBlockState(otherPos);
            if (otherState.getBlock() == this) {
                worldIn.setBlockAndUpdate(otherPos, otherState.setValue(COMPLETE, false));
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    /**
     * The mirror has two parts: top and bottom.
     */
    public enum EnumPartType implements StringRepresentable {
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
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
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

        @SuppressWarnings({"unused", "WeakerAccess"})
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
        public static MessageAttachModifier decode(FriendlyByteBuf buf) {
            MessageAttachModifier result = new MessageAttachModifier();
            result.mirrorPos = buf.readBlockPos();
            result.usedItemStack = buf.readItem();
            result.modifierName = buf.readUtf();
            return result;
        }


        /**
         * Encode the message into a packet buffer.
         *
         * @param buf The buffer to write to.
         */
        public void encode(FriendlyByteBuf buf) {
            buf.writeBlockPos(mirrorPos);
            buf.writeItem(usedItemStack);
            buf.writeUtf(modifierName);
        }
    }

    /**
     * Handler for messages describing modifiers being attached to mirrors.
     */
    public static void onMessageAttachModifier(MessageAttachModifier message, Supplier<? extends NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                BlockEntity te = world.getBlockEntity(message.mirrorPos);
                if (te instanceof MagicMirrorBaseTileEntity) {
                    MagicMirrorModifier modifier = MagicMirrorModifier.getModifier(message.modifierName);
                    if (modifier == null) {
                        MagicMirrorMod.LOGGER.error("Received a request to add modifier \"{}\" which does not exist.", message.modifierName);
                        return;
                    }
                    modifier.apply(world, message.mirrorPos, message.usedItemStack);
                    world.playLocalSound(message.mirrorPos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, .6f, .6f, true);
                }
            }
        }));
        ctx.setPacketHandled(true);
    }
}
