package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class ChestCraftSkill extends ItemCrafterSkill {

	public ChestCraftSkill() {
		super(Blocks.CHEST, "ChestCraftSkill", Item.getItemFromBlock(Blocks.CHEST));
		addRequirement(new LevelRequirement(10));
		addRequirement(new SkillPointRequirement(1));
	}

}
