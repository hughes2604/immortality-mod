package com.billy.immortality.mechanics;

import com.billy.immortality.ImmortalityMod;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.TeleportTarget;
import org.joml.Vector3f;

/**
 * All immortality logic lives here. Everything is event-driven: nothing runs per tick.
 */
public final class ImmortalityManager {

    public static final int MAX_LEVEL = 3;
    public static final int PENALTY_TICKS = 3 * 60 * 20;

    private static final DustParticleEffect IMMORTALITY_PARTICLES =
            new DustParticleEffect(new Vector3f(1.0F, 0.72F, 0.12F), 0.8F);
    private static final DustParticleEffect MORTALITY_PARTICLES =
            new DustParticleEffect(new Vector3f(0.9F, 0.08F, 0.08F), 0.8F);

    public static final AttachmentType<ImmortalityData> DATA = AttachmentRegistry.create(
            Identifier.of(ImmortalityMod.MOD_ID, "data"),
            builder -> builder
                    .initializer(() -> ImmortalityData.DEFAULT)
                    .persistent(ImmortalityData.CODEC)
                    .copyOnDeath());

    private ImmortalityManager() {
    }

    /** Forces class loading so the attachment is registered during mod init. */
    public static void register() {
    }

    public static boolean isImmortal(ServerPlayerEntity player) {
        return player.getAttachedOrCreate(DATA).immortal();
    }

    public static void makeImmortal(ServerPlayerEntity player) {
        player.setAttached(DATA, player.getAttachedOrCreate(DATA).withImmortal(true));
        player.sendMessage(Text.literal("YOU ARE NOW IMMORTAL").formatted(Formatting.GOLD), false);
        player.playSoundToPlayer(SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 0.8F, 1.0F);
        spawnTransformationParticles(player, IMMORTALITY_PARTICLES);
    }

    /** Elixir of Mortality: remove immortality, clear penalty, resume normal death. */
    public static void makeMortal(ServerPlayerEntity player) {
        player.setAttached(DATA, ImmortalityData.DEFAULT);
        player.removeStatusEffect(StatusEffects.SLOWNESS);
        player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
        player.sendMessage(Text.literal("YOU ARE NOW MORTAL").formatted(Formatting.RED), false);
        player.playSoundToPlayer(SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 0.8F, 0.9F);
        spawnTransformationParticles(player, MORTALITY_PARTICLES);
    }

    private static void spawnTransformationParticles(ServerPlayerEntity player, DustParticleEffect particles) {
        double x = player.getX();
        double z = player.getZ();
        double legsY = player.getY() + player.getHeight() * 0.2;
        double bodyY = player.getY() + player.getHeight() * 0.65;
        player.getServerWorld().spawnParticles(particles, x, legsY, z, 7, 0.28, 0.12, 0.28, 0.01);
        player.getServerWorld().spawnParticles(particles, x, bodyY, z, 7, 0.28, 0.18, 0.28, 0.01);
    }

    /** Called when an immortal player takes a hit that would have killed them. */
    public static void handleLethalHit(ServerPlayerEntity player, DamageSource source) {
        boolean void_ = source.isOf(DamageTypes.OUT_OF_WORLD);
        ImmortalityData data = player.getAttachedOrCreate(DATA);
        long now = currentTime(player);

        int level;
        if (void_) {
            level = MAX_LEVEL;
        } else {
            int current = data.penaltyExpiry() > now ? data.penaltyLevel() : 0;
            level = Math.min(current + 1, MAX_LEVEL);
        }

        player.setAttached(DATA, data.withPenalty(level, now + PENALTY_TICKS));
        applyEffects(player, level, PENALTY_TICKS);

        player.setHealth(1.0F); // Half a heart: survive without being healed.
        player.extinguish();
        player.fallDistance = 0;

        if (void_) {
            TeleportTarget target = player.getRespawnTarget(true, TeleportTarget.NO_OP);
            player.teleportTo(target);
            player.fallDistance = 0;
        }
    }

    /** Re-applies the remaining penalty after login or respawn. */
    public static void restorePenalty(ServerPlayerEntity player) {
        ImmortalityData data = player.getAttachedOrCreate(DATA);
        if (data.penaltyLevel() <= 0) {
            return;
        }
        long remaining = data.penaltyExpiry() - currentTime(player);
        if (remaining > 0) {
            applyEffects(player, data.penaltyLevel(), (int) Math.min(remaining, PENALTY_TICKS));
        } else {
            player.setAttached(DATA, data.withPenalty(0, 0L));
        }
    }

    private static void applyEffects(ServerPlayerEntity player, int level, int ticks) {
        int amplifier = level - 1;
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, ticks, amplifier));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, ticks, amplifier));
    }

    /** Overworld time is shared by all dimensions and only advances while the world is loaded. */
    private static long currentTime(ServerPlayerEntity player) {
        return player.getEntityWorld().getServer().getOverworld().getTime();
    }
}
