package zdoctor.skilltree.api.skills.requirements;

import net.minecraft.entity.EntityLivingBase;
import zdoctor.skilltree.api.skills.interfaces.ISkill;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;

/**
 * A default requirement used to add the skill description to the skill tooltip.
 * Can be used to change the color of the skill name. Override the one in the
 * default skillbase class
 *
 */
public class DescriptionRequirment implements ISkillRequirment {

	private ISkill skill;
	private int color;

	public DescriptionRequirment(ISkill skill) {
		this(skill, -1);
	}

	public DescriptionRequirment(ISkill skill, int color) {
		this.skill = skill;
		this.color = color;
	}

	public DescriptionRequirment setColor(int color) {
		this.color = color;
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
		return skill.getUnlocaizedName() + ".desc";
	}

	@Override
	public int getTextColor(EntityLivingBase entity) {
		return color;
	}

}
