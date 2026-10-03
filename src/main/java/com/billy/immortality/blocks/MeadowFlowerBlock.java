package com.billy.immortality.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.effect.StatusEffect;

public final class MeadowFlowerBlock extends FlowerBlock {
    private final RegistryEntry<StatusEffect> standingEffect;
    private final int amplifier;

    public MeadowFlowerBlock(RegistryEntry<StatusEffect> stewEffect, float stewDuration,
                             RegistryEntry<StatusEffect> standingEffect, int amplifier, Settings settings) {
        super(stewEffect, stewDuration, settings);
        this.standingEffect = standingEffect;
        this.amplifier = amplifier;
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient() && entity instanceof ServerPlayerEntity player
                && player.getBlockPos().down().equals(pos)) {
            player.addStatusEffect(new StatusEffectInstance(standingEffect, 5, amplifier));
        }
        super.onEntityCollision(state, world, pos, entity);
    }
}
