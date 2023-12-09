package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.BlockEntities;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import static com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorInactiveBlock.EnumPartType.TOP;

@SuppressWarnings("deprecation")
public class MagicMirrorCoreBlock extends MagicMirrorActiveBlock {
    /**
     * Number of ticks between updating who we're reflecting
     */
    private static final int REFLECTION_UPDATE_INTERVAL = 10;

    /**
     * Create a new Magic Mirror block.
     */
    MagicMirrorCoreBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    protected BlockPos getCoreBlockPos(BlockPos pos) {
        return pos;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MagicMirrorCoreBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof MagicMirrorCoreBlockEntity) {
            ((MagicMirrorCoreBlockEntity) blockEntity).removeModifiers(worldIn, pos);
        }

        // Change the other part to incomplete
        BlockPos otherPos = pos.above();
        BlockState otherState = worldIn.getBlockState(otherPos);
        if (otherState.getBlock() == Blocks.MAGIC_MIRROR_PART.get()) {
            worldIn.setBlockAndUpdate(
                    otherPos,
                    Blocks.MAGIC_MIRROR_INACTIVE.get().defaultBlockState()
                            .setValue(MagicMirrorInactiveBlock.PART, TOP)
                            .setValue(HorizontalDirectionalBlock.FACING, state.getValue(HorizontalDirectionalBlock.FACING))
            );
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != BlockEntities.MAGIC_MIRROR_CORE.get()) {
            return null;
        }
        return new BlockEntityTicker<>() {
            // Start the update counter at its max, so we update on the first tick.
            private int reflectionUpdateCounter = REFLECTION_UPDATE_INTERVAL;

            @Override
            public void tick(Level world, BlockPos pos, BlockState state, T entity) {
                MagicMirrorCoreBlockEntity mirrorEntity = (MagicMirrorCoreBlockEntity) entity;
                if (reflectionUpdateCounter++ == REFLECTION_UPDATE_INTERVAL) {
                    reflectionUpdateCounter = 0;
                    mirrorEntity.updateReflection();
                }
                mirrorEntity.coolDown();
            }
        };
    }

    /**
     * Message describing the action of attaching a new modifier to a mirror.
     */
    public static class MessageAttachModifier {
        /**
         * The position of the mirror in the world.
         */
        BlockPos mirrorPos;
        /**
         * The item used to attach the modifier to the mirror.
         */
        ItemStack usedItemStack;
        /**
         * The name of the modifier this is being attached.
         */
        String modifierName;

        @SuppressWarnings({"unused", "WeakerAccess"})
        public MessageAttachModifier() {
        }

        /**
         * @param mirrorPos     The position of the mirror in the world.
         * @param usedItemStack The item used to attach the modifier to the mirror.
         * @param modifier      The modifier this is being attached.
         */
        MessageAttachModifier(BlockPos mirrorPos, ItemStack usedItemStack, MagicMirrorModifier modifier) {
            this.mirrorPos = mirrorPos;
            this.usedItemStack = usedItemStack;
            modifierName = modifier.getName();
        }

        /**
         * Decode a packet into an instance of the message.
         *
         * @param buf The buffer to read from.
         * @return The created message instance.
         */
        public static MessageAttachModifier decode(FriendlyByteBuf buf) {
            MessageAttachModifier result = new MessageAttachModifier();
            result.mirrorPos = buf.readBlockPos();
            result.usedItemStack = buf.readItem();
            result.modifierName = buf.readUtf();
            return result;
        }


        /**
         * Encode the message into a packet buffer.
         *
         * @param buf The buffer to write to.
         */
        public void encode(FriendlyByteBuf buf) {
            buf.writeBlockPos(mirrorPos);
            buf.writeItem(usedItemStack);
            buf.writeUtf(modifierName);
        }
    }

    /**
     * Handler for messages describing modifiers being attached to mirrors.
     */
    public static void onMessageAttachModifier(MessageAttachModifier message, Supplier<? extends NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                BlockEntity blockEntity = world.getBlockEntity(message.mirrorPos);
                if (blockEntity instanceof MagicMirrorCoreBlockEntity) {
                    MagicMirrorModifier modifier = MagicMirrorModifier.getModifier(message.modifierName);
                    if (modifier == null) {
                        MagicMirrorMod.LOGGER.error("Received a request to add modifier \"{}\" which does not exist.", message.modifierName);
                        return;
                    }
                    modifier.apply((MagicMirrorCoreBlockEntity) blockEntity, message.usedItemStack);
                    world.playLocalSound(message.mirrorPos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, .6f, .6f, true);
                }
            }
        }));
        ctx.setPacketHandled(true);
    }
}
