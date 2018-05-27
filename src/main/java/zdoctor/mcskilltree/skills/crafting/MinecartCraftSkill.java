package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemMinecart;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class MinecartCraftSkill extends ItemCrafterSkill {

	public MinecartCraftSkill() {
		super(ItemMinecart.class, "MinecartCraftSkill", Items.MINECART);
		addRequirement(new LevelRequirement(20));
		addRequirement(new SkillPointRequirement(1));
	}

}
