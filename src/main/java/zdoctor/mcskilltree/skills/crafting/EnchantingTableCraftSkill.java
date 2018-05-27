package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class EnchantingTableCraftSkill extends ItemCrafterSkill {

	public EnchantingTableCraftSkill() {
		super(Blocks.ENCHANTING_TABLE, "EnchantingTableCraftSkill", Item.getItemFromBlock(Blocks.ENCHANTING_TABLE));
		setFrameType(SkillFrameType.SPECIAL);
		addRequirement(new LevelRequirement(30));
		addRequirement(new SkillPointRequirement(5));
	}

}
