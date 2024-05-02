package com.tomboshoven.minecraft.magicmirror.blocks.entities;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifiers;
import com.tomboshoven.minecraft.magicmirror.events.MagicMirrorModifiersUpdatedEvent;
import com.tomboshoven.minecraft.magicmirror.events.MagicMirrorReflectedEntityEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The block that has all the reflection logic.
 */
public class MagicMirrorCoreBlockEntity extends BlockEntity {
    /**
     * The list of all modifiers to the mirror.
     */
    private final List<MagicMirrorBlockEntityModifier> modifiers = Lists.newArrayList();

    /**
     * The currently reflected entity, if any.
     */
    @Nullable
    private Entity reflectedEntity = null;

    public MagicMirrorCoreBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MAGIC_MIRROR_CORE.get(), pos, state);
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

    /**
     * Cool down all modifiers.
     */
    public void coolDown() {
        modifiers.forEach(MagicMirrorBlockEntityModifier::coolDown);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
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
            ResourceLocation modifierId = modifier.getModifier().getRegistryName();
            if (modifierId == null) {
                continue;
            }
            CompoundTag modifierCompound = new CompoundTag();
            modifierCompound.putString("id", modifierId.toString());
            modifierList.add(modifier.write(modifierCompound));
        }
        compound.put("modifiers", modifierList);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        readInternal(compound);
    }

    private void readInternal(CompoundTag compound) {
        ListTag modifiers = compound.getList("modifiers", 10);
        for (Tag modifierTag : modifiers) {
            if (modifierTag instanceof CompoundTag modifierCompound) {
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

                MagicMirrorModifier modifier = MagicMirrorModifiers.MAGIC_MIRROR_MODIFIER_REGISTRY.get().getValue(id);
                if (modifier != null) {
                    modifier.apply(this, modifierCompound);
                }
            }
        }
        postModifiersUpdate();
    }

    /**
     * @return A list of all the current modifiers of the mirror.
     */
    public List<MagicMirrorBlockEntityModifier> getModifiers() {
        return Collections.unmodifiableList(modifiers);
    }

    /**
     * Called when a player uses the magic mirror.
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
        postModifiersUpdate();
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
        postModifiersUpdate();
    }

    /**
     * Post an event indicating a change to the set of modifiers on the mirror.
     */
    private void postModifiersUpdate() {
        MinecraftForge.EVENT_BUS.post(new MagicMirrorModifiersUpdatedEvent(this));
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
        readInternal(pkt.getTag());
    }

    /**
     * @return the current reflected entity, if any.
     */
    @Nullable
    public Entity getReflectedEntity() {
        return reflectedEntity;
    }
}
