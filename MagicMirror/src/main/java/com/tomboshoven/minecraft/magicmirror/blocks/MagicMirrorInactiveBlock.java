package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.items.Items;
import net.minecraft.MethodsReturnNonnullByDefault;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorInactiveBlock extends MagicMirrorBaseBlock {

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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(PART, EnumPartType.BOTTOM);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack heldItemStack = player.getItemInHand(handIn);
        Item heldItem = heldItemStack.getItem();

        // Add the second part if possible
        if (!heldItemStack.isEmpty() && heldItem == Items.MAGIC_MIRROR.get()) {
            Direction ownDirection = state.getValue(FACING);

            if (hit.getDirection() == ownDirection) {
                // Attempt to place above first, then below.
                for (Direction direction : new Direction[]{Direction.UP, Direction.DOWN}) {
                    BlockPlaceContext ctx = new BlockPlaceContext(worldIn, player, handIn, heldItemStack, new BlockHitResult(hit.getLocation(), direction, pos, false));
                    // Only do this if the block can be replaced it wouldn't result in the new part being turned around
                    if (Arrays.stream(ctx.getNearestLookingDirections()).filter(d -> d.getAxis().isHorizontal()).findFirst().orElse(ownDirection) == ownDirection.getOpposite()) {
                        if (heldItem instanceof BlockItem) {
                            InteractionResult result = ((BlockItem) heldItem).place(ctx);
                            if (result.consumesAction()) {
                                return result;
                            }
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}
