package com.billy.immortality.events;

import com.billy.immortality.mechanics.ImmortalityManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public final class DeathEvents {

    private DeathEvents() {
    }

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player && ImmortalityManager.isImmortal(player)) {
                ImmortalityManager.handleLethalHit(player, source);
                return false; // cancel the real death
            }
            return true;
        });
    }
}
