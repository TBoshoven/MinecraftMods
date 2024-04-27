package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

public abstract class MagicMirrorActiveBlock extends MagicMirrorBaseBlock implements EntityBlock {
    /**
     * Create a new Magic Mirror block.
     */
    MagicMirrorActiveBlock(Properties properties) {
        super(properties);
    }

    /**
     * Get the position of the core block, given the position of this block.
     *
     * @param pos The position of this block.
     * @return The position of the corresponding core block.
     */
    protected abstract BlockPos getCoreBlockPos(BlockPos pos);

    /**
     * Get the block entity for the mirror core.
     *
     * @param level The current level,
     * @param pos   The position of this block.
     * @return The block entity corresponding to the mirror core.
     */
    @Nullable
    public MagicMirrorCoreBlockEntity getCoreBlockEntity(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(getCoreBlockPos(pos));
        if (blockEntity instanceof MagicMirrorCoreBlockEntity) {
            return (MagicMirrorCoreBlockEntity) blockEntity;
        }
        return null;
    }

    /**
     * Attach a modifier to the mirror at the specified position (server version).
     * This sends a message to clients as well.
     *
     * @param heldItem The item used to attach the modifier to the mirror.
     * @param modifier The modifier to attach to the mirror.
     * @param blockEntity The block entity of the mirror to apply the modifier to.
     */
    private void attachModifier(ItemStack heldItem, MagicMirrorModifier modifier, MagicMirrorCoreBlockEntity blockEntity) {
        // Item stack may change by attaching
        ItemStack originalHeldItem = heldItem.copy();
        modifier.apply(blockEntity, heldItem);
        Level level = blockEntity.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            MagicMirrorCoreBlock.MessageAttachModifier message = new MagicMirrorCoreBlock.MessageAttachModifier(blockEntity.getBlockPos(), originalHeldItem, modifier);
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(blockEntity.getBlockPos()), message);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // The mirror will only do anything if it's used from the front.
        if (state.getValue(FACING) == hit.getDirection()) {
            MagicMirrorCoreBlockEntity coreBlockEntity = getCoreBlockEntity(level, pos);
            if (coreBlockEntity != null) {
                // See if any existing modifier can do something.
                return coreBlockEntity.useWithoutItem(player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // The mirror will only do anything if it's used from the front.
        if (state.getValue(FACING) == hit.getDirection()) {
            MagicMirrorCoreBlockEntity coreBlockEntity = getCoreBlockEntity(level, pos);
            if (coreBlockEntity != null) {
                // First, see if we can add a modifier
                for (MagicMirrorModifier modifier : MagicMirrorModifier.getModifiers()) {
                    if (modifier.canModify(heldItem, coreBlockEntity)) {
                        attachModifier(heldItem, modifier, coreBlockEntity);
                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                    }
                }

                // Then, see if any existing modifier can do something.
                return coreBlockEntity.useWithItem(player, heldItem);
            }
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        return super.useItemOn(heldItem, state, level, pos, player, hand, hit);
    }
}
