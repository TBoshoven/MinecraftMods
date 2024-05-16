package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import com.mojang.datafixers.Dynamic;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty;
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
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nullable;

import static com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod.MOD_ID;
import static com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty.PROPERTY_NAMESPACE;

/**
 * Base class for tile entities that make up magic doorways.
 */
public abstract class MagicDoorwayPartBaseBlockEntity extends TileEntity {
    /**
     * The main texture of the doorway (based on base block).
     */
    private static final ModelTextureProperty TEXTURE_MAIN = ModelTextureProperty.get(new ResourceLocation(PROPERTY_NAMESPACE, "texture_main"));

    /**
     * The highlight texture of the doorway (based on doorknob).
     */
    private static final ModelTextureProperty TEXTURE_HIGHLIGHT = ModelTextureProperty.get(new ResourceLocation(PROPERTY_NAMESPACE, "texture_highlight"));

    // The block we're basing the appearance of this block on.
    private BlockState baseBlockState = Blocks.AIR.defaultBlockState();
    // The doorknob that caused this block to be created.
    private MagicDoorknobItem doorknob;

    MagicDoorwayPartBaseBlockEntity(TileEntityType<? extends MagicDoorwayPartBaseBlockEntity> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        return writeInternal(compound);
    }

    private CompoundNBT writeInternal(CompoundNBT compound) {
        CompoundNBT result = super.save(compound);
        compound.put("baseBlock", BlockState.serialize(NBTDynamicOps.INSTANCE, baseBlockState).getValue());
        if (doorknob != null) {
            result.putString("doorknobType", doorknob.getTypeName());
        }
        return result;
    }

    @Override
    public void load(CompoundNBT compound) {
        super.load(compound);
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
        return new SUpdateTileEntityPacket(getBlockPos(), 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(pkt.getTag());
        requestModelDataUpdate();
    }

    @Override
    public IModelData getModelData() {
        Minecraft minecraft = Minecraft.getInstance();
        BlockModelShapes blockModelShapes = minecraft.getBlockRenderer().getBlockModelShaper();

        // Get the base block texture
        World world = getLevel();
        TextureAtlasSprite blockTexture = world == null ? MissingTextureSprite.newInstance() : blockModelShapes.getTexture(baseBlockState, world, getBlockPos());
        if (blockTexture instanceof MissingTextureSprite) {
            // If we can't find the texture, use a transparent one instead, to deal with things like air.
            blockTexture = minecraft.getTextureAtlas().getTexture(MOD_ID + ":block/empty");
        }

        // Get the highlight texture
        ResourceLocation doorknobTextureLocation;
        if (doorknob != null) {
            doorknobTextureLocation = doorknob.getMainTextureLocation();
        } else {
            // This can happen when we draw a frame before receiving the tile entity data from the server.
            // In that case, we just want to draw the outline to make it less conspicuous.
            doorknobTextureLocation = blockTexture.getName();
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
