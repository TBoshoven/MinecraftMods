package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoorway;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

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

    private static final AxisAlignedBB BOUNDING_BOX_PILLAR_NW = new AxisAlignedBB(0, 0, 0.9375, 0.0625, 1, 1);
    private static final AxisAlignedBB BOUNDING_BOX_PILLAR_NE = new AxisAlignedBB(0.9375, 0, 0, 1, 1, 0.0625);
    private static final AxisAlignedBB BOUNDING_BOX_PILLAR_SW = new AxisAlignedBB(0, 0, 0, 0.0625, 1, 0.0625);
    private static final AxisAlignedBB BOUNDING_BOX_PILLAR_SE = new AxisAlignedBB(0.9375, 0, 0.9375, 1, 1, 1);

    private static final AxisAlignedBB BOUNDING_BOX_WALL_S = new AxisAlignedBB(0, 0, 0, 1, 1, 0.0625);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_N = new AxisAlignedBB(0, 0, 0.9375, 1, 1, 1);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_E = new AxisAlignedBB(0, 0, 0, 0.0625, 1, 1);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_W = new AxisAlignedBB(0.9375, 0, 0, 1, 1, 1);

    private static final AxisAlignedBB BOUNDING_BOX_TOP = new AxisAlignedBB(0, 0.9375, 0, 1, 1, 1);

    /**
     * Create a new Magic Doorway block.
     * This is typically not necessary. Use Blocks.blockMagicDoorway instead.
     */
    BlockMagicDoorway() {
        // By default, the doorway is not open in any direction
        setDefaultState(
                blockState.getBaseState()
                        .with(PART, EnumPartType.BOTTOM)
                        .with(OPEN_EAST_WEST, Boolean.TRUE)
                        .with(OPEN_NORTH_SOUTH, Boolean.FALSE)
        );
    }

    /**
     * @param state The blockstate of the doorway
     * @return A list of all bounding boxes for collision purposes
     */
    private static List<AxisAlignedBB> getCollisionBoxes(BlockState state) {
        boolean openNorthSouth = state.get(OPEN_NORTH_SOUTH);
        boolean openEastWest = state.get(OPEN_EAST_WEST);
        boolean isTop = state.get(PART) == EnumPartType.TOP;
        List<AxisAlignedBB> result = Lists.newArrayList();
        if (openNorthSouth && openEastWest) {
            Collections.addAll(result, BOUNDING_BOX_PILLAR_NE, BOUNDING_BOX_PILLAR_NW, BOUNDING_BOX_PILLAR_SE, BOUNDING_BOX_PILLAR_SW);
        } else {
            if (!openNorthSouth) {
                Collections.addAll(result, BOUNDING_BOX_WALL_N, BOUNDING_BOX_WALL_S);
            }
            if (!openEastWest) {
                Collections.addAll(result, BOUNDING_BOX_WALL_E, BOUNDING_BOX_WALL_W);
            }
        }
        if (isTop) {
            result.add(BOUNDING_BOX_TOP);
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
    public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        for (AxisAlignedBB collisionBox : getCollisionBoxes(state)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, collisionBox);
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IEnviromentBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
        switch (face) {
            case DOWN:
                return BlockFaceShape.UNDEFINED;
            case UP:
                return isTopSolid(state) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
            case NORTH:
            case SOUTH:
                return state.get(OPEN_NORTH_SOUTH) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
            case WEST:
            case EAST:
                return state.get(OPEN_EAST_WEST) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
        }
        return super.getBlockFaceShape(worldIn, state, pos, face);
    }

    @Override
    public boolean isTopSolid(BlockState state) {
        return state.get(PART) == EnumPartType.TOP;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this)
                .add(PART)
                .add(OPEN_NORTH_SOUTH)
                .add(OPEN_EAST_WEST)
                .add(TEXTURE_MAIN)
                .add(TEXTURE_HIGHLIGHT)
                .build();
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.get(PART).get()
                | (state.get(OPEN_NORTH_SOUTH) ? 1 : 0) << 1
                | (state.get(OPEN_EAST_WEST) ? 1 : 0) << 2;
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .with(PART, (meta & 1) == 0 ? EnumPartType.BOTTOM : EnumPartType.TOP)
                .with(OPEN_NORTH_SOUTH, (meta & (1 << 1)) != 0)
                .with(OPEN_EAST_WEST, (meta & (1 << 2)) != 0);
    }

    @Override
    public BlockState getActualState(BlockState state, IEnviromentBlockReader worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, BlockState state) {
        return new TileEntityMagicDoorway();
    }

    @Override
    @Nullable
    public RayTraceResult collisionRayTrace(BlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        // Manually override collisions so we can break things from within this block
        List<RayTraceResult> rayTraceResults = Lists.newArrayList();

        for (AxisAlignedBB collisionBox : getCollisionBoxes(blockState)) {
            rayTraceResults.add(rayTrace(pos, start, end, collisionBox));
        }

        RayTraceResult result = null;
        double longest = 0.0D;

        for (RayTraceResult rayTraceResult : rayTraceResults) {
            if (rayTraceResult != null) {
                double distance = rayTraceResult.hitVec.squareDistanceTo(end);

                if (distance > longest) {
                    result = rayTraceResult;
                    longest = distance;
                }
            }
        }

        return result;
    }
}
