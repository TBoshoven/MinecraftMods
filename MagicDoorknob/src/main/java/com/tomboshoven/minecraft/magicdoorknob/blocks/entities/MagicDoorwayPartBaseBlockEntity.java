package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
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
    private static final ModelTextureProperty TEXTURE_MAIN = ModelTextureProperty.get(new ResourceLocation(PROPERTY_NAMESPACE, "texture_main"));

    /**
     * The highlight texture of the doorway (based on doorknob).
     */
    private static final ModelTextureProperty TEXTURE_HIGHLIGHT = ModelTextureProperty.get(new ResourceLocation(PROPERTY_NAMESPACE, "texture_highlight"));

    // The block we're basing the appearance of this block on.
    private BlockState baseBlockState = Blocks.AIR.defaultBlockState();
    // The doorknob that caused this block to be created.
    private MagicDoorknobItem doorknob;

    MagicDoorwayPartBaseBlockEntity(BlockEntityType<? extends MagicDoorwayPartBaseBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        saveInternal(compound);
    }

    private void saveInternal(CompoundTag compound) {
        compound.put("baseBlock", NbtUtils.writeBlockState(baseBlockState));
        if (doorknob != null) {
            compound.putString("doorknobType", doorknob.getTypeName());
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        loadInternal(compound);
    }

    private void loadInternal(CompoundTag compound) {
        baseBlockState = NbtUtils.readBlockState(compound.getCompound("baseBlock"));
        String doorknobType = compound.getString("doorknobType");
        doorknob = Items.DOORKNOBS.get(doorknobType).get();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();
        saveInternal(updateTag);
        return updateTag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            loadInternal(tag);
            requestModelDataUpdate();
        }
    }

    @Override
    public @NotNull ModelData getModelData() {
        Minecraft minecraft = Minecraft.getInstance();
        BlockModelShaper blockModelShapes = minecraft.getBlockRenderer().getBlockModelShaper();

        // Get the base block texture
        Level world = getLevel();
        TextureAtlasSprite blockTexture = world == null ? null : blockModelShapes.getTexture(baseBlockState, world, getBlockPos());
        Material blockMaterial;
        if (blockTexture == null || blockTexture instanceof MissingTextureAtlasSprite) {
            // If we can't find the texture, use a transparent one instead, to deal with things like air.
            blockMaterial = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MOD_ID, "block/empty"));
        } else {
            blockMaterial = new Material(blockTexture.atlas().location(), blockTexture.getName());
        }

        // Get the highlight texture
        Material doorknobMaterial;
        if (doorknob != null) {
            doorknobMaterial = doorknob.getMainMaterial();
        } else {
            // This can happen when we draw a frame before receiving the block entity data from the server.
            // In that case, we just want to draw the outline to make it less conspicuous.
            doorknobMaterial = blockMaterial;
        }

        return ModelData.builder()
                .with(TEXTURE_MAIN, blockMaterial)
                .with(TEXTURE_HIGHLIGHT, doorknobMaterial)
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
