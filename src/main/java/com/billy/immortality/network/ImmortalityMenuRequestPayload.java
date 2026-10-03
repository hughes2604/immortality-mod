package com.billy.immortality.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ImmortalityMenuRequestPayload() implements CustomPayload {
    public static final Id<ImmortalityMenuRequestPayload> ID =
            new Id<>(Identifier.of("immortality", "open_status"));
    public static final PacketCodec<RegistryByteBuf, ImmortalityMenuRequestPayload> CODEC =
            PacketCodec.unit(new ImmortalityMenuRequestPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
