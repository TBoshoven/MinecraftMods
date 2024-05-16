package com.tomboshoven.minecraft.magicmirror.blocks.entities;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifiers;
import com.tomboshoven.minecraft.magicmirror.events.MagicMirrorModifiersUpdatedEvent;
import com.tomboshoven.minecraft.magicmirror.events.MagicMirrorReflectedEntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEntityReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The tile entity for the bottom mirror block; this is the block that has all the reflection logic.
 */
public class MagicMirrorCoreBlockEntity extends MagicMirrorBaseBlockEntity implements ITickableTileEntity {
    /**
     * Number of ticks between updating who we're reflecting
     */
    private static final int REFLECTION_UPDATE_INTERVAL = 10;
    /**
     * The list of all modifiers to the mirror.
     */
    private final List<MagicMirrorBlockEntityModifier> modifiers = Lists.newArrayList();

    /**
     * The currently reflected entity, if any.
     */
    @Nullable
    private Entity reflectedEntity = null;
    // Start the update counter at its max, so we update on the first tick.
    private int reflectionUpdateCounter = REFLECTION_UPDATE_INTERVAL;

    public MagicMirrorCoreBlockEntity() {
        super(Objects.requireNonNull(BlockEntities.MAGIC_MIRROR_CORE.get()));
    }

    /**
     * Find all players that can be reflected.
     *
     * @param world       The world in which the tile entity exists.
     * @param ownPosition The position of the tile entity in the world.
     * @return A list of players that are candidates for reflecting.
     */
    private static List<PlayerEntity> findReflectablePlayers(IEntityReader world, BlockPos ownPosition) {
        // TODO: Add facing limitations
        AxisAlignedBB scanBB = new AxisAlignedBB(ownPosition.offset(-10, -4, -10), ownPosition.offset(10, 4, 10));
        List<PlayerEntity> playerEntities = world.getEntitiesOfClass(PlayerEntity.class, scanBB);
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
    private static PlayerEntity findPlayerToReflect(IEntityReader world, BlockPos ownPosition) {
        List<PlayerEntity> players = findReflectablePlayers(world, ownPosition);
        if (players.isEmpty()) {
            return null;
        }
        return Collections.min(players, Comparator.comparingDouble(player -> player.getCommandSenderBlockPosition().distSqr(ownPosition)));
    }

    @Nullable
    @Override
    public MagicMirrorCoreBlockEntity getCore() {
        return this;
    }

    /**
     * Update the reflection to show the closest player.
     */
    private void updateReflection() {
        World world = getLevel();

        if (world != null) {
            Entity newEntity = findPlayerToReflect(world, getBlockPos());
            if (newEntity != reflectedEntity) {
                reflectedEntity = newEntity;
                postReflectedEntityEvent(reflectedEntity);
            }
        }
    }

    /**
     * Post an event indicating a change to which entity is reflected.
     *
     * @param reflectedEntity the new reflected entity.
     */
    private void postReflectedEntityEvent(@Nullable Entity reflectedEntity) {
        MinecraftForge.EVENT_BUS.post(new MagicMirrorReflectedEntityEvent(this, reflectedEntity));
    }

    @Override
    public void tick() {
        if (reflectionUpdateCounter++ == REFLECTION_UPDATE_INTERVAL) {
            reflectionUpdateCounter = 0;
            updateReflection();
        }
        modifiers.forEach(MagicMirrorBlockEntityModifier::coolDown);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        return writeInternal(super.save(compound));
    }

    /**
     * Write out the data for the magic mirror core to an NBT compound.
     *
     * @param compound The NBT compound to write to.
     * @return The nbt parameter, for chaining.
     */
    private CompoundNBT writeInternal(CompoundNBT compound) {
        ListNBT modifierList = new ListNBT();
        for (MagicMirrorBlockEntityModifier modifier : modifiers) {
            ResourceLocation modifierId = modifier.getModifier().getRegistryName();
            if (modifierId == null) {
                continue;
            }
            CompoundNBT modifierCompound = new CompoundNBT();
            modifierCompound.putString("id", modifierId.toString());
            modifierList.add(modifier.write(modifierCompound));
        }
        compound.put("modifiers", modifierList);
        return compound;
    }

    @Override
    public void load(CompoundNBT compound) {
        super.load(compound);
        ListNBT modifiers = compound.getList("modifiers", 10);
        for (INBT modifierTag : modifiers) {
            if (modifierTag instanceof CompoundNBT) {
                CompoundNBT modifierCompound = (CompoundNBT) modifierTag;
                String idStr = modifierCompound.getString("id");
                ResourceLocation id;
                if (idStr.isEmpty()) {
                    // Fall back on old "name" field
                    // TODO: Remove fallback
                    String name = modifierCompound.getString("name");
                    id = new ResourceLocation(MagicMirrorMod.MOD_ID, name);
                }
                else {
                    id = ResourceLocation.tryParse(idStr);
                }

                MagicMirrorModifier modifier = MagicMirrorModifiers.MAGIC_MIRROR_MODIFIER_REGISTRY.getValue(id);
                if (modifier != null) {
                    modifier.apply(this, modifierCompound);
                }
            }
        }
        postModifiersUpdate();
    }

    @Override
    public List<MagicMirrorBlockEntityModifier> getModifiers() {
        return Collections.unmodifiableList(modifiers);
    }

    @Override
    public boolean tryActivate(PlayerEntity playerIn, Hand hand) {
        return modifiers.stream().anyMatch(modifier -> modifier.tryPlayerActivate(this, playerIn, hand));
    }

    @Override
    public void addModifier(MagicMirrorBlockEntityModifier modifier) {
        modifiers.add(modifier);
        modifier.activate(this);
        setChanged();
        postModifiersUpdate();
    }

    @Override
    public void removeModifiers(World worldIn, BlockPos pos) {
        for (MagicMirrorBlockEntityModifier modifier : modifiers) {
            modifier.deactivate(this);
            modifier.remove(worldIn, pos);
        }
        modifiers.clear();
        postModifiersUpdate();
    }

    /**
     * Post an event indicating a change to the set of modifiers on the mirror.
     */
    private void postModifiersUpdate() {
        MinecraftForge.EVENT_BUS.post(new MagicMirrorModifiersUpdatedEvent(this));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return writeInternal(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(pkt.getTag());
    }

    /**
     * @return the current reflected entity, if any.
     */
    @Nullable
    public Entity getReflectedEntity() {
        return reflectedEntity;
    }
}
