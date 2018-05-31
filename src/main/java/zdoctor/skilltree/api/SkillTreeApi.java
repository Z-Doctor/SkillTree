package zdoctor.skilltree.api;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import zdoctor.skilltree.api.events.SkillHandlerEvent;
import zdoctor.skilltree.api.skills.interfaces.ISkill;
import zdoctor.skilltree.api.skills.interfaces.ISkillHandler;
import zdoctor.skilltree.api.skills.interfaces.ISkillSellable;
import zdoctor.skilltree.api.skills.interfaces.ISkillStackable;
import zdoctor.skilltree.api.skills.interfaces.ISkillToggle;

/**
 * A bunch of helper methods for interacting with an entity's skill handler.
 * Most methods can be found in the skill handler.
 *
 */
public class SkillTreeApi {
	/**
	 * The dependency string of the oldest compatible version
	 */
	public static final String DEPENDENCY = "required-after:skilltree@[1.3.0.0,)";

	/**
	 * The dependency string of the new version
	 */
	public static final String DEPENDENCY_LATEST = "required-after:skilltree@[1.3.0.0,)";

	@CapabilityInject(ISkillHandler.class)
	public static Capability<ISkillHandler> SKILL_CAPABILITY = null;

	/**
	 * Returns the handler for the giving entity. Support is added for all living
	 * entities
	 * 
	 * @param entity
	 *            The entity
	 * @return The skill handler
	 */
	public static ISkillHandler getSkillHandler(EntityLivingBase entity) {
		ISkillHandler handler = entity.getCapability(SKILL_CAPABILITY, null);
		if (handler != null && handler.getOwner() == null) {
			handler.setOwner(entity);
		}
		return handler;
	}

	/**
	 * A method to check if an entities has a skill
	 * 
	 * @param entity
	 *            The enttiy
	 * @param skill
	 *            The skill
	 * @return Whether the entity has the skill
	 */
	public static boolean hasSkill(EntityLivingBase entity, ISkill skill) {
		ISkillHandler skillHandler = getSkillHandler(entity);
		return skillHandler.hasSkill(skill);
	}

	/**
	 * A method to check if a player has a the requirements to get a skill.
	 * 
	 * @param entity
	 *            The entity
	 * @param skill
	 *            The skill
	 * @return Whether the entity has the skill requirements
	 */
	public static boolean hasSkillRequirements(EntityLivingBase entity, ISkill skill) {
		return getSkillHandler(entity).hasRequirements(skill);
	}

	/**
	 * A method to test whether a given skill is active on the entity
	 * 
	 * @param entity
	 *            The entity
	 * @param skill
	 *            The skill
	 * @return Whether the skill is active (must also be obtained)
	 */
	public static boolean isSkillActive(EntityLivingBase entity, ISkill skill) {
		ISkillHandler skillHandler = getSkillHandler(entity);
		return skillHandler.isSkillActive(skill);
	}

	/**
	 * A method to check if a entity can buy a skill
	 * 
	 * @param entity
	 *            The entity
	 * @param skill
	 *            The skill
	 * @return Whether the entity can buy the skill
	 */
	public static boolean canBuySkill(EntityLivingBase entity, ISkill skill) {
		ISkillHandler skillHandler = getSkillHandler(entity);
		return skillHandler.canBuySkill(skill);
	}

	/**
	 * A method to get the current tier of a skill
	 * 
	 * @param entity
	 *            The entity
	 * @param skill
	 *            The skill
	 * @return 0 if not obtained, 1 if obtained and not a case of
	 *         {@link ISkillStackable} or whatever the skill tier is
	 */
	public static int getSkillTier(EntityLivingBase entity, ISkill skill) {
		return getSkillHandler(entity).getSkillTier(skill);
	}

	/**
	 * A method to get the current amount of skill points of an entity
	 * 
	 * @param entity
	 *            The entity
	 * @return The current amount of skill points
	 */
	public static int getSkillPoints(EntityLivingBase entity) {
		return getSkillHandler(entity).getSkillPoints();
	}

	/**
	 * A method to add a number of points to the given entity
	 * 
	 * @param entity
	 *            The entity
	 * @param points
	 *            The amount of points to be given (or taken if negative)
	 */
	public static void addSkillPoints(EntityLivingBase entity, int points) {
		getSkillHandler(entity).addPoints(points);
	}

	// Skill Handler Interactions

	/**
	 * A method that resets the skill tree to the initial state that will fire
	 * {@link SkillHandlerEvent.FirstLoadEvent}
	 * 
	 * @param entity
	 *            The entity
	 */
	public static void resetSkillHandler(EntityLivingBase entity) {
		// TODO Add a prestige method
		getSkillHandler(entity).reset();
	}

	/**
	 * A method to buy a skill. Will not be bought unless the player has the
	 * requirements. To give a player a skill use giveSkill
	 * 
	 * @param entity
	 *            The entity
	 * @param skill
	 *            The skill to buy
	 */
	public static void buySkill(EntityLivingBase entity, ISkill skill) {
		getSkillHandler(entity).buySkill(skill);
	}

	/**
	 * A method to toggle a skill
	 * 
	 * @param entity
	 *            The entity
	 * @param skill
	 *            The skill
	 */
	public static void toggleSkill(EntityLivingBase entity, ISkill skill) {
		if (skill instanceof ISkillToggle)
			getSkillHandler(entity).setSkillActive(skill, !isSkillActive(entity, skill));
	}

	public static void sellSkill(EntityLivingBase entity, ISkill skill) {
		if (skill instanceof ISkillSellable && hasSkill(entity, skill)) {
			getSkillHandler(entity).sellSkill(skill);
		}

	}

	/**
	 * A method to manually sync the skills. Changes are automatically sync. Only
	 * use if you manipulated the skill handler's skill slots directly. Only sends
	 * an update to the client. Does nothing from the client beside reload the
	 * skills. Sends update to every tracking player. If the entity is a player, the
	 * update is sent to him/her also.
	 * 
	 * @param entity
	 *            The entity
	 */
	public static void syncSkills(EntityLivingBase entity) {
		getSkillHandler(entity).sync();
	}

	/**
	 * A method to manually sync the skills. Changes are automatically sync. Only
	 * use if you manipulated the skill handler's skill slots directly. Only sends
	 * an update to the client. Does nothing from the client beside reload the
	 * skills. Sends update to every player in the list.
	 * 
	 * @param entity
	 * @param receivers
	 */
	public static void syncSkills(EntityLivingBase entity, List<EntityPlayer> receivers) {
		getSkillHandler(entity).sync(receivers);
	}

	/**
	 * A method to reload the skills (i.e. re-apply the skills). Skills are reloaded
	 * automatically and this should only be called when you manipulate the skill
	 * handler directly.
	 * 
	 * @param entity
	 */
	public static void reloadHandler(EntityPlayer entity) {
		getSkillHandler(entity).reloadHandler();
	}

}
