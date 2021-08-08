package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
     * @param worldIn  The world containing the mirror.
     * @param pos      The position of the mirror in the world.
     * @param heldItem The item used to attach the modifier to the mirror.
     * @param modifier The modifier to attach to the mirror.
     */
    private void attachModifier(Level worldIn, BlockPos pos, ItemStack heldItem, MagicMirrorModifier modifier) {
        // Attaching happens in the core
        BlockPos coreBlockPos = getCoreBlockPos(pos);

        // Item stack may change by attaching
        ItemStack originalHeldItem = heldItem.copy();
        modifier.apply(worldIn, coreBlockPos, heldItem);
        MagicMirrorCoreBlock.MessageAttachModifier message = new MagicMirrorCoreBlock.MessageAttachModifier(coreBlockPos, originalHeldItem, modifier);
        PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_CHUNK.with(() -> worldIn.getChunkAt(coreBlockPos));
        Network.CHANNEL.send(target, message);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        // The mirror will only do anything if it's used from the front.
        if (state.getValue(FACING) == hit.getDirection()) {
            if (!worldIn.isClientSide) {
                // First, see if we can add a modifier
                ItemStack heldItem = player.getItemInHand(handIn);
                if (!heldItem.isEmpty()) {
                    for (MagicMirrorModifier modifier : MagicMirrorModifier.getModifiers()) {
                        if (modifier.canModify(worldIn, pos, heldItem)) {
                            attachModifier(worldIn, pos, heldItem, modifier);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }

                // Then, see if any existing modifier can do something.
                MagicMirrorCoreBlockEntity coreBlockEntity = getCoreBlockEntity(worldIn, pos);
                if (coreBlockEntity != null) {
                    if (coreBlockEntity.tryActivate(player, handIn)) {
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
