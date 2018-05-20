package zdoctor.skilltree.api.skills.requirements;

import net.minecraft.entity.player.EntityPlayer;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.skills.ISkillRequirment;

public class LevelRequirement implements ISkillRequirment {

	private int levelRequirment;

	public LevelRequirement() {
		this(1);
	}

	public LevelRequirement(int levelRequirment) {
		this.levelRequirment = Math.max(1, levelRequirment);
	}

	@Override
	public boolean test(EntityPlayer t) {
		return t.experienceLevel >= levelRequirment;
	}

	@Override
	public void onFufillment(EntityPlayer player) {
		player.addExperienceLevel(-levelRequirment);
	}

	@Override
	public Object[] getDescriptionValues() {
		return new Object[] { levelRequirment };
	}

	@Override
	public String getDescription() {
		return "requirement." + ModMain.MODID + ".level.desc";
	}

}
