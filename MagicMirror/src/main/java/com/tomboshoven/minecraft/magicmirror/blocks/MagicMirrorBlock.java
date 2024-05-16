package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorBaseBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorPartBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifiers;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class MagicMirrorBlock extends HorizontalBlock {
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
    private static void attachModifier(World worldIn, BlockPos pos, ItemStack heldItem, MagicMirrorModifier modifier) {
        // Item stack may change by attaching
        ItemStack originalHeldItem = heldItem.copy();
        modifier.apply(worldIn, pos, heldItem);
        ResourceLocation key = modifier.getRegistryName();
        if (key != null) {
            MessageAttachModifier message = new MessageAttachModifier(pos, originalHeldItem, key);
            PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_CHUNK.with(() -> worldIn.getChunkAt(pos));
            Network.CHANNEL.send(target, message);
        }
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, COMPLETE, PART);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isEntityBlock() {
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(COMPLETE);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        // The bottom part is the core of the mirror which has all the logic; the top part just uses the results.
        if (state.getValue(PART) == EnumPartType.BOTTOM) {
            return new MagicMirrorCoreBlockEntity();
        }
        return new MagicMirrorPartBlockEntity();
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
        return defaultBlockState().setValue(FACING, horizontalDirection.getOpposite());
    }

    @Override
    public boolean use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        // The mirror will only do anything if it's used from the front.
        if (state.getValue(COMPLETE)) {
            if (state.getValue(FACING) == hit.getDirection()) {
                if (!worldIn.isClientSide) {
                    // First, see if we can add a modifier
                    ItemStack heldItem = player.getItemInHand(handIn);
                    if (!heldItem.isEmpty()) {
                        Optional<MagicMirrorModifier> modifier = MagicMirrorModifiers.MAGIC_MIRROR_MODIFIER_REGISTRY.getValues().stream().filter(m -> m.canModify(worldIn, pos, heldItem)).findFirst();

                        if (modifier.isPresent()) {
                            attachModifier(worldIn, pos, heldItem, modifier.get());
                            return true;
                        }
                    }

                    // Then, see if any existing modifier can do something.
                    TileEntity tileEntity = worldIn.getBlockEntity(pos);
                    if (tileEntity instanceof MagicMirrorBaseBlockEntity) {
                        if (((MagicMirrorBaseBlockEntity) tileEntity).tryActivate(player, handIn)) {
                            return true;
                        }
                    }
                }
                return true;
            }
        }
        else {
            ItemStack heldItemStack = player.getItemInHand(handIn);
            Item heldItem = heldItemStack.getItem();

            // Add the second part if possible
            if (!heldItemStack.isEmpty() && heldItem == Items.MAGIC_MIRROR.get()) {
                Direction ownDirection = state.getValue(FACING);

                if (hit.getDirection() == ownDirection) {
                    // Attempt to place above first, then below.
                    for (Direction direction : new Direction[]{Direction.UP, Direction.DOWN}) {
                        BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, handIn, new BlockRayTraceResult(hit.getLocation(), direction, pos, false)));
                        // Only do this if the block can be replaced it wouldn't result in the new part being turned around
                        if (Arrays.stream(ctx.getNearestLookingDirections()).filter(d -> d.getAxis().isHorizontal()).findFirst().orElse(ownDirection) == ownDirection.getOpposite()) {
                            if (heldItem instanceof BlockItem) {
                                if (((BlockItem) heldItem).place(ctx) == ActionResultType.SUCCESS) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getValue(COMPLETE)) {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof MagicMirrorBaseBlockEntity) {
                ((MagicMirrorBaseBlockEntity) tileEntity).removeModifiers(worldIn, pos);
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
        ResourceLocation modifier;

        @SuppressWarnings({"unused", "WeakerAccess"})
        public MessageAttachModifier() {
        }

        /**
         * @param mirrorPos     The position of the mirror in the world.
         * @param usedItemStack The item used to attach the modifier to the mirror.
         * @param modifier      The modifier this is being attached.
         */
        MessageAttachModifier(BlockPos mirrorPos, ItemStack usedItemStack, ResourceLocation modifier) {
            this.mirrorPos = mirrorPos;
            this.usedItemStack = usedItemStack;
            this.modifier = modifier;
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
            result.usedItemStack = buf.readItem();
            result.modifier = buf.readResourceLocation();
            return result;
        }


        /**
         * Encode the message into a packet buffer.
         *
         * @param buf The buffer to write to.
         */
        public void encode(PacketBuffer buf) {
            buf.writeBlockPos(mirrorPos);
            buf.writeItem(usedItemStack);
            buf.writeResourceLocation(modifier);
        }
    }

    /**
     * Handler for messages describing modifiers being attached to mirrors.
     */
    public static void onMessageAttachModifier(MessageAttachModifier message, Supplier<? extends NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().level;
            TileEntity te = world.getBlockEntity(message.mirrorPos);
            if (te instanceof MagicMirrorBaseBlockEntity) {
                MagicMirrorModifier modifier = MagicMirrorModifiers.MAGIC_MIRROR_MODIFIER_REGISTRY.getValue(message.modifier);
                if (modifier == null) {
                    MagicMirrorMod.LOGGER.error("Received a request to add modifier \"{}\" which does not exist.", message.modifier);
                    return;
                }
                modifier.apply(world, message.mirrorPos, message.usedItemStack);
                world.playLocalSound(message.mirrorPos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundCategory.BLOCKS, .6f, .6f, true);
            }
        });
        ctx.setPacketHandled(true);
    }
}
