package com.billy.immortality.mechanics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Persistent per-player state.
 *
 * @param immortal      whether the player is immortal
 * @param penaltyLevel  current penalty level (0 = none, 3 = max)
 * @param penaltyExpiry overworld game time (ticks) at which the penalty lapses
 */
public record ImmortalityData(boolean immortal, int penaltyLevel, long penaltyExpiry) {

    public static final ImmortalityData DEFAULT = new ImmortalityData(false, 0, 0L);

    public static final Codec<ImmortalityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("immortal").forGetter(ImmortalityData::immortal),
            Codec.INT.fieldOf("penalty_level").forGetter(ImmortalityData::penaltyLevel),
            Codec.LONG.fieldOf("penalty_expiry").forGetter(ImmortalityData::penaltyExpiry)
    ).apply(instance, ImmortalityData::new));

    public ImmortalityData withImmortal(boolean value) {
        return new ImmortalityData(value, penaltyLevel, penaltyExpiry);
    }

    public ImmortalityData withPenalty(int level, long expiry) {
        return new ImmortalityData(immortal, level, expiry);
    }
}
