package zdoctor.mcskilltree.skills;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zdoctor.skilltree.api.skills.AttributeSkill;

public abstract class AttackSkill extends AttributeSkill {

	public AttackSkill(String name, Item icon) {
		this(name, new ItemStack(icon));
	}

	public AttackSkill(String name, ItemStack icon) {
		super(name, icon, SharedMonsterAttributes.ATTACK_DAMAGE);
	}

}
