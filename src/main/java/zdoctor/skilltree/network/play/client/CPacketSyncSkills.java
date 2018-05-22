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
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;

/**
 * Sends the player skill data from server to client.
 *
 */
public class CPacketSyncSkills implements IMessage {

	private int playerId;
	private NBTTagCompound skillTreeTag;

	public CPacketSyncSkills() {
	}

	public CPacketSyncSkills(EntityLivingBase entity) {
		playerId = entity.getEntityId();
		skillTreeTag = SkillTreeApi.getSkillHandler(entity).serializeNBT();
		ModMain.proxy.log.debug("Creating Packet: {} Args: {}", this, entity);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		playerId = buffer.readInt();
		try {
			skillTreeTag = DataSerializers.COMPOUND_TAG.read(new PacketBuffer(buffer));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(playerId);
		DataSerializers.COMPOUND_TAG.write(new PacketBuffer(buffer), skillTreeTag);
	}

	public static class PacketSyncHandler implements IMessageHandler<CPacketSyncSkills, IMessage> {
		@Override
		public IMessage onMessage(CPacketSyncSkills message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = ModMain.proxy.getWorld();
					if (world == null)
						return;
					Entity player = world.getEntityByID(message.playerId);
					if (player != null && player instanceof EntityPlayer) {
						ModMain.proxy.log.debug("Syncing Skillls - Side: {} Player: {} Data: ID: {}, TAG: {}", ctx.side,
								player.getDisplayName(), message.playerId, message.skillTreeTag);
						SkillTreeApi.getSkillHandler((EntityPlayer) player).deserializeNBT(message.skillTreeTag);
						SkillTreeApi.reloadHandler((EntityPlayer) player);
					}
				}
			});
			return null;
		}
	}

}
