package zdoctor.skilltree.api.skills;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.SkillSlot;

public interface ISkillHandler extends INBTSerializable<NBTTagCompound> {
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
	 * Sets the player of the skill handler.
	 * 
	 * @param player
	 */
	public void setPlayer(EntityPlayer player);

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
	public void onSkillChange(SkillSlot skillSlot);

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

	public EntityPlayer getPlayer();

	public void tickEvent();

}
