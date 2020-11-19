package zdoctor.mcskilltree.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.skilltree.packet.SkillTreePacket;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SkillTreePacketRegister {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(McSkillTree.MODID, "packet_bus"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        McSkillTree.LOGGER.debug("Setting up packets");
        INSTANCE.registerMessage(id++, SkillTreePacket.class, SkillTreePacket::encode, SkillTreePacket::decode, SkillTreePacket.SkillTreePacketHandler::handle);
    }
}
