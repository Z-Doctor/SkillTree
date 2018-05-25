package zdoctor.mcskilltree.skills.attack;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import zdoctor.mcskilltree.skills.AttackSkill;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.SkillAttributeModifier;
import zdoctor.skilltree.api.skills.interfaces.ISkillToggle;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;
import zdoctor.skilltree.skills.SkillBase;

public class DoubleDamage extends AttackSkill implements ISkillToggle {
	public static final ItemStack icon = new ItemStack(Items.DIAMOND_SWORD);

	public final SkillAttributeModifier DOUBLE_DAMAGE;

	public DoubleDamage() {
		super("doubleDamage", icon);
		addRequirement(new LevelRequirement(30));
		addRequirement(new SkillPointRequirement(10));
		setFrameType(SkillFrameType.SPECIAL);
		DOUBLE_DAMAGE = new SkillAttributeModifier("attackSkill.doubleDamge", 1, 2);
	}

	@Override
	public SkillAttributeModifier getModifier(EntityLivingBase entity, SkillBase skill) {
		return DOUBLE_DAMAGE;
	}

}
