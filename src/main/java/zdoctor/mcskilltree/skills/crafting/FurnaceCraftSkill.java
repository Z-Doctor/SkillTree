package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class FurnaceCraftSkill extends ItemCrafterSkill {

	public FurnaceCraftSkill() {
		super(Blocks.FURNACE, "FurnaceCraftSkill", Item.getItemFromBlock(Blocks.FURNACE));
		addRequirement(new LevelRequirement(15));
		addRequirement(new SkillPointRequirement(1));
	}

}
