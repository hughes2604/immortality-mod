package com.billy.immortality.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ImmortalityStatusPayload(boolean showScreen, boolean immortal, boolean nullified)
        implements CustomPayload {
    public static final Id<ImmortalityStatusPayload> ID =
            new Id<>(Identifier.of("immortality", "immortality_status"));
    public static final PacketCodec<RegistryByteBuf, ImmortalityStatusPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, ImmortalityStatusPayload::showScreen,
            PacketCodecs.BOOLEAN, ImmortalityStatusPayload::immortal,
            PacketCodecs.BOOLEAN, ImmortalityStatusPayload::nullified,
            ImmortalityStatusPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
