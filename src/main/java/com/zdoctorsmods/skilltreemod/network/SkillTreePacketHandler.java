package com.zdoctorsmods.skilltreemod.network;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.zdoctorsmods.skilltreemod.SkillTreeMod;
import com.zdoctorsmods.skilltreemod.client.ClientMain;
import com.zdoctorsmods.skilltreemod.network.packets.ClientBoundUpdateLocalizationPacket;
import com.zdoctorsmods.skilltreemod.network.packets.ClientboundUpdateSkillsPacket;
import com.zdoctorsmods.skilltreemod.network.packets.Packet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class SkillTreePacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SkillTreeMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    static int packetId;

    static {
        INSTANCE.registerMessage(packetId++, ClientboundUpdateSkillsPacket.class, ClientboundUpdateSkillsPacket::write,
                ClientboundUpdateSkillsPacket::new, SkillTreePacketHandler::processPacket);
        INSTANCE.registerMessage(packetId++, ClientBoundUpdateLocalizationPacket.class,
                ClientBoundUpdateLocalizationPacket::write, ClientBoundUpdateLocalizationPacket::new,
                SkillTreePacketHandler::processPacket);
    }

    public void sendToAll(Packet packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

    public void sendPacketTo(ServerPlayer player, Packet packet) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void processPacket(Packet packet, Supplier<NetworkEvent.Context> context) {
        if (packet instanceof ClientboundUpdateSkillsPacket)
            enqueueWorkOnClient(context, packet, ClientMain::updateSkills);
        else if (packet instanceof ClientBoundUpdateLocalizationPacket)
            enqueueWorkOnClient(context, packet, ClientMain::updateLocalizations);

        context.get().setPacketHandled(true);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Packet> void enqueueWorkOnClient(Supplier<NetworkEvent.Context> context, Packet packet,
            Consumer<T> consumer) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> consumer.accept((T) packet));
        });
    }

}