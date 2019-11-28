package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.properties.PropertyTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

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

    BlockMagicDoor() {
        super(new MaterialTransparent(MapColor.AIR));
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoor) {
            IBlockState textureBlock = ((TileEntityMagicDoor) tileEntity).getTextureBlock();
            // Try to get the block's texture
            TextureAtlasSprite texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(textureBlock);
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
