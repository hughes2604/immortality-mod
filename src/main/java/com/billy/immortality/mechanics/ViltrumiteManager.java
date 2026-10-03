package com.billy.immortality.mechanics;

import com.billy.immortality.items.ModItems;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Powers supplied by the Viltrumite chestplate. The server is authoritative:
 * flight and all combat effects are granted only while the chestplate is worn.
 */
public final class ViltrumiteManager {
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

    private static void updatePowers(ServerPlayerEntity player) {
        boolean wearing = isWearingChestplate(player);
        var abilities = player.getAbilities();

        if (wearing) {
            if (!player.isCreative() && !player.isSpectator() && !abilities.allowFlying) {
                abilities.allowFlying = true;
                FLIGHT_GRANTED.add(player.getUuid());
                player.sendAbilitiesUpdate();
            }

            // Refresh once per second. The short duration makes removal of the
            // chestplate switch the buffs off promptly without persistent data.
            if (player.age % 20 == 0) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 40, 1, false, false, true));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 0, false, false, true));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 1, false, false, true));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 1, false, false, true));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 40, 1, false, false, true));
            }
        } else if (FLIGHT_GRANTED.remove(player.getUuid())) {
            // Never revoke flight belonging to creative or spectator mode.
            if (!player.isCreative() && !player.isSpectator()) {
                abilities.flying = false;
                abilities.allowFlying = false;
                player.sendAbilitiesUpdate();
            }
        }
    }

    private static boolean isWearingChestplate(ServerPlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.CHEST).isOf(ModItems.VILTRUMITE_CHESTPLATE);
    }
}
