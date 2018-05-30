package zdoctor.skilltree.api.skills.interfaces;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

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
	public boolean hasSkill(ISkill skill);

	/**
	 * Checks if owner of the {@link ISkillHandler} has the the requirements to
	 * obtain this skill
	 * 
	 * @param skill
	 */
	public boolean hasRequirements(ISkill skill);

	/**
	 * 
	 * @return An array that has all the {@link SkillSlot} in the
	 *         {@link ISkillHandler}
	 */
	public ArrayList<ISkillSlot> getSkillSlots();

	/**
	 * Sets the owner of the skill handler.
	 * 
	 */
	public void setOwner(EntityLivingBase entity);

	/**
	 * Returns the {@link SkillSlot} associated with the {@link ISkill} in the
	 * {@link ISkillHandler}
	 * 
	 * @param skill
	 */
	public ISkillSlot getSkillSlot(ISkill skill);

	/**
	 * Should be used to make sure that the change was valid. Should notify its
	 * neighbors and parent about its change
	 * 
	 * @param skillSlot
	 */
	public void onSkillChange(ISkillSlot skillSlot, ChangeType type);

	/**
	 * Used to obtain a skill. Does not check if the play can buy skill, but makes
	 * sure that any parent skills are obtained first
	 * 
	 * @param skill
	 * @param obtained
	 *            If the skill has been obtained or not
	 */
	public void setSkillObtained(ISkill skill, boolean obtained);

	public boolean canBuySkill(ISkill skill);

	public void buySkill(ISkill skill);

	public int getSkillPoints();

	public void addPoints(int points);

	public boolean isSkillActive(ISkill skill);

	public void setSkillActive(ISkill skill, boolean active);

	public void reloadHandler();

	public EntityLivingBase getOwner();

	public int getSkillTier(ISkill skill);

	public boolean isDirty();

	public void clean();

	public void addSkillTier(ISkill skill);

	public void addSkillTier(ISkill skill, int amount);

	public void markDirty();

	public List<ISkill> getActiveSkillListeners();

	public void onTick(EntityLivingBase entity, World world);

	public void sync();

	public void sync(List<EntityPlayer> receivers);

	public void reset();

	public void sellSkill(ISkill skill);

}
