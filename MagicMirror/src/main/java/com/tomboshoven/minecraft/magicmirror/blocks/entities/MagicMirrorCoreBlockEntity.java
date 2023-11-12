package com.tomboshoven.minecraft.magicmirror.blocks.entities;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.ReflectionClient;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.DistExecutor;
import net.neoforged.neoforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * The block that has all the reflection logic.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorCoreBlockEntity extends BlockEntity {
    /**
     * The list of all modifiers to the mirror.
     */
    private final List<MagicMirrorBlockEntityModifier> modifiers = Lists.newArrayList();
    /**
     * The reflection object, used for keeping track of who is being reflected and rendering.
     */
    private final Reflection reflection;

    public MagicMirrorCoreBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MAGIC_MIRROR_CORE.get(), pos, state);

        reflection = DistExecutor.unsafeRunForDist(() -> ReflectionClient::new, () -> Reflection::new);
    }

    /**
     * Find all players that can be reflected.
     *
     * @param world       The world in which the block entity exists.
     * @param ownPosition The position of the block entity in the world.
     * @return A list of players that are candidates for reflecting.
     */
    private static List<Player> findReflectablePlayers(EntityGetter world, BlockPos ownPosition) {
        // TODO: Add facing limitations
        AABB scanBB = new AABB(ownPosition.offset(-10, -4, -10), ownPosition.offset(10, 4, 10));
        List<Player> playerEntities = world.getEntitiesOfClass(Player.class, scanBB);
        // Only return real players
        return playerEntities.stream().filter(player -> !(player instanceof FakePlayer)).collect(Collectors.toList());
    }

    /**
     * Find the best player to reflect.
     *
     * @param world       The world in which the block entity exists.
     * @param ownPosition The position of the block entity in the world.
     * @return A player to reflect, or null if there are no valid candidates.
     */
    @Nullable
    private static Player findPlayerToReflect(EntityGetter world, BlockPos ownPosition) {
        List<Player> players = findReflectablePlayers(world, ownPosition);
        if (players.isEmpty()) {
            return null;
        }
        return Collections.min(players, Comparator.comparingDouble(player -> ownPosition.distSqr(player.getOnPos())));
    }

    /**
     * Update the reflection to show the closest player.
     */
    public void updateReflection() {
        Level world = getLevel();

        if (world != null) {
            // Update the reflection angle.
            // It's impractical to do this while loading because we need access to the blockstate.
            BlockState blockState = getBlockState();
            reflection.setAngle(blockState.getValue(HORIZONTAL_FACING).toYRot());

            Player playerToReflect = findPlayerToReflect(world, getBlockPos());

            if (playerToReflect == null) {
                reflection.stopReflecting();
            } else {
                reflection.setReflectedEntity(playerToReflect);
            }
        }
    }

    /**
     * Cool down all modifiers.
     */
    public void coolDown() {
        modifiers.forEach(MagicMirrorBlockEntityModifier::coolDown);
    }

    @Override
    public void onChunkUnloaded() {
        // Stop reflecting to unload the textures and frame buffer
        reflection.stopReflecting();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        // We need to make sure that we stop reflecting when the block entity is destroyed, so we don't leak any frame
        // buffers and textures
        reflection.stopReflecting();
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        saveInternal(compound);
    }

    /**
     * Write out the data for the magic mirror core to an NBT compound.
     *
     * @param compound The NBT compound to write to.
     */
    private void saveInternal(CompoundTag compound) {
        ListTag modifierList = new ListTag();
        for (MagicMirrorBlockEntityModifier modifier : modifiers) {
            CompoundTag modifierCompound = new CompoundTag();
            modifierCompound.putString("name", modifier.getName());
            modifierList.add(modifier.write(modifierCompound));
        }
        compound.put("modifiers", modifierList);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        loadInternal(compound);
    }

    private void loadInternal(CompoundTag compound) {
        ListTag modifiers = compound.getList("modifiers", 10);
        for (Tag modifierCompound : modifiers) {
            if (modifierCompound instanceof CompoundTag) {
                String name = ((CompoundTag) modifierCompound).getString("name");
                MagicMirrorModifier modifier = MagicMirrorModifier.getModifier(name);
                if (modifier != null) {
                    modifier.apply(this, (CompoundTag) modifierCompound);
                }
            }
        }
    }

    /**
     * @return The reflection in the mirror.
     */
    @Nullable
    public Reflection getReflection() {
        return reflection;
    }

    /**
     * @return A list of all the current modifiers of the mirror.
     */
    public List<MagicMirrorBlockEntityModifier> getModifiers() {
        return Collections.unmodifiableList(modifiers);
    }

    /**
     * Called when a player uses the magic mirror..
     *
     * @param playerIn The player that activated the mirror.
     * @param hand     The hand used by the player to activate the mirror.
     * @return Whether activation was successful.
     */
    public boolean tryActivate(Player playerIn, InteractionHand hand) {
        return modifiers.stream().anyMatch(modifier -> modifier.tryPlayerActivate(this, playerIn, hand));
    }

    /**
     * Add a modifier to the mirror.
     *
     * @param modifier The modifier to add. Must be verified to be applicable.
     */
    public void addModifier(MagicMirrorBlockEntityModifier modifier) {
        modifiers.add(modifier);
        modifier.activate(this);
        setChanged();
    }

    /**
     * Remove all modifiers from the block entity.
     * This is used when the block is destroyed, and may have side effects such as spawning item entities into the
     * world.
     *
     * @param worldIn The world containing the removed block.
     * @param pos     The position of the block that was removed.
     */
    public void removeModifiers(Level worldIn, BlockPos pos) {
        for (MagicMirrorBlockEntityModifier modifier : modifiers) {
            modifier.deactivate(this);
            modifier.remove(worldIn, pos);
        }
        modifiers.clear();
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            loadInternal(tag);
        }
    }
}
