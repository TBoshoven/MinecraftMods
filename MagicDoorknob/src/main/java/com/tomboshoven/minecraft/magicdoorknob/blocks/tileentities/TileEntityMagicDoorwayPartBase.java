package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import com.mojang.datafixers.Dynamic;
import com.tomboshoven.minecraft.magicdoorknob.ModMagicDoorknob;
import com.tomboshoven.minecraft.magicdoorknob.items.ItemMagicDoorknob;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.ModelTextureProperty;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.TexturedModelLoader.PROPERTY_NAMESPACE;

/**
 * Base class for tile entities that make up magic doorways.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityMagicDoorwayPartBase extends TileEntity {
    /**
     * The main texture of the doorway (based on base block).
     */
    private static final ModelTextureProperty TEXTURE_MAIN = ModelTextureProperty.get(new ResourceLocation(PROPERTY_NAMESPACE, "texture_main"));

    /**
     * The highlight texture of the doorway (based on doorknob).
     */
    private static final ModelTextureProperty TEXTURE_HIGHLIGHT = ModelTextureProperty.get(new ResourceLocation(PROPERTY_NAMESPACE, "texture_highlight"));

    // The block we're basing the appearance of this block on.
    private BlockState baseBlockState = Blocks.AIR.getDefaultState();
    // The doorknob that caused this block to be created.
    private ItemMagicDoorknob doorknob;

    public TileEntityMagicDoorwayPartBase(TileEntityType<? extends TileEntityMagicDoorwayPartBase> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return writeInternal(compound);
    }

    private CompoundNBT writeInternal(CompoundNBT compound) {
        CompoundNBT result = super.write(compound);
        ResourceLocation registryName = baseBlockState.getBlock().getRegistryName();
        if (registryName != null) {
            compound.put("baseBlock", BlockState.serialize(NBTDynamicOps.INSTANCE, baseBlockState).getValue());
        }
        if (doorknob != null) {
            result.putString("doorknobType", doorknob.getTypeName());
        }
        return result;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        baseBlockState = BlockState.deserialize(new Dynamic<>(NBTDynamicOps.INSTANCE, compound.get("baseBlock")));
        String doorknobType = compound.getString("doorknobType");
        doorknob = Items.DOORKNOBS.get(doorknobType);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return writeInternal(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        read(pkt.getNbtCompound());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IModelData getModelData() {
        Minecraft minecraft = Minecraft.getInstance();
        BlockModelShapes blockModelShapes = minecraft.getBlockRendererDispatcher().getBlockModelShapes();

        // Get the base block texture
        BlockState baseBlockState = getBaseBlockState();
        TextureAtlasSprite blockTexture = blockModelShapes.getTexture(baseBlockState);
        if (blockTexture instanceof MissingTextureSprite) {
            // If we can't find the texture, use a transparent one instead, to deal with things like air.
            blockTexture = minecraft.getTextureMap().getAtlasSprite(ModMagicDoorknob.MOD_ID + ":block/empty");
        }

        // Get the highlight texture
        ItemMagicDoorknob doorknob = getDoorknob();
        ResourceLocation doorknobTextureLocation;
        if (doorknob != null) {
            doorknobTextureLocation = doorknob.getMainTextureLocation();
        } else {
            doorknobTextureLocation = MissingTextureSprite.getLocation();
        }

        return new ModelDataMap.Builder()
                .withInitial(TEXTURE_MAIN, blockTexture.getName())
                .withInitial(TEXTURE_HIGHLIGHT, doorknobTextureLocation)
                .build();
    }

    /**
     * @return The blockstate that the appearance of this block is based on.
     */
    public BlockState getBaseBlockState() {
        return baseBlockState;
    }

    /**
     * @param baseBlockState The blockstate that the appearance of this block is based on.
     */
    public void setBaseBlockState(BlockState baseBlockState) {
        this.baseBlockState = baseBlockState;
    }

    /**
     * @return The doorknob that was used to create this block.
     */
    @Nullable
    public ItemMagicDoorknob getDoorknob() {
        return doorknob;
    }

    /**
     * @param doorknob The doorknob that was used to create this block.
     */
    public void setDoorknob(ItemMagicDoorknob doorknob) {
        this.doorknob = doorknob;
    }
}
