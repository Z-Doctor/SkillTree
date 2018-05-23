package zdoctor.skilltree.skills;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import zdoctor.skilltree.api.skills.ISkillStackable;
import zdoctor.skilltree.api.skills.ISkillToggle;

public class SkillSlot implements INBTSerializable<NBTTagCompound> {
	private boolean obtained;
	private SkillBase skill;
	private NBTTagCompound skillTagCompound;
	private boolean active;
	private int skillTier;

	public SkillSlot(SkillBase skill) {
		this(skill, false, false, 0);
	}

	public SkillSlot(SkillBase skill, boolean obtained, boolean active, int skillTier) {
		this.skill = skill;
		this.obtained = obtained;
		this.active = active;
		this.skillTier = skillTier;
	}

	public boolean isObtained() {
		return obtained;
	}

	public void setObtained() {
		setObtained(true);
	}

	public void setObtained(boolean obtained) {
		this.obtained = obtained;
	}

	public boolean isActive() {
		return isObtained() && active;
	}

	public void setActive() {
		setActive(true);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public SkillSlot(NBTTagCompound nbt) {
		deserializeNBT(nbt);
	}

	public SkillBase getSkill() {
		return skill;
	}

	public void addkillTier() {
		skillTier++;
	}

	public void setSkillTier(int skillTier) {
		this.skillTier = skillTier;
	}

	public int getSkillTier() {
		return skillTier;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound ret = new NBTTagCompound();
		this.writeToNBT(ret);
		return ret;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		skill = SkillBase.getSkillByKey(new ResourceLocation(nbt.getString("id")));
		this.obtained = nbt.getBoolean("obtained");
		if (obtained && !(skill instanceof ISkillToggle))
			this.active = true;
		else
			this.active = nbt.getBoolean("active");
		if (obtained && !(skill instanceof ISkillStackable))
			this.skillTier = 1;
		else
			this.skillTier = nbt.getInteger("skillTier");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("id", skill.getRegistryName().toString());
		nbt.setBoolean("obtained", isObtained());
		nbt.setBoolean("active", isActive());
		nbt.setInteger("skillTier", getSkillTier());
		return nbt;
	}

	public static boolean areSkillSlotsEqual(SkillSlot skillA, SkillSlot skillB) {
		return skillA.skill == skillB.skill;
	}

}
