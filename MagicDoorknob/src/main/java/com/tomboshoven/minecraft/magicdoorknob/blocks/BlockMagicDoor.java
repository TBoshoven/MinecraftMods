package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.properties.PropertyTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public
class BlockMagicDoor extends Block {
    /**
     * Property describing which part of the door is being represented by this block.
     */
    public static final PropertyEnum<EnumPartType> PART = PropertyEnum.create("part", EnumPartType.class);
    public static final PropertyEnum<EnumFacing> FACING = BlockHorizontal.FACING;

    private static final PropertyTexture TEXTURE_MAIN = new PropertyTexture("texture_main");
    private static final PropertyTexture TEXTURE_HIGHLIGHT = new PropertyTexture("texture_highlight");

    private static final AxisAlignedBB BOUNDING_BOX_WALL_S = new AxisAlignedBB(0, 0, 0, 1, 1, 0.0625);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_N = new AxisAlignedBB(0, 0, 0.9375, 1, 1, 1);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_E = new AxisAlignedBB(0, 0, 0, 0.0625, 1, 1);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_W = new AxisAlignedBB(0.9375, 0, 0, 1, 1, 1);

    BlockMagicDoor() {
        super(new MaterialTransparent(MapColor.AIR));
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoor) {
            return ((TileEntityMagicDoor) tileEntity).getTextureBlock().getLightValue(world, pos);
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoor) {
            return ((TileEntityMagicDoor) tileEntity).getTextureBlock().getLightOpacity(world, pos);
        }
        return super.getLightOpacity(state, world, pos);
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoor) {
            return ((TileEntityMagicDoor) tileEntity).getTextureBlock().getBlockHardness(worldIn, pos);
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
        if (tileEntity instanceof TileEntityMagicDoor) {
            IBlockState textureBlock = ((TileEntityMagicDoor) tileEntity).getTextureBlock();
            SoundType actualSoundType = textureBlock.getBlock().getSoundType(textureBlock, world, pos, entity);
            return new SoundType(
                    actualSoundType.volume,
                    actualSoundType.pitch,
                    SoundEvents.BLOCK_WOODEN_DOOR_CLOSE,
                    actualSoundType.getStepSound(),
                    SoundEvents.BLOCK_WOODEN_DOOR_OPEN,
                    actualSoundType.getHitSound(),
                    actualSoundType.getFallSound()
            );
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        EnumPartType part = state.getValue(PART);

        // Break the door if the other part is broken.
        if (
                part == EnumPartType.TOP && worldIn.getBlockState(pos.down()).getBlock() != this ||
                        part == EnumPartType.BOTTOM && worldIn.getBlockState(pos.up()).getBlock() != this
        ) {
            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.itemMagicDoorknob, 1, 0));
            worldIn.destroyBlock(pos, false);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        breakDoorway(worldIn, pos, state.getValue(FACING));
        super.breakBlock(worldIn, pos, state);
    }

    private static void breakDoorway(World world, BlockPos pos, EnumFacing facing) {
        EnumFacing doorwayFacing = facing.getOpposite();
        for (int i = 0; i < 10; ++i) {
            BlockPos blockPos = pos.offset(doorwayFacing, i);
            IBlockState state = world.getBlockState(blockPos);
            if (state.getBlock() == Blocks.blockMagicDoorway) {
                world.destroyBlock(blockPos, false);
            }
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        EnumFacing facing = state.getValue(FACING);
        switch (facing) {
            case NORTH:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOX_WALL_W);
                break;
            case EAST:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOX_WALL_N);
                break;
            case SOUTH:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOX_WALL_E);
                break;
            case WEST:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOX_WALL_S);
                break;
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch(state.getValue(FACING)) {
            case NORTH:
                return BOUNDING_BOX_WALL_W;
            case EAST:
                return BOUNDING_BOX_WALL_N;
            case SOUTH:
                return BOUNDING_BOX_WALL_E;
        }
        return BOUNDING_BOX_WALL_S;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoor) {
            IBlockState textureBlock = ((TileEntityMagicDoor) tileEntity).getTextureBlock();
            // Try to get the block's texture
            TextureAtlasSprite texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(textureBlock);
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
                .add(FACING)
                .add(TEXTURE_MAIN)
                .add(TEXTURE_HIGHLIGHT)
                .build();
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
        return state.getValue(PART) == BlockMagicDoor.EnumPartType.TOP;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PART).getValue() | (state.getValue(FACING).getHorizontalIndex() << 1);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(PART, (meta & 1) == 0 ? EnumPartType.BOTTOM : EnumPartType.TOP)
                .withProperty(FACING, EnumFacing.byHorizontalIndex(meta >> 1));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMagicDoor();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            breakBlock(worldIn, pos, state);
        }
        return true;
    }

    /**
     * The door has two parts: top and bottom.
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
