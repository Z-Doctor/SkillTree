package zdoctor.skilltree.skills.cap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import zdoctor.skilltree.api.skills.SkillBase;
import zdoctor.skilltree.api.skills.interfaces.ISkill;
import zdoctor.skilltree.api.skills.interfaces.ISkillSlot;
import zdoctor.skilltree.api.skills.interfaces.ISkillStackable;
import zdoctor.skilltree.api.skills.interfaces.ISkillToggle;

public class SkillSlot implements ISkillSlot {
	private boolean obtained;
	private ISkill skill;
	private NBTTagCompound skillTagCompound;
	private boolean active;
	private int skillTier;

	public SkillSlot(ISkill skill) {
		this(skill, false, false, 0);
	}

	public SkillSlot(ISkill skill, boolean obtained, boolean active, int skillTier) {
		this.skill = skill;
		this.obtained = obtained;
		this.active = active;
		this.skillTier = skillTier;
	}

	public SkillSlot(NBTTagCompound nbt) {
		deserializeNBT(nbt);
	}

	@Override
	public boolean isObtained() {
		return obtained;
	}

	@Override
	public void setObtained() {
		setObtained(true);
	}

	@Override
	public void setObtained(boolean obtained) {
		this.obtained = obtained;
	}

	@Override
	public boolean isActive() {
		return isObtained() && active;
	}

	@Override
	public void setActive() {
		setActive(true);
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public ISkill getSkill() {
		return skill;
	}

	@Override
	public void addSkillTier() {
		addSkillTier(1);
	}

	@Override
	public void addSkillTier(int amount) {
		skillTier += amount;
	}

	@Override
	public void setSkillTier(int skillTier) {
		this.skillTier = skillTier;
	}

	@Override
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

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("id", skill.getRegistryName().toString());
		nbt.setBoolean("obtained", isObtained());
		nbt.setBoolean("active", isActive());
		nbt.setInteger("skillTier", getSkillTier());
		return nbt;
	}

	@Override
	public boolean areSkillSlotsEqual(ISkillSlot skillB) {
		return this.skill == skillB.getSkill();
	}

}
