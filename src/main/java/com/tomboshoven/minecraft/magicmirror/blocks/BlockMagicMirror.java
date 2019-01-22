package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirror;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockMagicMirror extends BlockHorizontal {
    /**
     * Property describing whether the mirror is completely constructed.
     */
    private static final PropertyBool COMPLETE = PropertyBool.create("complete");

    /**
     * Property describing which part of the mirror is being represented by this block.
     */
    private static final PropertyEnum<EnumPart> PART = PropertyEnum.create("part", EnumPart.class);

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

    public BlockMagicMirror() {
        super(new Material(MapColor.GRAY));

        // By default, we're the bottom part of a broken mirror
        setDefaultState(
                this.blockState.getBaseState()
                        .withProperty(COMPLETE, Boolean.FALSE)
                        .withProperty(PART, EnumPart.BOTTOM)
        );

        setHardness(.8f);
        setSoundType(SoundType.GLASS);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        // Try to complete a mirror by looking for an incomplete mirror above or below.
        IBlockState blockBelow = worldIn.getBlockState(pos.down());
        IBlockState blockAbove = worldIn.getBlockState(pos.up());
        if (blockBelow.getBlock() == this && !blockBelow.getValue(COMPLETE) && blockBelow.getValue(FACING) == state.getValue(FACING)) {
            worldIn.setBlockState(pos.down(), blockBelow.withProperty(COMPLETE, true).withProperty(PART, EnumPart.BOTTOM));
            worldIn.setBlockState(pos, state.withProperty(COMPLETE, true).withProperty(PART, EnumPart.TOP));
        } else if (blockAbove.getBlock() == this && !blockAbove.getValue(COMPLETE) && blockAbove.getValue(FACING) == state.getValue(FACING)) {
            worldIn.setBlockState(pos.up(), blockAbove.withProperty(COMPLETE, true).withProperty(PART, EnumPart.TOP));
            worldIn.setBlockState(pos, state.withProperty(COMPLETE, true).withProperty(PART, EnumPart.BOTTOM));
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        // Break the mirror if the other part is broken.
        if (state.getValue(COMPLETE)) {
            if (
                    (state.getValue(PART) == EnumPart.TOP && worldIn.getBlockState(pos.down()).getBlock() != this) ||
                            (state.getValue(PART) == EnumPart.BOTTOM && worldIn.getBlockState(pos.up()).getBlock() != this)
            ) {
                worldIn.setBlockState(pos, state.withProperty(COMPLETE, false));
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, COMPLETE, PART);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex()
                | (state.getValue(COMPLETE) ? 1 : 0) << 2
                | state.getValue(PART).getValue() << 3;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
                .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
                .withProperty(COMPLETE, (meta & (1 << 2)) != 0)
                .withProperty(PART, (meta & (1 << 3)) != 0 ? EnumPart.TOP : EnumPart.BOTTOM);
    }


    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        // Only the opposite face is default
        return state.getValue(FACING).getOpposite() == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX[state.getValue(FACING).getHorizontalIndex()];
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState blockState) {
        return blockState.getValue(COMPLETE);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMagicMirror(state.getValue(FACING), state.getValue(PART));
    }

    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        // Make sure the mirror is facing the right way when placed
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public String getTranslationKey() {
        return super.getTranslationKey();
    }

    /**
     * The mirror has two parts: top and bottom.
     */
    public enum EnumPart implements IStringSerializable {
        TOP("top", 0),
        BOTTOM("bottom", 1),
        ;

        String name;
        int value;

        EnumPart(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        int getValue() {
            return this.value;
        }
    }
}
