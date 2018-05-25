package zdoctor.skilltree.network.play.client;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.interfaces.ISkillHandler;

/**
 * A packet to update an entities skills on the client side
 *
 */
public class CPacketSyncSkills implements IMessage {

	private int entityId;
	private NBTTagCompound skillTreeTag;

	public CPacketSyncSkills() {
		// TODO Auto-generated constructor stub
	}

	public CPacketSyncSkills(EntityLivingBase entity) {
		entityId = entity.getEntityId();
		skillTreeTag = SkillTreeApi.getSkillHandler(entity).serializeNBT();
		// ModMain.proxy.log.info("Creating Packet: {} Args: {}", this, entity);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		entityId = buffer.readInt();
		try {
			skillTreeTag = DataSerializers.COMPOUND_TAG.read(new PacketBuffer(buffer));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(entityId);
		DataSerializers.COMPOUND_TAG.write(new PacketBuffer(buffer), skillTreeTag);
	}

	public static class PacketSyncHandler implements IMessageHandler<CPacketSyncSkills, IMessage> {
		@Override
		public IMessage onMessage(CPacketSyncSkills message, MessageContext ctx) {
			// System.out.println("Client update");
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = ModMain.proxy.getWorld();
					if (world == null || !world.isRemote)
						return;
					Entity entity = world.getEntityByID(message.entityId);
					if (entity != null && entity instanceof EntityLivingBase) {
						ISkillHandler cap = SkillTreeApi.getSkillHandler((EntityLivingBase) entity);
						cap.deserializeNBT(message.skillTreeTag);
						cap.markDirty();
					}
				}
			});
			return null;
		}
	}

}
