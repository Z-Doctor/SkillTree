package zdoctor.skilltree.api.skills;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import zdoctor.skilltree.api.SkillTreeApi;

public interface ISkillStackable {
	public default boolean hasRequirments(EntityLivingBase player) {
		for (ISkillRequirment requirement : getRequirments(false)) {
			if (!requirement.test(player))
				return false;
		}
		return true;
	}

	public List<ISkillRequirment> getRequirments(boolean hasSkill);

	public int getSkillTier(EntityLivingBase player);

	public void onSkillRePurchase(EntityLivingBase owner);
}
