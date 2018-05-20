package zdoctor.skilltree.api.skills.requirements;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.ISkillRequirment;
import zdoctor.skilltree.skills.SkillBase;

/**
 * A default requirment used to add the name to the skill tooltip. Can be used
 * to change the color of the skill name. Override the one in the default
 * skillbase class
 *
 */
public class NameRequirment implements ISkillRequirment {

	private SkillBase skill;
	private int colorObtained;
	private int colorNotObtained;

	public NameRequirment(SkillBase skill) {
		this(skill, 0xFED83D, -1);
	}

	public NameRequirment(SkillBase skill, int colorObtained, int colorNotObtained) {
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
	public void onFufillment(EntityLivingBase player) {

	}

	@Override
	public String getDescription() {
		return skill.getUnlocaizedName();
	}

	@Override
	public int getTextColor(EntityLivingBase player) {
		return SkillTreeApi.hasSkill(player, skill) ? colorObtained : colorNotObtained;
	}

}
