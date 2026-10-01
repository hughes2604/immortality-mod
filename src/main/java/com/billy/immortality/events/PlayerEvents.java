package com.billy.immortality.events;

import com.billy.immortality.mechanics.ImmortalityManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class PlayerEvents {

    private PlayerEvents() {
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                ImmortalityManager.restorePenalty(handler.getPlayer()));

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                ImmortalityManager.restorePenalty(newPlayer));
    }
}
