package zdoctor.skilltree.api.skills.interfaces;

import net.minecraft.entity.EntityLivingBase;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.SkillSlot;

/**
 * If a skill interfaces this will recieve this event every 20 ticks (1 second)
 *
 */
public interface ISkillTickable {
	public void onActiveTick(EntityLivingBase entity, SkillBase skill, SkillSlot skillSlot);

}
