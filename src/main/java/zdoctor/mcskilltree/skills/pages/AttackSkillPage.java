package zdoctor.mcskilltree.skills.pages;

import net.minecraft.init.Items;
import zdoctor.mcskilltree.skills.attack.DoubleDamage;
import zdoctor.mcskilltree.skills.attack.ExtraDamage;
import zdoctor.mcskilltree.skills.attack.SwordProficiency;
import zdoctor.skilltree.api.skills.Skill;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.pages.SkillPageBase;

public class AttackSkillPage extends SkillPageBase {

	private DoubleDamage doubleDamage;
	private ExtraDamage extraDamage;
	private ExtraDamage extraDamage1;
	private ExtraDamage extraDamage2;

	private Skill temp;
	private SkillBase temp1;
	private SkillBase temp2;
	private SkillBase temp3;

	private SkillBase temp4;
	private SkillBase temp5;
	private SwordProficiency swordProficiency;

	public AttackSkillPage() {
		super("AttackPage");
	}

	@Override
	public void registerSkills() {
		doubleDamage = new DoubleDamage();
		extraDamage = new ExtraDamage();
		extraDamage1 = (ExtraDamage) new ExtraDamage().setParent(extraDamage);
		extraDamage2 = (ExtraDamage) new ExtraDamage().setParent(extraDamage1);

		temp = new Skill("temp", Items.REDSTONE);
		temp1 = new Skill("temp1", Items.IRON_INGOT).setParent(temp);
		temp2 = new Skill("temp2", Items.GOLD_INGOT).setParent(temp);
		temp3 = new Skill("temp3", Items.DIAMOND).setParent(temp);

		temp4 = new Skill("temp4", Items.DIAMOND);
		temp5 = new Skill("temp5", Items.DIAMOND);

		swordProficiency = new SwordProficiency();
	}

	@Override
	public void loadPage() {
		addSkill(doubleDamage, 0, 0);
		addSkill(extraDamage, 0, 1);
		addSkill(extraDamage1, 1, 1);
		addSkill(extraDamage2, 2, 1);

		addSkill(temp, 0, 3);
		addSkill(temp1, 1, 2);
		addSkill(temp2, 1, 3);
		addSkill(temp3, 1, 4);
		temp4.setParent(temp);
		addSkill(temp4, 10, 15);
		addSkill(temp5, 5, 4);

		addSkill(swordProficiency, 0, 5);
	}

	@Override
	public BackgroundType getBackgroundType() {
		return BackgroundType.NETHERRACK;
	}

}
