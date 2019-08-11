package zdoctor.skilltree.api.skills.interfaces;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import zdoctor.skilltree.skills.SkillBase;

public interface ISkillStackable {
	public boolean hasRequirments(EntityLivingBase entity);

	public List<ISkillRequirment> getRequirments(EntityLivingBase entity, boolean hasSkill);

	public void onSkillRePurchase(EntityLivingBase entity);
	
	public default int getMaxTier(EntityLivingBase entity) {
		return Integer.MAX_VALUE;
	}

}
