package com.zdoctorsmods.skilltreemod.network;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.zdoctorsmods.skilltreemod.SkillTree;
import com.zdoctorsmods.skilltreemod.client.ClientMain;
import com.zdoctorsmods.skilltreemod.network.packets.ClientboundUpdateLocalizationPacket;
import com.zdoctorsmods.skilltreemod.network.packets.ClientboundUpdateSkillsPacket;
import com.zdoctorsmods.skilltreemod.network.packets.Packet;
import com.zdoctorsmods.skilltreemod.network.packets.ServerboundClientSkillPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class SkillTreePacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final Optional<NetworkDirection> TO_SERVER = Optional.of(NetworkDirection.PLAY_TO_SERVER);
    private static final Optional<NetworkDirection> TO_CLIENT = Optional.of(NetworkDirection.PLAY_TO_CLIENT);

    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SkillTree.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    static int packetId;

    static {
        INSTANCE.registerMessage(packetId++, ClientboundUpdateSkillsPacket.class, ClientboundUpdateSkillsPacket::write,
                ClientboundUpdateSkillsPacket::new, SkillTreePacketHandler::processPacket);

        INSTANCE.registerMessage(packetId++, ClientboundUpdateLocalizationPacket.class,
                ClientboundUpdateLocalizationPacket::write, ClientboundUpdateLocalizationPacket::new,
                SkillTreePacketHandler::processPacket);

        INSTANCE.registerMessage(packetId++, ServerboundClientSkillPacket.class, ServerboundClientSkillPacket::write,
                ServerboundClientSkillPacket::new, SkillTreePacketHandler::processPacket, TO_SERVER);
    }

    public void sendToAll(Packet packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

    public void sendPacketTo(ServerPlayer player, Packet packet) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public void sendToServer(Packet packet) {
        INSTANCE.sendToServer(packet);
    }

    private static void processPacket(Packet packet, Supplier<NetworkEvent.Context> context) {
        if (packet instanceof ClientboundUpdateSkillsPacket)
            enqueueWorkOnClient(context, packet, ClientMain::updateSkills);
        else if (packet instanceof ClientboundUpdateLocalizationPacket)
            enqueueWorkOnClient(context, packet, ClientMain::updateLocalizations);
        else if (packet instanceof ServerboundClientSkillPacket)
            enqueueWorkOnServer(context, packet, SkillTree::processSkillPacket);

        context.get().setPacketHandled(true);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Packet> void enqueueWorkOnClient(Supplier<NetworkEvent.Context> context, Packet packet,
            Consumer<T> consumer) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> consumer.accept((T) packet));
        });
    }

    @SuppressWarnings("unchecked")
    private static <T extends Packet> void enqueueWorkOnServer(Supplier<NetworkEvent.Context> context, Packet packet,
            BiConsumer<ServerPlayer, T> consumer) {
        packet.setSender(context.get().getSender());
        context.get().enqueueWork(() -> {
            consumer.accept(context.get().getSender(), (T) packet);
        });
    }

}