package zdoctor.skilltree.api.skills.interfaces;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISkillSlot extends INBTSerializable<NBTTagCompound> {

	boolean isObtained();

	void setObtained();

	void setObtained(boolean obtained);

	boolean isActive();

	void setActive();

	void setActive(boolean active);

	ISkill getSkill();

	void addSkillTier();

	void addSkillTier(int amount);

	void setSkillTier(int skillTier);

	int getSkillTier();

	NBTTagCompound writeToNBT(NBTTagCompound nbt);

	boolean areSkillSlotsEqual(ISkillSlot skillB);

}
