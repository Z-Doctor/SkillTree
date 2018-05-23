package zdoctor.mcskilltree.skills;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zdoctor.skilltree.skills.AttributeSkill;

public class AttackSkill extends AttributeSkill {

	public AttackSkill(String name, Item icon, AttributeModifier modifierIn) {
		this(name, new ItemStack(icon), modifierIn);
	}

	public AttackSkill(String name, ItemStack icon, AttributeModifier modifierIn) {
		super(name, icon, SharedMonsterAttributes.ATTACK_DAMAGE, modifierIn);
	}

}
