package zdoctor.skilltree.skills;

import java.security.acl.Owner;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zdoctor.skilltree.api.skills.ISkillToggle;
import zdoctor.skilltree.api.skills.ISkillWatcher;
import zdoctor.skilltree.events.SkillEvent;

public class SkillSlot implements INBTSerializable<NBTTagCompound> {
	private boolean obtained;
	private SkillBase skill;
	private NBTTagCompound skillTagCompound;
	private boolean active;

	public SkillSlot(SkillBase skill) {
		this(skill, false, false);
	}

	public SkillSlot(SkillBase skill, boolean obtained, boolean active) {
		this.skill = skill;
		this.obtained = obtained;
		this.active = active;

		// if (skill instanceof ISkillWatcher)
		// MinecraftForge.EVENT_BUS.register(this);
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
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("id", skill.getRegistryName().toString());
		nbt.setBoolean("obtained", isObtained());
		nbt.setBoolean("active", isActive());
		return nbt;
	}

	public static boolean areSkillSlotsEqual(SkillSlot skillA, SkillSlot skillB) {
		return skillA.skill == skillB.skill;
	}
	
}
