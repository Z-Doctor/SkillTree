package com.zdoctorsmods.skilltreemod.network.packets;

import net.minecraft.network.FriendlyByteBuf;

public interface Packet {
    /**
     * Writes the raw packet data to the data stream.
     */
    void write(FriendlyByteBuf pBuffer);

    /**
     * Whether decoding errors will be ignored for this packet.
     */
    default boolean isSkippable() {
        return false;
    }
}
