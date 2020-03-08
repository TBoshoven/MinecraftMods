package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection.ReflectionFactory;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The tile entity for the bottom mirror block; this is the block that has all the reflection logic.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityMagicMirrorCore extends TileEntityMagicMirrorBase implements ITickableTileEntity {
    /**
     * Number of ticks between updating who we're reflecting
     */
    private static final int REFLECTION_UPDATE_INTERVAL = 10;
    /**
     * Factory for reflections; this is a sided proxy so we spawn a renderless reflection on the server side.
     */
    @SidedProxy(
            clientSide = "com.tomboshoven.minecraft.magicmirror.reflection.ReflectionClient$ReflectionFactory",
            serverSide = "com.tomboshoven.minecraft.magicmirror.reflection.Reflection$ReflectionFactory"
    )
    private static ReflectionFactory reflectionFactory;
    /**
     * The list of all modifiers to the mirror.
     */
    private final List<MagicMirrorTileEntityModifier> modifiers = Lists.newArrayList();
    /**
     * The reflection object, used for keeping track of who is being reflected and rendering.
     */
    private Reflection reflection;
    // Start the update counter at its max, so we update on the first tick.
    private int reflectionUpdateCounter = REFLECTION_UPDATE_INTERVAL;

    public TileEntityMagicMirrorCore() {
        super(TileEntities.MAGIC_MIRROR_CORE);
    }

    /**
     * Find all players that can be reflected.
     *
     * @param world       The world in which the tile entity exists.
     * @param ownPosition The position of the tile entity in the world.
     * @return A list of players that are candidates for reflecting.
     */
    private static List<PlayerEntity> findReflectablePlayers(World world, BlockPos ownPosition) {
        // TODO: Add facing limitations
        AxisAlignedBB scanBB = new AxisAlignedBB(ownPosition.add(-10, -4, -10), ownPosition.add(10, 4, 10));
        List<PlayerEntity> playerEntities = world.getEntitiesWithinAABB(PlayerEntity.class, scanBB);
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
    private static PlayerEntity findPlayerToReflect(World world, BlockPos ownPosition) {
        List<PlayerEntity> players = findReflectablePlayers(world, ownPosition);
        if (players.isEmpty()) {
            return null;
        }
        return Collections.min(players, Comparator.comparingDouble(player -> player.getPosition().distanceSq(ownPosition)));
    }

    @Override
    public void onLoad() {
        super.onLoad();

        reflection = getWorld().isRemote ? reflectionFactory.createClient() : reflectionFactory.createServer();

        reflection.setFacing(getFacing().getHorizontalAngle());
    }

    @Nullable
    @Override
    protected TileEntityMagicMirrorCore getCore() {
        return this;
    }

    /**
     * Update the reflection to show the closest player.
     */
    private void updateReflection() {
        World world = getWorld();

        PlayerEntity playerToReflect = findPlayerToReflect(world, getPos());

        if (playerToReflect == null) {
            reflection.stopReflecting();
        } else {
            reflection.setReflectedEntity(playerToReflect);
        }
    }

    @Override
    public void tick() {
        if (reflectionUpdateCounter++ == REFLECTION_UPDATE_INTERVAL) {
            reflectionUpdateCounter = 0;
            updateReflection();
        }
        // Make sure we re-render each full tick, to make the partialTick optimization work
        reflection.forceRerender();
        modifiers.forEach(MagicMirrorTileEntityModifier::coolDown);
    }

    @Override
    public void onChunkUnload() {
        // Stop reflecting to unload the textures and frame buffer
        reflection.stopReflecting();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        // Stop reflecting, but prepare to restart the next tick, in case we get validated again
        // We need to make sure that we stop reflecting when the tile entity is destroyed, so we don't leak any frame
        // buffers and textures
        reflection.stopReflecting();
        reflectionUpdateCounter = REFLECTION_UPDATE_INTERVAL;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return writeInternal(super.write(compound));
    }

    /**
     * Write out the data for the magic mirror core to an NBT compound.
     *
     * @param compound The NBT compound to write to.
     * @return The nbt parameter, for chaining.
     */
    private CompoundNBT writeInternal(CompoundNBT compound) {
        ListNBT modifierList = new ListNBT();
        for (MagicMirrorTileEntityModifier modifier : modifiers) {
            CompoundNBT modifierCompound = new CompoundNBT();
            modifierCompound.putString("name", modifier.getName());
            modifierList.add(modifier.write(modifierCompound));
        }
        compound.put("modifiers", modifierList);
        return compound;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        ListNBT modifiers = compound.getList("modifiers", 10);
        for (INBT modifierCompound : modifiers) {
            if (modifierCompound instanceof CompoundNBT) {
                String name = ((CompoundNBT) modifierCompound).getString("name");
                MagicMirrorModifier modifier = MagicMirrorModifier.getModifier(name);
                if (modifier != null) {
                    modifier.apply(this, (CompoundNBT) modifierCompound);
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
    public boolean tryActivate(PlayerEntity playerIn, Hand hand) {
        return modifiers.stream().anyMatch(modifier -> modifier.tryPlayerActivate(this, playerIn, hand));
    }

    @Override
    public void addModifier(MagicMirrorTileEntityModifier modifier) {
        modifiers.add(modifier);
        modifier.activate(this);
        markDirty();
    }

    @Override
    public void removeModifiers(World worldIn, BlockPos pos) {
        for (MagicMirrorTileEntityModifier modifier : modifiers) {
            modifier.deactivate(this);
            modifier.remove(worldIn, pos);
        }
        modifiers.clear();
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return writeInternal(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        read(pkt.getNbtCompound());
    }
}
