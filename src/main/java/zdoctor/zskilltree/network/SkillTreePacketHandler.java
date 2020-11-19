package zdoctor.zskilltree.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import zdoctor.zskilltree.network.play.server.SCriterionTrackerSyncPacket;

import java.util.Optional;

public class SkillTreePacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel INSTANCE;

    private static int id = 0;

    private static void registerPackets() {
        INSTANCE.registerMessage(id++, SCriterionTrackerSyncPacket.class, SCriterionTrackerSyncPacket::writePacketData,
                SCriterionTrackerSyncPacket::readFrom, SCriterionTrackerSyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static SimpleChannel createChannel() {
        if (INSTANCE == null) {
            INSTANCE = NetworkRegistry.newSimpleChannel(
                    new ResourceLocation("zskilltree", "main"),
                    () -> PROTOCOL_VERSION,
                    PROTOCOL_VERSION::equals,
                    PROTOCOL_VERSION::equals
            );
            registerPackets();
        }
        return INSTANCE;
    }

}
