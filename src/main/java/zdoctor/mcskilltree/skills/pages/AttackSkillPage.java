package zdoctor.mcskilltree.skills.pages;

import net.minecraft.init.Items;
import zdoctor.mcskilltree.skills.attack.DoubleDamage;
import zdoctor.mcskilltree.skills.attack.ExtraDamage;
import zdoctor.mcskilltree.skills.attack.SwordProficiency;
import zdoctor.skilltree.api.skills.Skill;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.pages.SkillPageBase;

public class AttackSkillPage extends SkillPageBase {

	// private DoubleDamage doubleDamage;
	private ExtraDamage extraDamage;
	private ExtraDamage extraDamage1;
	private ExtraDamage extraDamage2;

	private SwordProficiency swordProficiency;

	public AttackSkillPage() {
		super("AttackPage");
	}

	@Override
	public void registerSkills() {
		// doubleDamage = new DoubleDamage();
		extraDamage = new ExtraDamage();
		extraDamage1 = (ExtraDamage) new ExtraDamage().setParent(extraDamage);
		extraDamage2 = (ExtraDamage) new ExtraDamage().setParent(extraDamage1);

		swordProficiency = new SwordProficiency();
	}

	@Override
	public void loadPage() {
		// addSkill(doubleDamage, 0, 0);
		addSkill(extraDamage, 0, 1);
		addSkill(extraDamage1, 1, 1);
		addSkill(extraDamage2, 2, 1);

		addSkill(swordProficiency, 0, 5);
	}

	@Override
	public BackgroundType getBackgroundType() {
		return BackgroundType.NETHERRACK;
	}

}
