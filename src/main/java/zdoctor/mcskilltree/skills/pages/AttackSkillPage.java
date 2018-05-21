package zdoctor.mcskilltree.skills.pages;

import net.minecraft.init.Items;
import zdoctor.mcskilltree.skills.attack.DoubleDamage;
import zdoctor.mcskilltree.skills.attack.ExtraDamage;
import zdoctor.mcskilltree.skills.attack.NightVision;
import zdoctor.skilltree.skills.Skill;
import zdoctor.skilltree.skills.pages.SkillPageBase;

public class AttackSkillPage extends SkillPageBase {

	public AttackSkillPage() {
		super("AttackPage");
		addSkill(new DoubleDamage(0, 0));
		addSkill(new ExtraDamage(0, 1));
		addSkill(new ExtraDamage(1, 1).setParent(getLastAddedSkill()));
		addSkill(new ExtraDamage(2, 1).setParent(getLastAddedSkill()));
		addSkill(new Skill("temp1", 0, 3, Items.REDSTONE));
		addSkill(new Skill("temp2", 1, 2, Items.IRON_INGOT).setParent(getLastAddedSkill()));
		addSkill(new Skill("temp3", 1, 3, Items.GOLD_INGOT).setParent(getLastAddedSkill().getParent()));
		addSkill(new Skill("temp4", 1, 4, Items.DIAMOND).setParent(getLastAddedSkill().getParent()));
		
		addSkill(new NightVision(1, 0));
	}

	@Override
	public BackgroundType getBackgroundType() {
		return BackgroundType.NETHERRACK;
	}

}
