package zdoctor.skilltree.api.events;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import zdoctor.skilltree.api.skills.interfaces.ISkill;
import zdoctor.skilltree.api.skills.interfaces.ISkillSlot;

/**
 * Called when a player skills are being read from nbt. Can be used to fix
 * skills whose names have been change or override other skills being added. If
 * canceled, result denied, or skill is null, will note deserialize that skill.
 *
 */
@HasResult
@Cancelable
public class SkillDeseralizeEvent extends Event {

	private NBTTagCompound skillTag;
	private ISkill skill;
	private ISkillSlot skillSlot;

	public SkillDeseralizeEvent(NBTTagCompound skillTag, ISkill skill, ISkillSlot skillSlot) {
		this.skillTag = skillTag;
		this.skill = skill;
		this.skillSlot = skillSlot;
	}

	public NBTTagCompound getSkillTag() {
		return skillTag;
	}

	public void setSkillTag(NBTTagCompound skillTag) {
		this.skillTag = skillTag;
	}

	public ISkill getSkill() {
		return skill;
	}

	public void setSkill(ISkill skill) {
		this.skill = skill;
	}

	public ISkillSlot getSkillSlot() {
		return skillSlot;
	}

	public void setSkillSlot(ISkillSlot skillSlot) {
		this.skillSlot = skillSlot;
	}

}
