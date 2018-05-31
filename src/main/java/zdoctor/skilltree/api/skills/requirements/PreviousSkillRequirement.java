package zdoctor.skilltree.api.skills.requirements;

import net.minecraft.entity.EntityLivingBase;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.interfaces.ISkill;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;

public class PreviousSkillRequirement implements ISkillRequirment {

	private ISkill skill;

	public PreviousSkillRequirement(ISkill parent) {
		this.skill = parent;
	}

	@Override
	public boolean test(EntityLivingBase entity) {
		return SkillTreeApi.hasSkill(entity, skill);
	}

	@Override
	public void onFufillment(EntityLivingBase entity) {
	}

	@Override
	public String getDescription() {
		return "requirement.skilltree.requireSkill.desc";
	}

	@Override
	public Object[] getDescriptionValues() {
		return new Object[] { skill.getDisplayName() };
	}

}
