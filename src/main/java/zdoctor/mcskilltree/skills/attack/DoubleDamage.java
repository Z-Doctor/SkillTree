package zdoctor.mcskilltree.skills.attack;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import zdoctor.mcskilltree.skills.AttackSkill;
import zdoctor.skilltree.api.skills.ISkillToggle;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class DoubleDamage extends AttackSkill implements ISkillToggle {
	public static final AttributeModifier DOUBLE_DAMAGE = new AttributeModifier("attackSkill.doubleDamge", 1, 2);
	public static final ItemStack icon = new ItemStack(Items.DIAMOND_SWORD);

	public DoubleDamage(int column, int row) {
		super("doubleDamage", column, row, icon, DOUBLE_DAMAGE);
		addRequirement(new LevelRequirement(30));
		addRequirement(new SkillPointRequirement(10));
	}

}
