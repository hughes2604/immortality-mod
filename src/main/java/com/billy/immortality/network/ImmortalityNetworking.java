package com.billy.immortality.network;

import com.billy.immortality.mechanics.ImmortalityManager;
import com.billy.immortality.mechanics.ViltrumiteManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ImmortalityNetworking {
    private ImmortalityNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(
                ImmortalityMenuRequestPayload.ID, ImmortalityMenuRequestPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(
                ViltrumiteSpeedPayload.ID, ViltrumiteSpeedPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(
                ImmortalityStatusPayload.ID, ImmortalityStatusPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ImmortalityMenuRequestPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            boolean immortal = ImmortalityManager.isImmortal(player);
            boolean nullified = immortal && ImmortalityManager.isInNullifierField(player, 10);
            ServerPlayNetworking.send(player, new ImmortalityStatusPayload(true, immortal, nullified));
        });

        ServerPlayNetworking.registerGlobalReceiver(ViltrumiteSpeedPayload.ID, (payload, context) ->
                ViltrumiteManager.cycleSpeed(context.player(), payload.slower()));
    }

    public static void sendStatus(ServerPlayerEntity player, boolean immortal, boolean nullified) {
        ServerPlayNetworking.send(player, new ImmortalityStatusPayload(false, immortal, nullified));
    }
}
