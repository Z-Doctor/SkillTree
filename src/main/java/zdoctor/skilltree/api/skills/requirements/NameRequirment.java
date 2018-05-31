package zdoctor.skilltree.api.skills.requirements;

import net.minecraft.entity.EntityLivingBase;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.interfaces.ISkill;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;

/**
 * A default requirment used to add the name to the skill tooltip. Can be used
 * to change the color of the skill name. Override the one in the default
 * skillbase class
 *
 */
public class NameRequirment implements ISkillRequirment {

	private ISkill skill;
	private int colorObtained;
	private int colorNotObtained;

	public NameRequirment(ISkill skill) {
		this(skill, 0xFED83D, -1);
	}

	public NameRequirment(ISkill skill, int colorObtained, int colorNotObtained) {
		this.skill = skill;
		this.colorObtained = colorObtained;
		this.colorNotObtained = colorNotObtained;
	}

	public NameRequirment setColor(int colorObtained, int colorNotObtained) {
		this.colorObtained = colorObtained;
		this.colorNotObtained = colorNotObtained;
		return this;
	}

	@Override
	public boolean test(EntityLivingBase t) {
		return true;
	}

	@Override
	public void onFufillment(EntityLivingBase entity) {

	}

	@Override
	public String getDescription() {
		return skill.getUnlocaizedName();
	}

	@Override
	public int getTextColor(EntityLivingBase entity) {
		return SkillTreeApi.hasSkill(entity, skill) ? colorObtained : colorNotObtained;
	}

}
