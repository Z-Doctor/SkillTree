package zdoctor.skilltree.api.skills.requirements;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;

public class LevelRequirement implements ISkillRequirment {

	private int levelRequirment;

	public LevelRequirement() {
		this(1);
	}

	public LevelRequirement(int levelRequirment) {
		this.levelRequirment = Math.max(1, levelRequirment);
	}

	@Override
	public boolean test(EntityLivingBase t) {
		if (t instanceof EntityPlayer)
			return ((EntityPlayer) t).experienceLevel >= levelRequirment;
		return false;
	}

	@Override
	public void onFufillment(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer)
			((EntityPlayer) entity).addExperienceLevel(-levelRequirment);
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
