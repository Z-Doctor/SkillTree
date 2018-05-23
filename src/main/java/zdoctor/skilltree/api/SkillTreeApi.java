package zdoctor.skilltree.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.skills.ISkillHandler;
import zdoctor.skilltree.api.skills.ISkillToggle;
import zdoctor.skilltree.network.SkillTreePacketHandler;
import zdoctor.skilltree.network.play.client.CPacketSyncSkills;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.SkillHandler;
import zdoctor.skilltree.skills.SkillSlot;

public class SkillTreeApi {
	public static final String DEPENDENCY = "required-after:skilltree@[1.1.0.2,)";

	@CapabilityInject(ISkillHandler.class)
	public static Capability<ISkillHandler> SKILL_CAPABILITY = null;

	public static ISkillHandler getSkillHandler(EntityLivingBase player) {
		ISkillHandler handler = player.getCapability(SKILL_CAPABILITY, null);
		return handler;
	}

	public static boolean hasSkill(EntityLivingBase player, SkillBase skill) {
		ISkillHandler skillHandler = getSkillHandler(player);
		return skillHandler.hasSkill(skill);
	}

	public static boolean hasSkillRequirements(EntityLivingBase player, SkillBase skill) {
		return getSkillSlot(player, skill).getSkill().hasRequirments(player);
	}

	public static boolean isSkillActive(EntityLivingBase player, SkillBase skill) {
		ISkillHandler skillHandler = getSkillHandler(player);
		return skillHandler.isSkillActive(skill);
	}

	public static boolean canBuySkill(EntityLivingBase player, SkillBase skill) {
		ISkillHandler skillHandler = getSkillHandler(player);
		return skillHandler.canBuySkill(skill);
	}

	public static void buySkill(EntityLivingBase player, SkillBase skill) {
		ISkillHandler skillHandler = getSkillHandler(player);
		skillHandler.buySkill(skill);
	}

	public static int getSkillTier(EntityLivingBase entity, SkillBase skill) {
		return getSkillHandler(entity).getSkillTier(skill);
	}

	/**
	 * Returns a copy of the skill slot. Changes made to it wont reflect
	 * 
	 * @param player
	 * @param skill
	 * @return
	 */
	public static SkillSlot getSkillSlot(EntityLivingBase player, SkillBase skill) {
		return new SkillSlot(skill, hasSkill(player, skill), isSkillActive(player, skill), getSkillTier(player, skill));
	}

	public static void syncSkills(EntityLivingBase entity) {
		if (entity == null) {
			ModMain.proxy.log
					.catching(new IllegalArgumentException("Tried to sync non living entity. Entity: " + entity));
			return;
		}
		CPacketSyncSkills packet = new CPacketSyncSkills(entity);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			SkillTreePacketHandler.INSTANCE.sendToAll(packet);
		} else {
			// Client Side Request
			if (entity instanceof EntityPlayer) {
				// Only players should ask for a request client side
				SkillTreePacketHandler.INSTANCE.sendToServer(packet);
			}
		}
	}

	// public static void syncSkills(EntityLivingBase entity, List<EntityPlayer>
	// receivers) {
	// if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
	// return;
	// if (entity == null) {
	// ModMain.proxy.log.catching(new IllegalArgumentException("Tried to sync null
	// entity"));
	// return;
	// }
	// CPacketSyncSkills pkt = new CPacketSyncSkills(entity);
	// for (EntityPlayer receiver : receivers) {
	// SkillTreePacketHandler.INSTANCE.sendTo(pkt, (EntityPlayerMP) receiver);
	// }
	//
	// }

	public static void sellSkill(EntityPlayer player, SkillBase skill) {
		// TODO Auto-generated method stub

	}

	public static void refundSkill(EntityPlayer player, SkillBase skill) {
		// TODO Auto-generated method stub

	}

	public static int getPlayerSkillPoints(EntityPlayer player) {
		return getSkillHandler(player).getSkillPoints();
	}

	public static void addSkillPoints(EntityPlayer player, int points) {
		getSkillHandler(player).addPoints(points);
	}

	public static void resetSkillHandler(EntityPlayer entityplayer) {
		SkillHandler skillhandler = new SkillHandler();
		skillhandler.setOwner(entityplayer);
		SkillTreeApi.getSkillHandler(entityplayer).deserializeNBT(skillhandler.serializeNBT());
		SkillTreeApi.syncSkills(entityplayer);
	}

	public static void reloadHandler(EntityLivingBase player) {
		getSkillHandler(player).reloadHandler();
	}

	public static void toggleSkill(EntityPlayerMP player, SkillBase skill) {
		if (skill instanceof ISkillToggle) {
			getSkillHandler(player).setSkillActive(skill, !isSkillActive(player, skill));
		}
	}

}
