package com.tomboshoven.minecraft.magicmirror.blocks;

import com.mojang.serialization.MapCodec;
import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.BlockEntities;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import javax.annotation.Nullable;

import static com.tomboshoven.minecraft.magicmirror.MagicMirrorMod.MOD_ID;
import static com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorInactiveBlock.EnumPartType.TOP;

public class MagicMirrorCoreBlock extends MagicMirrorActiveBlock {
    private static final MapCodec<MagicMirrorCoreBlock> CODEC = simpleCodec(MagicMirrorCoreBlock::new);

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
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
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
    public record MessageAttachModifier(BlockPos mirrorPos, ItemStack usedItemStack, ResourceLocation modifier) implements CustomPacketPayload {
        public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "attach_modifier");

        /**
         * Decode a packet into an instance of the message.
         *
         * @param buf The buffer to read from.
         */
        public MessageAttachModifier(FriendlyByteBuf buf) {
            this(buf.readBlockPos(), buf.readItem(), buf.readResourceLocation());
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeBlockPos(mirrorPos);
            buf.writeItem(usedItemStack);
            buf.writeResourceLocation(modifier);
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }

    /**
     * Handler for messages describing modifiers being attached to mirrors.
     */
    public static void onMessageAttachModifier(MessageAttachModifier message, PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                BlockEntity blockEntity = world.getBlockEntity(message.mirrorPos);
                if (blockEntity instanceof MagicMirrorCoreBlockEntity) {
                    MagicMirrorModifier modifier = MagicMirrorModifiers.MAGIC_MIRROR_MODIFIER_REGISTRY.get(message.modifier);
                    if (modifier == null) {
                        MagicMirrorMod.LOGGER.error("Received a request to add modifier \"{}\" which does not exist.", message.modifier);
                        return;
                    }
                    modifier.apply((MagicMirrorCoreBlockEntity) blockEntity, message.usedItemStack);
                    world.playLocalSound(message.mirrorPos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, .6f, .6f, true);
                }
            }
        });
    }
}
