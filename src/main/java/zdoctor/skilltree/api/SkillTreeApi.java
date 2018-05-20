package zdoctor.skilltree.api;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.skills.ISkillHandler;
import zdoctor.skilltree.api.skills.IToggleSkill;
import zdoctor.skilltree.network.SkillTreePacketHandler;
import zdoctor.skilltree.network.play.client.CPacketSyncSkills;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.SkillHandler;
import zdoctor.skilltree.skills.SkillSlot;

public class SkillTreeApi {
	public static final String DEPENDENCY = "required-after:skilltree@[1.0.0.0,)";

	@CapabilityInject(ISkillHandler.class)
	public static Capability<ISkillHandler> SKILL_CAPABILITY = null;

	public static ISkillHandler getSkillHandler(EntityLivingBase player) {
		ISkillHandler handler = player.getCapability(SKILL_CAPABILITY, null);
		handler.setOwner(player);
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

	/**
	 * Returns a copy of the skill slot. Changes made to it wont reflect
	 * 
	 * @param player
	 * @param skill
	 * @return
	 */
	public static SkillSlot getSkillSlot(EntityLivingBase player, SkillBase skill) {
		return new SkillSlot(skill, hasSkill(player, skill), isSkillActive(player, skill));
	}

	/**
	 * Request and update from the server
	 */
	@SideOnly(Side.CLIENT)
	public static void SyncClientSkills(EntityPlayer player) {
		CPacketSyncSkills pkt = new CPacketSyncSkills(player);
		SkillTreePacketHandler.INSTANCE.sendToServer(pkt);
	}

	/**
	 * Sends update to all receivers to sync data, should be called on server
	 */
	public static void syncServerSkills(EntityPlayer player, List<EntityPlayer> receivers) {
		if (!(player instanceof EntityPlayerMP))
			return;
		CPacketSyncSkills pkt = new CPacketSyncSkills(player);
		for (EntityPlayer receiver : receivers) {
			SkillTreePacketHandler.INSTANCE.sendTo(pkt, (EntityPlayerMP) receiver);
		}
	}

	public static void syncServerSkillsAll(EntityPlayer player) {
		if (!(player instanceof EntityPlayerMP) || !(ModMain.proxy.getWorld() instanceof WorldServer))
			return;
		CPacketSyncSkills pkt = new CPacketSyncSkills(player);
		for (EntityPlayer receiver : ((WorldServer) ModMain.proxy.getWorld()).playerEntities) {
			SkillTreePacketHandler.INSTANCE.sendTo(pkt, (EntityPlayerMP) receiver);
		}
	}

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
		SkillTreeApi.syncServerSkillsAll(entityplayer);
	}

	public static void reloadHandler(EntityPlayer player) {
		getSkillHandler(player).reloadHandler();
	}

	public static void toggleSkill(EntityPlayerMP player, SkillBase skill) {
		if (skill instanceof IToggleSkill) {
			getSkillHandler(player).setSkillActive(skill, !isSkillActive(player, skill));
		}
	}

}
