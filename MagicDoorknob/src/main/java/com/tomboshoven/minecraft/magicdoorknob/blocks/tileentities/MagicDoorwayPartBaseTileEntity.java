package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import com.mojang.datafixers.Dynamic;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.ModelTextureProperty;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod.MOD_ID;
import static com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.TexturedModelLoader.PROPERTY_NAMESPACE;
import static net.minecraftforge.client.model.CompositeModel.SUBMODEL_DATA;

/**
 * Base class for tile entities that make up magic doorways.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MagicDoorwayPartBaseTileEntity extends TileEntity {
    /**
     * The main texture of the doorway (based on base block).
     */
    private static final ModelTextureProperty TEXTURE_MAIN = ModelTextureProperty.get(new ResourceLocation(PROPERTY_NAMESPACE, "texture_main"));

    /**
     * The highlight texture of the doorway (based on doorknob).
     */
    private static final ModelTextureProperty TEXTURE_HIGHLIGHT = ModelTextureProperty.get(new ResourceLocation(PROPERTY_NAMESPACE, "texture_highlight"));

    /**
     * All parts of the model that need to be textured.
     */
    private static final String[] SUBMODEL_NAMES = {"door", "doorknob", "top", "wall1", "wall2", "pillar1", "pillar2", "pillar3", "pillar4"};

    // The block we're basing the appearance of this block on.
    private BlockState baseBlockState = Blocks.AIR.getDefaultState();
    // The doorknob that caused this block to be created.
    private MagicDoorknobItem doorknob;

    MagicDoorwayPartBaseTileEntity(TileEntityType<? extends MagicDoorwayPartBaseTileEntity> tileEntityType) {
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
        requestModelDataUpdate();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IModelData getModelData() {
        Minecraft minecraft = Minecraft.getInstance();
        BlockModelShapes blockModelShapes = minecraft.getBlockRendererDispatcher().getBlockModelShapes();

        // Get the base block texture
        World world = getWorld();
        TextureAtlasSprite blockTexture = world == null ? null : blockModelShapes.getTexture(baseBlockState, world, getPos());
        Material blockMaterial;
        if (blockTexture == null || blockTexture instanceof MissingTextureSprite) {
            // If we can't find the texture, use a transparent one instead, to deal with things like air.
            blockMaterial = new Material(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(MOD_ID, "block/empty"));
        }
        else {
            blockMaterial = new Material(blockTexture.getAtlasTexture().getTextureLocation(), blockTexture.getName());
        }

        // Get the highlight texture
        Material doorknobMaterial;
        if (doorknob != null) {
            doorknobMaterial = doorknob.getMainMaterial();
        } else {
            // This can happen when we draw a frame before receiving the tile entity data from the server.
            // In that case, we just want to draw the outline to make it less conspicuous.
            doorknobMaterial = blockMaterial;
        }

        IModelData submodelData = new ModelDataMap.Builder()
                .withInitial(TEXTURE_MAIN, blockMaterial)
                .withInitial(TEXTURE_HIGHLIGHT, doorknobMaterial)
                .build();
        CompositeModel.SubmodelModelData submodelModelData = new CompositeModel.SubmodelModelData();
        for (String submodelName : SUBMODEL_NAMES) {
            submodelModelData.putSubmodelData(submodelName, submodelData);
        }
        return new ModelDataMap.Builder()
                .withInitial(SUBMODEL_DATA, submodelModelData)
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
    public MagicDoorknobItem getDoorknob() {
        return doorknob;
    }

    /**
     * @param doorknob The doorknob that was used to create this block.
     */
    public void setDoorknob(MagicDoorknobItem doorknob) {
        this.doorknob = doorknob;
    }
}
