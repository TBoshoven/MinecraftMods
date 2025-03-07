package com.tomboshoven.minecraft.magicmirror.blocks;

import com.mojang.serialization.MapCodec;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Arrays;

public class MagicMirrorInactiveBlock extends MagicMirrorBaseBlock {
    private static final MapCodec<MagicMirrorCoreBlock> CODEC = simpleCodec(MagicMirrorCoreBlock::new);

    /**
     * Property describing which part of the mirror is being represented by this block.
     */
    public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);

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
     * Create a new Magic Mirror block.
     */
    MagicMirrorInactiveBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(PART, EnumPartType.BOTTOM);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Item item = itemStack.getItem();

        // Add the second part if possible
        if (item == Items.MAGIC_MIRROR.get()) {
            Direction ownDirection = state.getValue(FACING);

            if (hit.getDirection() == ownDirection) {
                // Attempt to place above first, then below.
                for (Direction direction : new Direction[]{Direction.UP, Direction.DOWN}) {
                    BlockPlaceContext ctx = new BlockPlaceContext(level, player, hand, itemStack, new BlockHitResult(hit.getLocation(), direction, pos, false));
                    // Only do this if the block can be replaced it wouldn't result in the new part being turned around
                    if (Arrays.stream(ctx.getNearestLookingDirections()).filter(d -> d.getAxis().isHorizontal()).findFirst().orElse(ownDirection) == ownDirection.getOpposite()) {
                        if (item instanceof BlockItem blockItem) {
                            return blockItem.place(ctx);
                        }
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }
}
