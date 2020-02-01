package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoorway;
import com.tomboshoven.minecraft.magicdoorknob.properties.PropertyTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockMagicDoorway extends Block {
    /**
     * Property describing which part of the doorway is being represented by this block.
     */
    public static final PropertyEnum<EnumPartType> PART = PropertyEnum.create("part", EnumPartType.class);

    /**
     * Property describing whether the block forms a doorway between north and south.
     */
    public static final PropertyBool OPEN_NORTH_SOUTH = PropertyBool.create("open_north_south");
    /**
     * Property describing whether the block forms a doorway between east and west.
     */
    public static final PropertyBool OPEN_EAST_WEST = PropertyBool.create("open_east_west");

    private static final PropertyTexture TEXTURE_MAIN = new PropertyTexture("texture_main");
    private static final PropertyTexture TEXTURE_HIGHLIGHT = new PropertyTexture("texture_highlight");

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
        super(new MaterialTransparent(MapColor.AIR));

        // By default, the doorway is not open in any direction
        setDefaultState(
                blockState.getBaseState()
                        .withProperty(PART, EnumPartType.BOTTOM)
                        .withProperty(OPEN_EAST_WEST, Boolean.TRUE)
                        .withProperty(OPEN_NORTH_SOUTH, Boolean.FALSE)
        );
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoorway) {
            return ((TileEntityMagicDoorway) tileEntity).getReplacedBlock().getLightValue(world, pos);
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoorway) {
            return ((TileEntityMagicDoorway) tileEntity).getReplacedBlock().getLightOpacity(world, pos);
        }
        return super.getLightOpacity(state, world, pos);
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoorway) {
            return ((TileEntityMagicDoorway) tileEntity).getReplacedBlock().getBlockHardness(worldIn, pos);
        }
        return super.getBlockHardness(blockState, worldIn, pos);
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return -1;
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return null;
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoorway) {
            IBlockState replacedBlock = ((TileEntityMagicDoorway) tileEntity).getReplacedBlock();
            return replacedBlock.getBlock().getSoundType(replacedBlock, world, pos, entity);
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoorway) {
            worldIn.setBlockState(pos, ((TileEntityMagicDoorway) tileEntity).getReplacedBlock());
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        for (AxisAlignedBB collisionBox : getCollisionBoxes(state)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, collisionBox);
        }
    }

    private static List<AxisAlignedBB> getCollisionBoxes(IBlockState state) {
        boolean openNorthSouth = state.getValue(OPEN_NORTH_SOUTH);
        boolean openEastWest = state.getValue(OPEN_EAST_WEST);
        boolean isTop = state.getValue(PART) == EnumPartType.TOP;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        switch (face) {
            case DOWN:
                return BlockFaceShape.UNDEFINED;
            case UP:
                return isTopSolid(state) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
            case NORTH:
            case SOUTH:
                return state.getValue(OPEN_NORTH_SOUTH) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
            case WEST:
            case EAST:
                return state.getValue(OPEN_EAST_WEST) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
        }
        return super.getBlockFaceShape(worldIn, state, pos, face);
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return state.getValue(PART) == EnumPartType.TOP;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoorway) {
            IBlockState replacedBlock = ((TileEntityMagicDoorway) tileEntity).getReplacedBlock();
            // Try to get the block's texture
            TextureAtlasSprite texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(replacedBlock);
            if ("missingno".equals(texture.getIconName())) {
                texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getTextureMap().getAtlasSprite("magic_doorknob:blocks/empty");
            }
            return ((IExtendedBlockState) state)
                    .withProperty(TEXTURE_MAIN, new ResourceLocation(texture.getIconName()))
                    .withProperty(TEXTURE_HIGHLIGHT, new ResourceLocation("minecraft:blocks/redstone_block"));
        }
        return state;
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
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PART).getValue()
                | (state.getValue(OPEN_NORTH_SOUTH) ? 1 : 0) << 1
                | (state.getValue(OPEN_EAST_WEST) ? 1 : 0) << 2;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(PART, (meta & 1) == 0 ? EnumPartType.BOTTOM : EnumPartType.TOP)
                .withProperty(OPEN_NORTH_SOUTH, (meta & (1 << 1)) != 0)
                .withProperty(OPEN_EAST_WEST, (meta & (1 << 2)) != 0);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMagicDoorway();
    }

    @Nullable
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
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

    /**
     * The doorway has two parts: top and bottom.
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
}
