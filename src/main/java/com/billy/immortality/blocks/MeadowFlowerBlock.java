package com.billy.immortality.blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public final class MeadowFlowerBlock extends FlowerBlock {
    public MeadowFlowerBlock(RegistryEntry<StatusEffect> stewEffect, float stewDuration,
                             RegistryEntry<StatusEffect> standingEffect, int amplifier, Settings settings) {
        super(stewEffect, stewDuration, settings);
    }
}
