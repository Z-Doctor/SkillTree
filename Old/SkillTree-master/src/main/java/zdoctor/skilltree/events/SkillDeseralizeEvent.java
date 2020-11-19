package zdoctor.skilltree.events;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.SkillSlot;

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
	private SkillBase skill;
	private SkillSlot skillSlot;

	public SkillDeseralizeEvent(NBTTagCompound skillTag, SkillBase skill, SkillSlot skillSlot) {
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

	public SkillBase getSkill() {
		return skill;
	}

	public void setSkill(SkillBase skill) {
		this.skill = skill;
	}

	public SkillSlot getSkillSlot() {
		return skillSlot;
	}

	public void setSkillSlot(SkillSlot skillSlot) {
		this.skillSlot = skillSlot;
	}

}
