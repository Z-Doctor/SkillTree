package zdoctor.skilltree.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.network.play.client.CPacketSyncSkills;
import zdoctor.skilltree.network.play.client.CPacketSyncSkills.PacketSyncHandler;
import zdoctor.skilltree.network.play.server.SPacketSkillSlotInteract;
import zdoctor.skilltree.network.play.server.SPacketSkillSlotInteract.PacketInteractHandler;

public class SkillTreePacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModMain.MODID);
	private static int packetId = 0;

	public static void initPackets() {
		INSTANCE.registerMessage(PacketSyncHandler.class, CPacketSyncSkills.class, getNextID(), Side.CLIENT);
		INSTANCE.registerMessage(PacketInteractHandler.class, SPacketSkillSlotInteract.class, getNextID(), Side.SERVER);
	}

	private static int getNextID() {
		return packetId++;
	}

}
