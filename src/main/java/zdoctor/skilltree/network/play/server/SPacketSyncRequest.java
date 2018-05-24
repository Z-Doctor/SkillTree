package zdoctor.skilltree.network.play.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.network.play.client.CPacketSyncSkills;

/**
 * A packet where the client can request skill updates for entities
 *
 */
public class SPacketSyncRequest implements IMessage {

	private int entityId;
	private boolean emptyPacket;

	public SPacketSyncRequest() {
	}

	public SPacketSyncRequest(EntityLivingBase entity) {
		entityId = entity.getEntityId();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(entityId);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		entityId = buffer.readInt();
	}

	public static class PacketSyncRequestHandler implements IMessageHandler<SPacketSyncRequest, CPacketSyncSkills> {
		@Override
		public CPacketSyncSkills onMessage(SPacketSyncRequest message, MessageContext ctx) {
			// System.out.println("Sync Request: " + ctx.side);
			Entity entity = ModMain.proxy.getWorld().getEntityByID(message.entityId);
			if (entity == null) {
				// ModMain.proxy.log
				// .catching(new IllegalArgumentException("Tried to sync null entity. Entity: "
				// + entity));
				return null;
			}

			if (entity instanceof EntityLivingBase) {
				return new CPacketSyncSkills((EntityLivingBase) entity);
			} else
				ModMain.proxy.log
						.catching(new IllegalArgumentException("Tried to sync non living entity. Entity: " + entity));
			return null;
		}
	}
}