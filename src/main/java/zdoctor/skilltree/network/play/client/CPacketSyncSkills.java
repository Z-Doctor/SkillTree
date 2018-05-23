package zdoctor.skilltree.network.play.client;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;

/**
 * Sends the player skill data from server to client.
 *
 */
public class CPacketSyncSkills implements IMessage {

	private int playerId;
	private NBTTagCompound skillTreeTag;
//	private boolean isServerWorld;

	public CPacketSyncSkills() {
	}

	public CPacketSyncSkills(EntityLivingBase entity) {
//		this.isServerWorld = isServerWorld;
		playerId = entity.getEntityId();
		skillTreeTag = SkillTreeApi.getSkillHandler(entity).serializeNBT();
		ModMain.proxy.log.debug("Creating Packet: {} Args: {}", this, entity);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
//		isServerWorld = buffer.readBoolean();
		playerId = buffer.readInt();
		try {
			skillTreeTag = DataSerializers.COMPOUND_TAG.read(new PacketBuffer(buffer));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buffer) {
//		buffer.writeBoolean(isServerWorld);
		buffer.writeInt(playerId);
		DataSerializers.COMPOUND_TAG.write(new PacketBuffer(buffer), skillTreeTag);
	}

	public static class PacketSyncHandler implements IMessageHandler<CPacketSyncSkills, IMessage> {
		@Override
		public IMessage onMessage(CPacketSyncSkills message, MessageContext ctx) {
			if (ctx.side == Side.SERVER) {
				Entity entity = ModMain.proxy.getWorld().getEntityByID(message.playerId);
				if (entity instanceof EntityLivingBase) {
					SkillTreeApi.syncSkills((EntityLivingBase) entity);
				} else {
					ModMain.proxy.log.catching(
							new IllegalArgumentException("Tried to sync non living entity. Entity: " + entity));
				}
				return null;
			}

			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = ModMain.proxy.getWorld();
					if (world == null)
						return;
					Entity entity = world.getEntityByID(message.playerId);
					if (entity != null && entity instanceof EntityLivingBase) {
						ModMain.proxy.log.debug("Syncing Skillls - Side: {} Entity: {} Data: ID: {}, TAG: {}", ctx.side,
								entity.getDisplayName(), message.playerId, message.skillTreeTag);
						SkillTreeApi.getSkillHandler((EntityLivingBase) entity).deserializeNBT(message.skillTreeTag);
						SkillTreeApi.reloadHandler((EntityLivingBase) entity);
					}
				}
			});
			return null;
		}
	}

}
