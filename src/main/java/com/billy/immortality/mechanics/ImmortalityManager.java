package com.billy.immortality.mechanics;

import com.billy.immortality.ImmortalityMod;
import com.billy.immortality.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.TeleportTarget;

/** All immortality logic lives here. */
public final class ImmortalityManager {
    public static final int MAX_LEVEL = 3;
    public static final int PENALTY_TICKS = 3 * 60 * 20;
    private static final int TRANSFORMATION_TICKS = 40;
    private static final double PARTICLE_RADIUS = 0.36;
    private static final DustParticleEffect IMMORTALITY_PARTICLES = new DustParticleEffect(0xFFB833, 0.72F);
    private static final DustParticleEffect MORTALITY_PARTICLES = new DustParticleEffect(0xE61414, 0.72F);
    private static final Map<UUID, TransformationVisual> ACTIVE_TRANSFORMATIONS = new HashMap<>();

    public static final AttachmentType<ImmortalityData> DATA = AttachmentRegistry.create(
            Identifier.of(ImmortalityMod.MOD_ID, "data"),
            builder -> builder.initializer(() -> ImmortalityData.DEFAULT)
                    .persistent(ImmortalityData.CODEC).copyOnDeath());

    private ImmortalityManager() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ImmortalityManager::tickTransformationParticles);
    }

    public static boolean isImmortal(ServerPlayerEntity player) {
        return player.getAttachedOrCreate(DATA).immortal();
    }

    public static void makeImmortal(ServerPlayerEntity player) {
        player.setAttached(DATA, player.getAttachedOrCreate(DATA).withImmortal(true));
        player.sendMessage(Text.literal("YOU ARE NOW IMMORTAL").formatted(Formatting.GOLD), false);
        player.playSoundToPlayer(SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 0.8F, 1.0F);
        startTransformation(player, IMMORTALITY_PARTICLES);
    }

    /** Elixir of Mortality: remove immortality, clear penalty, resume normal death. */
    public static void makeMortal(ServerPlayerEntity player) {
        player.setAttached(DATA, ImmortalityData.DEFAULT);
        player.removeStatusEffect(StatusEffects.SLOWNESS);
        player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
        player.sendMessage(Text.literal("YOU ARE NOW MORTAL").formatted(Formatting.RED), false);
        player.playSoundToPlayer(SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 0.8F, 0.9F);
        startTransformation(player, MORTALITY_PARTICLES);
    }

    private static void startTransformation(ServerPlayerEntity player, DustParticleEffect particles) {
        ACTIVE_TRANSFORMATIONS.put(player.getUuid(), new TransformationVisual(particles));
    }

    private static void tickTransformationParticles(MinecraftServer server) {
        Iterator<Map.Entry<UUID, TransformationVisual>> iterator = ACTIVE_TRANSFORMATIONS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, TransformationVisual> entry = iterator.next();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            TransformationVisual visual = entry.getValue();
            if (player == null || visual.age >= TRANSFORMATION_TICKS) {
                iterator.remove();
                continue;
            }
            spawnTransformationShell(player, visual.particles, visual.age++);
        }
    }

    /** Emits a slowly turning, close-fitting shell of fine particles for two seconds. */
    private static void spawnTransformationShell(ServerPlayerEntity player, DustParticleEffect particles, int age) {
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        double baseY = player.getY();
        double height = player.getHeight();
        double rotation = age * 0.24;
        for (int level = 0; level < 4; level++) {
            double y = baseY + 0.08 + (height - 0.16) * level / 3.0;
            double angle = rotation + level * 0.35;
            for (int side = 0; side < 2; side++) {
                double pointAngle = angle + side * Math.PI;
                double x = player.getX() + Math.cos(pointAngle) * PARTICLE_RADIUS;
                double z = player.getZ() + Math.sin(pointAngle) * PARTICLE_RADIUS;
                world.spawnParticles(particles, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
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
        player.sendMessage(Text.literal("You died... or did you?").formatted(Formatting.GOLD), false);
        player.setHealth(1.0F);
        player.extinguish();
        player.fallDistance = 0;
        if (void_) {
            TeleportTarget target = player.getRespawnTarget(true, TeleportTarget.NO_OP);
            player.teleportTo(target);
            player.fallDistance = 0;
        }
    }

    /** Clear only death penalties before a true death so they are not copied to the respawned player. */
    public static void clearPenaltyForRealDeath(ServerPlayerEntity player) {
        ImmortalityData data = player.getAttachedOrCreate(DATA);
        player.setAttached(DATA, data.withPenalty(0, 0L));
        player.removeStatusEffect(StatusEffects.SLOWNESS);
        player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
    }

    /** Whether a powered nullifier occupies a 10-block sphere around the player. */
    public static boolean isInNullifierField(ServerPlayerEntity player, int radius) {
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        double centerX = player.getX();
        double centerY = player.getY() + player.getHeight() * 0.5;
        double centerZ = player.getZ();
        int radiusSquared = radius * radius;
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int x = (int) Math.floor(centerX) - radius; x <= (int) Math.floor(centerX) + radius; x++) {
            for (int y = (int) Math.floor(centerY) - radius; y <= (int) Math.floor(centerY) + radius; y++) {
                for (int z = (int) Math.floor(centerZ) - radius; z <= (int) Math.floor(centerZ) + radius; z++) {
                    double dx = x + 0.5 - centerX;
                    double dy = y + 0.5 - centerY;
                    double dz = z + 0.5 - centerZ;
                    if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                        continue;
                    }
                    BlockState state = world.getBlockState(pos.set(x, y, z));
                    if (state.isOf(ModBlocks.IMMORTALITY_NULLIFIER)
                            && state.get(Properties.POWERED)) {
                        return true;
                    }
                }
            }
        }
        return false;
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

    private static final class TransformationVisual {
        private final DustParticleEffect particles;
        private int age;
        private TransformationVisual(DustParticleEffect particles) { this.particles = particles; }
    }
}
