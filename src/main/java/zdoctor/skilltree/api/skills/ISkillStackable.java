package zdoctor.skilltree.api.skills;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;

public interface ISkillStackable {
	public default boolean hasRequirments(EntityLivingBase entity) {
		for (ISkillRequirment requirement : getRequirments(entity, false)) {
			if (!requirement.test(entity))
				return false;
		}
		return true;
	}

	public List<ISkillRequirment> getRequirments(EntityLivingBase entity, boolean hasSkill);

	public void onSkillRePurchase(EntityLivingBase owner);
	
}
