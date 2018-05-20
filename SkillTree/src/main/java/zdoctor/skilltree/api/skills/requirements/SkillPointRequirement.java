package zdoctor.skilltree.api.skills.requirements;

import net.minecraft.entity.player.EntityPlayer;
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
	public boolean test(EntityPlayer player) {
		ISkillHandler skillHandler = SkillTreeApi.getSkillHandler(player);
		return skillHandler.getSkillPoints() >= requiredPoints;
	}

	@Override
	public void onFufillment(EntityPlayer player) {
		ISkillHandler skillHandler = SkillTreeApi.getSkillHandler(player);
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
