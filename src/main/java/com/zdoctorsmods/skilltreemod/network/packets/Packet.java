package com.zdoctorsmods.skilltreemod.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;

public interface Packet {
    public static final NetworkDirection TO_SERVER = NetworkDirection.PLAY_TO_SERVER;
    public static final NetworkDirection TO_CLIENT = NetworkDirection.PLAY_TO_CLIENT;

    default ServerPlayer getSender() {
        return null;
    }

    default void setSender(ServerPlayer sender) {

    }

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
