package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.TextureSourceReference;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;

import static com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod.MOD_ID;
import static com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty.PROPERTY_NAMESPACE;

/**
 * Base class for block entities that make up magic doorways.
 */
public abstract class MagicDoorwayPartBaseBlockEntity extends BlockEntity {
    /**
     * The main texture of the doorway (based on base block).
     */
    private static final ModelTextureProperty TEXTURE_MAIN = ModelTextureProperty.get(new ResourceLocation(PROPERTY_NAMESPACE, "texture_main"));

    /**
     * The highlight texture of the doorway (based on doorknob).
     */
    private static final ModelTextureProperty TEXTURE_HIGHLIGHT = ModelTextureProperty.get(new ResourceLocation(PROPERTY_NAMESPACE, "texture_highlight"));

    /**
     * The particle texture of the doorway (based on base block particle texture).
     */
    public static final ModelTextureProperty TEXTURE_PARTICLE = ModelTextureProperty.get(new ResourceLocation(PROPERTY_NAMESPACE, "texture_particle"));

    // The block we're basing the appearance of this block on.
    private BlockState baseBlockState = Blocks.AIR.defaultBlockState();
    // The doorknob that caused this block to be created.
    private MagicDoorknobItem doorknob;

    MagicDoorwayPartBaseBlockEntity(BlockEntityType<? extends MagicDoorwayPartBaseBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        return writeInternal(compound);
    }

    private CompoundTag writeInternal(CompoundTag compound) {
        CompoundTag result = super.save(compound);
        compound.put("baseBlock", NbtUtils.writeBlockState(baseBlockState));
        if (doorknob != null) {
            result.putString("doorknobType", doorknob.getTypeName());
        }
        return result;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        readInternal(compound);
    }

    private void readInternal(CompoundTag compound) {
        baseBlockState = NbtUtils.readBlockState(compound.getCompound("baseBlock"));
        String doorknobType = compound.getString("doorknobType");
        doorknob = Items.DOORKNOBS.get(doorknobType);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return writeInternal(super.getUpdateTag());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(getBlockPos(), 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        readInternal(pkt.getTag());
        requestModelDataUpdate();
    }

    @Override
    public IModelData getModelData() {
        // Get the base block texture
        Level level = getLevel();
        BlockPos blockPos = getBlockPos();

        // Material to use when no proper material can be found, such as with air.
        @SuppressWarnings("deprecation")
        final Material emptyMaterial = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(MOD_ID, "block/empty"));

        // Fallback chain is block texture -> empty
        TextureSourceReference fallbackReference = new TextureSourceReference.MaterialTextureSource(emptyMaterial);
        TextureSourceReference particleTextureSourceReference = new TextureSourceReference.BlockParticle(baseBlockState, fallbackReference);
        TextureSourceReference blockTextureSourceReference = new TextureSourceReference.BlockLookup(level, blockPos, baseBlockState, fallbackReference);

        TextureSourceReference doorknobTextureSourceReference;
        if (doorknob == null) {
            // This can happen when we draw a frame before receiving the block entity data from the server.
            // This makes it a bit less conspicuous.
            doorknobTextureSourceReference = blockTextureSourceReference;
        } else {
            doorknobTextureSourceReference = new TextureSourceReference.MaterialTextureSource(doorknob.getMainMaterial());
        }

        CompositeModel.CompositeModelData compositeModelData = new CompositeModel.CompositeModelData();
        compositeModelData.setData(TEXTURE_MAIN, blockTextureSourceReference);
        compositeModelData.setData(TEXTURE_HIGHLIGHT, doorknobTextureSourceReference);
        compositeModelData.setData(TEXTURE_PARTICLE, particleTextureSourceReference);
        return compositeModelData;
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
