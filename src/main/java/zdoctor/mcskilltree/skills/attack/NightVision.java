package zdoctor.mcskilltree.skills.attack;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import zdoctor.skilltree.api.skills.ISkillToggle;
import zdoctor.skilltree.api.skills.ISkillTickable;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.skills.Skill;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.SkillSlot;

public class NightVision extends Skill implements ISkillTickable, ISkillToggle {

	public NightVision(int column, int row) {
		super("SkillNightVision", Items.GOLDEN_CARROT);
		addRequirement(new LevelRequirement(1));
	}

	@Override
	public void onActiveTick(EntityLivingBase entity, SkillBase skill, SkillSlot skillSlot) {
		if (entity.getHealth() < entity.getMaxHealth()) {
			entity.heal(entity.getMaxHealth());
		}
	}

}
