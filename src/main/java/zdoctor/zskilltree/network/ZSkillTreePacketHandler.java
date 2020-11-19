package zdoctor.zskilltree.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import zdoctor.zskilltree.network.play.server.SSkillPageInfoPacket;

import java.util.Optional;

public class ZSkillTreePacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("zskilltree", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void registerPackets() {
        INSTANCE.registerMessage(id++, SSkillPageInfoPacket.class, SSkillPageInfoPacket::writePacketData,
                SSkillPageInfoPacket::readFrom, SSkillPageInfoPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

}
