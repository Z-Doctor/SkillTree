package zdoctor.skilltree.network.play.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.EnumSkillInteractType;
import zdoctor.skilltree.events.SkillInteractEvent;
import zdoctor.skilltree.network.play.client.CPacketSyncSkills;
import zdoctor.skilltree.skills.SkillBase;

public class SPacketSkillSlotInteract implements IMessage {

	private SkillBase skill;
	private EnumSkillInteractType type;

	public SPacketSkillSlotInteract() {
	}

	public SPacketSkillSlotInteract(SkillBase skill, EnumSkillInteractType type) {
		this.skill = skill;
		this.type = type;
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(type.ordinal());
		buffer.writeInt(skill.getId());
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		type = EnumSkillInteractType.values()[buffer.readInt()];
		skill = SkillBase.getSkillById(buffer.readInt());
	}

	public static class PacketInteractHandler implements IMessageHandler<SPacketSkillSlotInteract, CPacketSyncSkills> {
		@Override
		public CPacketSyncSkills onMessage(SPacketSkillSlotInteract message, MessageContext ctx) {
			SkillInteractEvent event = new SkillInteractEvent(ctx.getServerHandler().player, message.skill,
					message.type, ctx.side);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled())
				return null;

			ModMain.proxy.log.debug("Skill Interact - Type: {} Player: {} Skill: {}", message.type,
					ctx.getServerHandler().player, message.skill.getRegistryName());
			switch (message.type) {
			case BUY:
				SkillTreeApi.buySkill(ctx.getServerHandler().player, message.skill);
				break;
			case SELL:
				SkillTreeApi.sellSkill(ctx.getServerHandler().player, message.skill);
				break;
			case REFUND:
				SkillTreeApi.refundSkill(ctx.getServerHandler().player, message.skill);
				break;
			case TOGGLE:
				SkillTreeApi.toggleSkill(ctx.getServerHandler().player, message.skill);
				break;
			default:
				break;
			}

			if (ctx.side == Side.SERVER && event.getResult() != Event.Result.DENY) {
				// Not meant for non players
				return new CPacketSyncSkills(ctx.getServerHandler().player);
			}
			return null;
		}
	}
}