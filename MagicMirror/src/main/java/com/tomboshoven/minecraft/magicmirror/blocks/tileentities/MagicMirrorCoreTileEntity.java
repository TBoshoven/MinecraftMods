package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
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
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * The tnet.minecraft.world.level.block.state.properties.BlockStateProperties block that has all the reflection logic.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorCoreTileEntity extends MagicMirrorBaseTileEntity implements TickableBlockEntity {
    /**
     * Number of ticks between updating who we're reflecting
     */
    private static final int REFLECTION_UPDATE_INTERVAL = 10;
    /**
     * The list of all modifiers to the mirror.
     */
    private final List<MagicMirrorTileEntityModifier> modifiers = Lists.newArrayList();
    /**
     * The reflection object, used for keeping track of who is being reflected and rendering.
     */
    private final Reflection reflection;
    // Start the update counter at its max, so we update on the first tick.
    private int reflectionUpdateCounter = REFLECTION_UPDATE_INTERVAL;

    public MagicMirrorCoreTileEntity() {
        super(TileEntities.MAGIC_MIRROR_CORE.get());

        reflection = DistExecutor.unsafeRunForDist(() -> ReflectionClient::new, () -> Reflection::new);
    }

    /**
     * Find all players that can be reflected.
     *
     * @param world       The world in which the tile entity exists.
     * @param ownPosition The position of the tile entity in the world.
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
     * @param world       The world in which the tile entity exists.
     * @param ownPosition The position of the tile entity in the world.
     * @return A player to reflect, or null if there are no valid candidates.
     */
    @Nullable
    private static Player findPlayerToReflect(EntityGetter world, BlockPos ownPosition) {
        List<Player> players = findReflectablePlayers(world, ownPosition);
        if (players.isEmpty()) {
            return null;
        }
        return Collections.min(players, Comparator.comparingDouble(player -> ownPosition.distSqr(player.getX(), player.getY(), player.getZ(), true)));
    }

    @Nullable
    @Override
    protected MagicMirrorCoreTileEntity getCore() {
        return this;
    }

    /**
     * Update the reflection to show the closest player.
     */
    private void updateReflection() {
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

    @Override
    public void tick() {
        if (reflectionUpdateCounter++ == REFLECTION_UPDATE_INTERVAL) {
            reflectionUpdateCounter = 0;
            updateReflection();
        }
        modifiers.forEach(MagicMirrorTileEntityModifier::coolDown);
    }

    @Override
    public void onChunkUnloaded() {
        // Stop reflecting to unload the textures and frame buffer
        reflection.stopReflecting();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        // Stop reflecting, but prepare to restart the next tick, in case we get validated again
        // We need to make sure that we stop reflecting when the tile entity is destroyed, so we don't leak any frame
        // buffers and textures
        reflection.stopReflecting();
        reflectionUpdateCounter = REFLECTION_UPDATE_INTERVAL;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        return writeInternal(super.save(compound));
    }

    /**
     * Write out the data for the magic mirror core to an NBT compound.
     *
     * @param compound The NBT compound to write to.
     * @return The nbt parameter, for chaining.
     */
    private CompoundTag writeInternal(CompoundTag compound) {
        ListTag modifierList = new ListTag();
        for (MagicMirrorTileEntityModifier modifier : modifiers) {
            CompoundTag modifierCompound = new CompoundTag();
            modifierCompound.putString("name", modifier.getName());
            modifierList.add(modifier.write(modifierCompound));
        }
        compound.put("modifiers", modifierList);
        return compound;
    }

    @Override
    public void load(BlockState state, CompoundTag compound) {
        super.load(state, compound);
        readInternal(compound);
    }

    private void readInternal(CompoundTag compound) {
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

    @Nullable
    @Override
    public Reflection getReflection() {
        return reflection;
    }

    @Override
    public List<MagicMirrorTileEntityModifier> getModifiers() {
        return Collections.unmodifiableList(modifiers);
    }

    @Override
    public boolean tryActivate(Player playerIn, InteractionHand hand) {
        return modifiers.stream().anyMatch(modifier -> modifier.tryPlayerActivate(this, playerIn, hand));
    }

    @Override
    public void addModifier(MagicMirrorTileEntityModifier modifier) {
        modifiers.add(modifier);
        modifier.activate(this);
        setChanged();
    }

    @Override
    public void removeModifiers(Level worldIn, BlockPos pos) {
        for (MagicMirrorTileEntityModifier modifier : modifiers) {
            modifier.deactivate(this);
            modifier.remove(worldIn, pos);
        }
        modifiers.clear();
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        readInternal(pkt.getTag());
    }
}
