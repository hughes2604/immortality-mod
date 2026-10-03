package com.billy.immortality.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** One client key press to move a speed tier up or down. */
public record ViltrumiteSpeedPayload(boolean slower) implements CustomPayload {
    public static final Id<ViltrumiteSpeedPayload> ID =
            new Id<>(Identifier.of("immortality", "viltrumite_speed"));
    public static final PacketCodec<RegistryByteBuf, ViltrumiteSpeedPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.BOOLEAN, ViltrumiteSpeedPayload::slower, ViltrumiteSpeedPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
