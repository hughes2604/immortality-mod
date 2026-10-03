package com.billy.immortality.client;

import com.billy.immortality.network.ImmortalityMenuRequestPayload;
import com.billy.immortality.network.ImmortalityStatusPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class ImmortalityClient implements ClientModInitializer {
    private static KeyBinding openStatusKey;

    @Override
    public void onInitializeClient() {
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("immortality", "main"));
        openStatusKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.immortality.open_status", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, category));

        ClientPlayNetworking.registerGlobalReceiver(ImmortalityStatusPayload.ID, (payload, context) ->
                context.client().execute(() -> {
                    if (payload.showScreen()) {
                        if (payload.immortal()) {
                            context.client().setScreen(new ImmortalityStatusScreen(payload.nullified()));
                        }
                        return;
                    }

                    if (context.client().currentScreen instanceof ImmortalityStatusScreen screen) {
                        if (payload.immortal()) {
                            screen.setNullified(payload.nullified());
                        } else {
                            context.client().setScreen(null);
                        }
                    }
                }));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openStatusKey.wasPressed()) {
                if (client.player != null && client.getNetworkHandler() != null) {
                    ClientPlayNetworking.send(new ImmortalityMenuRequestPayload());
                }
            }
        });
    }
}
