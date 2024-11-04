package com.tomboshoven.minecraft.magicmirror.blocks.entities;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifiers;
import com.tomboshoven.minecraft.magicmirror.events.MagicMirrorModifiersUpdatedEvent;
import com.tomboshoven.minecraft.magicmirror.events.MagicMirrorReflectedEntityEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;

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
        AABB scanBB = AABB.encapsulatingFullBlocks(ownPosition.offset(-10, -4, -10), ownPosition.offset(10, 4, 10));
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
        NeoForge.EVENT_BUS.post(new MagicMirrorReflectedEntityEvent(this, reflectedEntity));
    }

    /**
     * Cool down all modifiers.
     */
    public void coolDown() {
        modifiers.forEach(MagicMirrorBlockEntityModifier::coolDown);
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(compound, lookupProvider);
        saveInternal(compound, lookupProvider);
    }

    /**
     * Write out the data for the magic mirror core to an NBT compound.
     *
     * @param compound       The NBT compound to write to.
     * @param lookupProvider The holder lookup provider for serializing the state.
     */
    private void saveInternal(CompoundTag compound, HolderLookup.Provider lookupProvider) {
        ListTag modifierList = new ListTag();
        for (MagicMirrorBlockEntityModifier modifier : modifiers) {
            ResourceLocation modifierId = MagicMirrorModifiers.MAGIC_MIRROR_MODIFIER_REGISTRY.getKey(modifier.getModifier());
            if (modifierId == null) {
                continue;
            }
            CompoundTag modifierCompound = new CompoundTag();
            modifierCompound.putString("id", modifierId.toString());
            modifierList.add(modifier.write(modifierCompound, lookupProvider));
        }
        compound.put("modifiers", modifierList);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider holderLookupProvider) {
        super.loadAdditional(compound, holderLookupProvider);
        loadInternal(compound, holderLookupProvider);
    }

    private void loadInternal(CompoundTag compound, HolderLookup.Provider holderLookupProvider) {
        ListTag modifiers = compound.getList("modifiers", 10);
        for (Tag modifierTag : modifiers) {
            if (modifierTag instanceof CompoundTag modifierCompound) {
                String idStr = modifierCompound.getString("id");
                ResourceLocation id;
                if (idStr.isEmpty()) {
                    // Fall back on old "name" field
                    // TODO: Remove fallback
                    String name = modifierCompound.getString("name");
                    id = ResourceLocation.fromNamespaceAndPath(MagicMirrorMod.MOD_ID, name);
                }
                else {
                    id = ResourceLocation.tryParse(idStr);
                }

                MagicMirrorModifier modifier = MagicMirrorModifiers.MAGIC_MIRROR_MODIFIER_REGISTRY.getValue(id);
                if (modifier != null) {
                    modifier.apply(this, modifierCompound, holderLookupProvider);
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
     * @param player The player that activated the mirror.
     * @return Whether activation was successful.
     */
    public InteractionResult useWithoutItem(Player player) {
        for (MagicMirrorBlockEntityModifier modifier : modifiers) {
            InteractionResult result = modifier.useWithoutItem(this, player);
            if (result != InteractionResult.PASS) {
                return result;
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * Called when a player uses the magic mirror with an item.
     *
     * @param player   The player that activated the mirror.
     * @param heldItem The item held by the player.
     * @return Whether activation was successful.
     */
    public InteractionResult useWithItem(Player player, ItemStack heldItem) {
        for (MagicMirrorBlockEntityModifier modifier : modifiers) {
            InteractionResult result = modifier.useWithItem(this, player, heldItem);
            if (result.consumesAction()) {
                return result;
            }
        }
        return InteractionResult.PASS;
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
        NeoForge.EVENT_BUS.post(new MagicMirrorModifiersUpdatedEvent(this));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        CompoundTag updateTag = super.getUpdateTag(lookupProvider);
        saveInternal(updateTag, lookupProvider);
        return updateTag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        loadInternal(pkt.getTag(), lookupProvider);
    }

    /**
     * @return The current reflected entity, if any.
     */
    @Nullable
    public Entity getReflectedEntity() {
        return reflectedEntity;
    }
}
