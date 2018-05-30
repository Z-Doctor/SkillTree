package zdoctor.skilltree.api.skills.interfaces;

import net.minecraft.entity.EntityLivingBase;

/**
 * If a skill interfaces this will recieve this event every 20 ticks (1 second)
 *
 */
public interface ISkillTickable {
	public void onActiveTick(EntityLivingBase entity, ISkill skill, ISkillSlot skillSlot);

}
