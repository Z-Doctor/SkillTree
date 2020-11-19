package zdoctor.skilltree.api;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicates;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.FMLLog;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.enums.EnumSkillInteractType;
import zdoctor.skilltree.api.skills.interfaces.ISkillHandler;
import zdoctor.skilltree.api.skills.interfaces.ISkillToggle;
import zdoctor.skilltree.network.SkillTreePacketHandler;
import zdoctor.skilltree.network.play.client.CPacketSyncSkills;
import zdoctor.skilltree.network.play.server.SPacketSkillSlotInteract;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.SkillHandler;
import zdoctor.skilltree.skills.SkillSlot;

public class SkillTreeApi {
	public static final String DEPENDENCY = "required-after:skilltree@[" + ModMain.VERSION + ",)";

	@CapabilityInject(ISkillHandler.class)
	public static Capability<ISkillHandler> SKILL_CAPABILITY = null;

	public static ISkillHandler getSkillHandler(EntityLivingBase entity) {
		ISkillHandler handler = entity.getCapability(SKILL_CAPABILITY, null);
		if (handler != null && handler.getOwner() == null) {
			handler.setOwner(entity);
		}
		return handler;
	}

	public static boolean hasSkill(EntityLivingBase entity, SkillBase skill) {
		ISkillHandler skillHandler = getSkillHandler(entity);
		return skillHandler.hasSkill(skill);
	}

	public static boolean hasSkillRequirements(EntityLivingBase entity, SkillBase skill) {
		return getSkillSlot(entity, skill).getSkill().hasRequirments(entity);
	}

	public static boolean isSkillActive(EntityLivingBase entity, SkillBase skill) {
		ISkillHandler skillHandler = getSkillHandler(entity);
		return skillHandler.isSkillActive(skill);
	}

	public static boolean canBuySkill(EntityLivingBase entity, SkillBase skill) {
		ISkillHandler skillHandler = getSkillHandler(entity);
		return skillHandler.canBuySkill(skill);
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
	public static SkillSlot getSkillSlot(EntityLivingBase entity, SkillBase skill) {
		return new SkillSlot(skill, hasSkill(entity, skill), isSkillActive(entity, skill), getSkillTier(entity, skill));
	}

	public static int getPlayerSkillPoints(EntityLivingBase entity) {
		return getSkillHandler(entity).getSkillPoints();
	}

	public static void addSkillPoints(EntityLivingBase entity, int points) {
		getSkillHandler(entity).addPoints(points);
		// SkillTreeApi.syncSkills(entity);
	}

	public static void resetSkillHandler(EntityLivingBase entity) {
		ISkillHandler handler = SkillTreeApi.getSkillHandler(entity);
		handler.deserializeNBT(new SkillHandler().serializeNBT());
		handler.reloadHandler();
		if (!entity.world.isRemote)
			handler.markDirty();
	}

	// Skill Interations

	public static void buySkill(EntityLivingBase entity, SkillBase skill) {
		ISkillHandler skillHandler = getSkillHandler(entity);
		if (entity.world.isRemote) {
			SPacketSkillSlotInteract message = new SPacketSkillSlotInteract(skill, EnumSkillInteractType.BUY);
			SkillTreePacketHandler.INSTANCE.sendToServer(message);
			skillHandler.buySkill(skill);
		} else {
			skillHandler.buySkill(skill);
		}
	}

	public static void toggleSkill(EntityLivingBase entity, SkillBase skill) {
		if (skill instanceof ISkillToggle) {
			ISkillHandler skillHandler = getSkillHandler(entity);
			if (entity.world.isRemote) {
				SPacketSkillSlotInteract message = new SPacketSkillSlotInteract(skill, EnumSkillInteractType.TOGGLE);
				SkillTreePacketHandler.INSTANCE.sendToServer(message);
				skillHandler.setSkillActive(skill, !isSkillActive(entity, skill));
			} else {
				skillHandler.setSkillActive(skill, !isSkillActive(entity, skill));
			}
		}

	}

	public static void sellSkill(EntityLivingBase entity, SkillBase skill) {
		// TODO Auto-generated method stub

	}

	public static void refundSkill(EntityLivingBase entity, SkillBase skill) {
		// TODO Auto-generated method stub

	}

	public static void syncSkills(EntityLivingBase entity) {
		if (entity == null) {
			ModMain.proxy.log.catching(new IllegalArgumentException("Tried to sync null entity. Entity: " + entity));
			return;
		}

		if (entity.world == null) {
			ModMain.proxy.log.catching(new IllegalArgumentException("Tried to sync with null world." + entity));
			return;
		}

		if (!(entity instanceof EntityLivingBase)) {
			ModMain.proxy.log
					.catching(new IllegalArgumentException("Tried to sync non-lvining entity. Entity: " + entity));
			return;
		}

		if (getSkillHandler(entity) == null) {
			ModMain.proxy.log.catching(new IllegalArgumentException("Tried to sync unsupported entity: " + entity));
			return;
		}

		if (!entity.world.isRemote) {
			// System.out.println("Syncing entity: " + entity);
			if (entity.world instanceof WorldServer) {
				// System.out.println("Syncing from server");
				List<EntityPlayer> receivers = new ArrayList<>(
						((WorldServer) entity.world).getEntityTracker().getTrackingPlayers(entity));
				if (entity instanceof EntityPlayerMP)
					receivers.addAll(
							((WorldServer) entity.world).getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue()));
				SkillTreeApi.syncSkills(entity, receivers);

			}
		} else {
			// System.out.println("Attempt to sync from client");
		}
	}

	/**
	 * Used to either send an entity updates from the server to players
	 */
	public static void syncSkills(EntityLivingBase entity, List<EntityPlayer> receivers) {
		CPacketSyncSkills packet = new CPacketSyncSkills(entity);
		boolean cleaned = false;
		for (EntityPlayer receiver : receivers) {
			if (receiver instanceof EntityPlayerMP) {
				// System.out.println("Sending update to " + receiver + "Dead: " +
				// receiver.isDead);
				if (receiver.isDead) {
					FMLLog.bigWarning("Receiver {} is Dead!", receiver);
					continue;
				}
				SkillTreePacketHandler.INSTANCE.sendTo(packet, (EntityPlayerMP) receiver);
				cleaned = true;
			} else {
				ModMain.proxy.log.debug("Unable to sync to receiver: {}", receiver);
			}
		}
		if (cleaned) {
			getSkillHandler(entity).clean();
		}
	}

	public static void reloadHandler(EntityPlayer entity) {
		getSkillHandler(entity).reloadHandler();
	}

}
