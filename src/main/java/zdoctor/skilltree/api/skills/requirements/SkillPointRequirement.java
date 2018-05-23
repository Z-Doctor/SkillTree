package zdoctor.skilltree.api.skills.requirements;

import net.minecraft.entity.EntityLivingBase;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.ISkillHandler;
import zdoctor.skilltree.api.skills.ISkillRequirment;

public class SkillPointRequirement implements ISkillRequirment {

	private int requiredPoints;

	public SkillPointRequirement(int pointsRequired) {
		requiredPoints = pointsRequired;
	}

	@Override
	public boolean test(EntityLivingBase entity) {
		ISkillHandler skillHandler = SkillTreeApi.getSkillHandler(entity);
		return skillHandler.getSkillPoints() >= requiredPoints;
	}

	@Override
	public void onFufillment(EntityLivingBase entity) {
		ISkillHandler skillHandler = SkillTreeApi.getSkillHandler(entity);
		skillHandler.addPoints(-requiredPoints);

	}

	@Override
	public String getDescription() {
		return "requirement." + ModMain.MODID + ".skillPoint.desc";
	}

	@Override
	public Object[] getDescriptionValues() {
		return new Object[] { requiredPoints };
	}

}
