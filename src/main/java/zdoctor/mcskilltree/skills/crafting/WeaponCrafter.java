package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zdoctor.mcskilltree.skills.CraftSkill;
import zdoctor.skilltree.api.skills.Skill;

public class WeaponCrafter extends CraftSkill {

	public WeaponCrafter(String name, Item icon) {
		this(name, new ItemStack(icon));
	}

	public WeaponCrafter(String name, ItemStack icon) {
		super(name, icon);
	}

}
