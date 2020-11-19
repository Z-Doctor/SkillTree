package zdoctor.skilltree.api.skills.interfaces;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.SkillSlot;

public interface ISkillHandler extends INBTSerializable<NBTTagCompound> {
	
	public static enum ChangeType {
		SKILL_ACTIVATED,
		SKILL_DEACTIVATED,
		SKILL_BOUGHT,
		SKILL_REBOUGHT,
		SKILL_SOLD,
		SKILL_REMOVED,
		NONE,
		ALL
	}
	
	/**
	 * Checks if the owner or the {@link ISkillHandler} has obtained the skill or
	 * not
	 * 
	 * @param skill
	 */
	public boolean hasSkill(SkillBase skill);

	/**
	 * Checks if owner of the {@link ISkillHandler} has the the requirements to
	 * obtain this skill
	 * 
	 * @param skill
	 */
	public boolean hasRequirements(SkillBase skill);

	/**
	 * 
	 * @return An array that has all the {@link SkillSlot} in the
	 *         {@link ISkillHandler}
	 */
	public ArrayList<SkillSlot> getSkillSlots();

	/**
	 * Sets the owner of the skill handler.
	 * 
	 */
	public void setOwner(EntityLivingBase entity);

	/**
	 * Returns the {@link SkillSlot} associated with the {@link SkillBase} in the
	 * {@link ISkillHandler}
	 * 
	 * @param skill
	 */
	public SkillSlot getSkillSlot(SkillBase skill);

	/**
	 * Should be used to make sure that the change was valid. Should notify its
	 * neighbors and parent about its change
	 * 
	 * @param skillSlot
	 */
	public void onSkillChange(SkillSlot skillSlot, ChangeType type);

	/**
	 * Used to obtain a skill. Does not check if the play can buy skill, but makes
	 * sure that any parent skills are obtained first
	 * 
	 * @param skill
	 * @param obtained
	 *            If the skill has been obtained or not
	 */
	public void setSkillObtained(SkillBase skill, boolean obtained);

	public boolean canBuySkill(SkillBase skill);

	public void buySkill(SkillBase skill);

	public int getSkillPoints();

	public void addPoints(int points);

	public boolean isSkillActive(SkillBase skill);

	public void setSkillActive(SkillBase skill, boolean active);

	public void reloadHandler();

	public EntityLivingBase getOwner();

	public int getSkillTier(SkillBase skill);

	public boolean isDirty();

	public void clean();

	public void addSkillTier(SkillBase skill);

	public void addSkillTier(SkillBase skill, int amount);

	public void markDirty();

	public List<SkillBase> getActiveSkillListeners();
	
	public void onTick(EntityLivingBase entity, World world);

}
