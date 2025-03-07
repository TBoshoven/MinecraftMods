package com.tomboshoven.minecraft.magicdoorknob.client.clientextensions;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

class MagicDoorwayClientBlockExtensions implements IClientBlockExtensions {
    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
        // Just remove all hit particles.
        // While we could make this work, the interface for this is rather awkward and the benefit is small.
        return true;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        // Remove all breaking particles to prevent particle showers when closing a door.
        return true;
    }
}
