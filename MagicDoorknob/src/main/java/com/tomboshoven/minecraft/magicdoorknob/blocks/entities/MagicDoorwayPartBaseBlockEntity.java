package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.TextureSourceReference;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

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
    public static final ModelTextureProperty TEXTURE_MAIN = ModelTextureProperty.get(ResourceLocation.fromNamespaceAndPath(PROPERTY_NAMESPACE, "texture_main"));

    /**
     * The highlight texture of the doorway (based on doorknob).
     */
    public static final ModelTextureProperty TEXTURE_HIGHLIGHT = ModelTextureProperty.get(ResourceLocation.fromNamespaceAndPath(PROPERTY_NAMESPACE, "texture_highlight"));

    // Material to use when no proper material can be found, such as with air.
    @SuppressWarnings("deprecation")
    private static final Material EMPTY_MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath(MOD_ID, "block/empty"));

    // The block we're basing the appearance of this block on.
    private BlockState baseBlockState = Blocks.AIR.defaultBlockState();
    // The doorknob that caused this block to be created.
    private @Nullable MagicDoorknobItem doorknob;

    MagicDoorwayPartBaseBlockEntity(BlockEntityType<? extends MagicDoorwayPartBaseBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        saveInternal(output);
    }

    private void saveInternal(ValueOutput output) {
        output.store("baseBlock", BlockState.CODEC, baseBlockState);
        if (doorknob != null) {
            output.putString("doorknobType", doorknob.getTypeName());
        }
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        loadInternal(valueInput);
    }

    private void loadInternal(ValueInput valueInput) {
        baseBlockState = valueInput.read("baseBlock", BlockState.CODEC)
                .orElse(Blocks.AIR.defaultBlockState());

        valueInput.getString("doorknobType").ifPresent(doorknobType -> {
            DeferredItem<MagicDoorknobItem> deferredDoorknob = Items.DOORKNOBS.get(doorknobType);
            doorknob = deferredDoorknob != null ? deferredDoorknob.get() : null;
        });
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        // Unfortunately this is still done with raw NBT tags instead of a ValueOutput.
        // Let's just create a ValueOutput ourselves so we can reuse the write code.
        TagValueOutput valueOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, lookupProvider);
        saveInternal(valueOutput);
        return valueOutput.buildResult();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ValueInput valueInput) {
        loadInternal(valueInput);
        requestModelDataUpdate();
    }

    @Override
    public @NotNull ModelData getModelData() {
        // Get the base block texture
        Level level = getLevel();
        BlockPos blockPos = getBlockPos();

        // Fallback chain is block texture -> block particle texture -> empty
        TextureSourceReference fallbackReference = new TextureSourceReference.MaterialTextureSource(EMPTY_MATERIAL);
        TextureSourceReference blockTextureSourceReference = new TextureSourceReference.BlockLookup(level, blockPos, baseBlockState, fallbackReference);

        TextureSourceReference doorknobTextureSourceReference;
        if (doorknob == null) {
            // This can happen when we draw a frame before receiving the block entity data from the server.
            // This makes it a bit less conspicuous.
            doorknobTextureSourceReference = blockTextureSourceReference;
        }
        else {
            doorknobTextureSourceReference = new TextureSourceReference.MaterialTextureSource(doorknob.getMainMaterial());
        }

        return ModelData.builder()
                .with(TEXTURE_MAIN, blockTextureSourceReference)
                .with(TEXTURE_HIGHLIGHT, doorknobTextureSourceReference)
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
