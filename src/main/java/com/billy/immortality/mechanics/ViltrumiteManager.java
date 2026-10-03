package com.billy.immortality.mechanics;

import com.billy.immortality.items.ModItems;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Server-authoritative Viltrumite flight and physical abilities.
 * Flight speed is an ability the player learns to control, from a steady hover
 * through a destructive redline charge.
 */
public final class ViltrumiteManager {
    private static final float[] FLIGHT_SPEEDS = {0.05F, 0.10F, 0.22F, 0.55F, 1.20F};
    private static final String[] SPEED_NAMES = {"hover", "cruise", "fast", "supersonic", "redline"};
    private static final Map<UUID, Integer> SPEED_TIERS = new HashMap<>();
    private static final Set<UUID> FLIGHT_GRANTED = new HashSet<>();

    private ViltrumiteManager() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                updatePowers(player);
            }
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player
                    && isWearingChestplate(player)
                    && source.isOf(DamageTypes.FALL)) {
                return false;
            }
            return true;
        });
    }

    /** Called only for the authenticated player who sent the speed-control key press. */
    public static void cycleSpeed(ServerPlayerEntity player, boolean slower) {
        if (!isWearingChestplate(player)) {
            return;
        }

        int oldTier = SPEED_TIERS.getOrDefault(player.getUuid(), 0);
        int newTier = Math.floorMod(oldTier + (slower ? -1 : 1), FLIGHT_SPEEDS.length);
        SPEED_TIERS.put(player.getUuid(), newTier);
        player.getAbilities().setFlySpeed(FLIGHT_SPEEDS[newTier]);
        player.sendAbilitiesUpdate();
        player.sendMessage(Text.translatable("message.immortality.flight_speed." + SPEED_NAMES[newTier]), true);

        if (newTier >= 3 && newTier > oldTier) {
            ServerWorld world = (ServerWorld) player.getEntityWorld();
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM,
                    SoundCategory.PLAYERS, 0.55F, newTier == 4 ? 0.72F : 1.05F);
            world.spawnParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 0.8,
                    player.getZ(), newTier == 4 ? 5 : 2, 0.35, 0.35, 0.35, 0.03);
        }
    }

    private static void updatePowers(ServerPlayerEntity player) {
        boolean wearing = isWearingChestplate(player);
        var abilities = player.getAbilities();
        UUID id = player.getUuid();

        if (wearing) {
            if (!player.isCreative() && !player.isSpectator() && !abilities.allowFlying) {
                abilities.allowFlying = true;
                FLIGHT_GRANTED.add(id);
                player.sendAbilitiesUpdate();
            }

            int tier = SPEED_TIERS.computeIfAbsent(id, ignored -> 0);
            if (Math.abs(abilities.getFlySpeed() - FLIGHT_SPEEDS[tier]) > 0.0001F) {
                abilities.setFlySpeed(FLIGHT_SPEEDS[tier]);
                player.sendAbilitiesUpdate();
            }

            if (player.age % 20 == 0) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 40, 1, false, false, true));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 0, false, false, true));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 1, false, false, true));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 1, false, false, true));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 40, 1, false, false, true));
            }

            if (tier >= 3 && player.age % 2 == 0) {
                Vec3d velocity = player.getVelocity();
                if (velocity.lengthSquared() > 0.08) {
                    Vec3d trail = player.getPos().subtract(velocity.normalize().multiply(0.7))
                            .add(0.0, player.getHeight() * 0.55, 0.0);
                    ((ServerWorld) player.getEntityWorld()).spawnParticles(
                            ParticleTypes.CLOUD, trail.x, trail.y, trail.z,
                            tier == 4 ? 3 : 1, 0.12, 0.12, 0.12, 0.015);
                    if (tier == 4 && player.isSprinting() && player.getAbilities().allowModifyWorld) {
                        punchThroughBlocks(player, (ServerWorld) player.getEntityWorld(), velocity);
                    }
                }
            }
        } else {
            if (FLIGHT_GRANTED.remove(id)) {
                // Never revoke flight belonging to creative or spectator mode.
                if (!player.isCreative() && !player.isSpectator()) {
                    abilities.flying = false;
                    abilities.allowFlying = false;
                    player.sendAbilitiesUpdate();
                }
            }
            if (SPEED_TIERS.remove(id) != null) {
                abilities.setFlySpeed(0.05F);
                player.sendAbilitiesUpdate();
            }
        }
    }

    /**
     * At redline while sprinting, the Viltrumite clears a narrow flight path.
     * Unbreakable blocks and block entities (chests, machines, spawners, etc.)
     * are preserved; cleared blocks do not drop items.
     */
    private static void punchThroughBlocks(ServerPlayerEntity player, ServerWorld world, Vec3d velocity) {
        Vec3d forward = velocity.normalize();
        Vec3d referenceUp = Math.abs(forward.y) > 0.92 ? new Vec3d(1.0, 0.0, 0.0) : new Vec3d(0.0, 1.0, 0.0);
        Vec3d right = forward.crossProduct(referenceUp).normalize();
        Vec3d up = right.crossProduct(forward).normalize();
        Vec3d origin = player.getPos().add(0.0, player.getHeight() * 0.52, 0.0);
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int depth = 1; depth <= 3; depth++) {
            Vec3d center = origin.add(forward.multiply(depth * 0.85));
            for (int side = -1; side <= 1; side++) {
                for (int vertical = -1; vertical <= 1; vertical++) {
                    if (side == 0 && vertical == 0) {
                        // Keep the tunnel tight: one central block plus the near corners.
                    }
                    Vec3d sample = center.add(right.multiply(side * 0.42)).add(up.multiply(vertical * 0.42));
                    BlockPos blockPos = pos.set(sample.x, sample.y, sample.z);
                    BlockState state = world.getBlockState(blockPos);
                    if (state.isAir() || state.hasBlockEntity() || state.isIn(BlockTags.WITHER_IMMUNE)
                            || state.getHardness(world, blockPos) < 0.0F) {
                        continue;
                    }
                    world.breakBlock(blockPos, false, player);
                }
            }
        }
    }

    private static boolean isWearingChestplate(ServerPlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.CHEST).isOf(ModItems.VILTRUMITE_CHESTPLATE);
    }
}
