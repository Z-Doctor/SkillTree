package zdoctor.skilltree.api.skills.interfaces;

import net.minecraft.entity.EntityLivingBase;

public interface ISkillSellable {
	public void onSold(EntityLivingBase owner);
}
