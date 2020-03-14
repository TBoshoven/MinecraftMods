package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoorway;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Part of a magic doorway.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockMagicDoorway extends BlockMagicDoorwayPartBase {
    /**
     * Property describing which part of the doorway is being represented by this block.
     */
    public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);

    /**
     * Property describing whether the block forms a doorway between north and south.
     */
    public static final BooleanProperty OPEN_NORTH_SOUTH = BooleanProperty.create("open_north_south");

    /**
     * Property describing whether the block forms a doorway between east and west.
     */
    public static final BooleanProperty OPEN_EAST_WEST = BooleanProperty.create("open_east_west");

    private static final VoxelShape BOUNDING_BOX_PILLAR_NW = makeCuboidShape(0, 0, 0.9375, 0.0625, 1, 1);
    private static final VoxelShape BOUNDING_BOX_PILLAR_NE = makeCuboidShape(0.9375, 0, 0, 1, 1, 0.0625);
    private static final VoxelShape BOUNDING_BOX_PILLAR_SW = makeCuboidShape(0, 0, 0, 0.0625, 1, 0.0625);
    private static final VoxelShape BOUNDING_BOX_PILLAR_SE = makeCuboidShape(0.9375, 0, 0.9375, 1, 1, 1);

    private static final VoxelShape BOUNDING_BOX_WALL_S = makeCuboidShape(0, 0, 0, 1, 1, 0.0625);
    private static final VoxelShape BOUNDING_BOX_WALL_N = makeCuboidShape(0, 0, 0.9375, 1, 1, 1);
    private static final VoxelShape BOUNDING_BOX_WALL_E = makeCuboidShape(0, 0, 0, 0.0625, 1, 1);
    private static final VoxelShape BOUNDING_BOX_WALL_W = makeCuboidShape(0.9375, 0, 0, 1, 1, 1);

    private static final VoxelShape BOUNDING_BOX_TOP = makeCuboidShape(0, 0.9375, 0, 1, 1, 1);

    /**
     * Create a new Magic Doorway block.
     */
    BlockMagicDoorway(Block.Properties properties) {
        super(properties);

        // By default, the doorway is not open in any direction
        setDefaultState(
                stateContainer.getBaseState()
                        .with(PART, EnumPartType.BOTTOM)
                        .with(OPEN_EAST_WEST, Boolean.TRUE)
                        .with(OPEN_NORTH_SOUTH, Boolean.FALSE)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        boolean openNorthSouth = state.get(OPEN_NORTH_SOUTH);
        boolean openEastWest = state.get(OPEN_EAST_WEST);
        boolean isTop = state.get(PART) == EnumPartType.TOP;
        VoxelShape result = VoxelShapes.empty();
        if (openNorthSouth && openEastWest) {
            result = VoxelShapes.or(result, BOUNDING_BOX_PILLAR_NE, BOUNDING_BOX_PILLAR_NW, BOUNDING_BOX_PILLAR_SE, BOUNDING_BOX_PILLAR_SW);
        } else {
            if (!openNorthSouth) {
                result = VoxelShapes.or(result, BOUNDING_BOX_WALL_N, BOUNDING_BOX_WALL_S);
            }
            if (!openEastWest) {
                result = VoxelShapes.or(result, BOUNDING_BOX_WALL_E, BOUNDING_BOX_WALL_W);
            }
        }
        if (isTop) {
            result = VoxelShapes.or(result, BOUNDING_BOX_TOP);
        }
        return result;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
        // When this block is destroyed (manually or by closing the door), replace it by its base block.
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoorway) {
            worldIn.setBlockState(pos, ((TileEntityMagicDoorway) tileEntity).getBaseBlockState());
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PART, OPEN_NORTH_SOUTH, OPEN_EAST_WEST);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityMagicDoorway();
    }
}
