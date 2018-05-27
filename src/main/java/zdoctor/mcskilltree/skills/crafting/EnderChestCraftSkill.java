package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import zdoctor.mcskilltree.skills.pages.CraftingSkillPage;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class EnderChestCraftSkill extends ItemCrafterSkill {

	public EnderChestCraftSkill() {
		super(Blocks.ENDER_CHEST, "EnderChestCraftSkill", Item.getItemFromBlock(Blocks.ENDER_CHEST));
		setParent(CraftingSkillPage.CHEST_CRAFTER);
		addRequirement(new LevelRequirement(30));
		addRequirement(new SkillPointRequirement(5));
		setFrameType(SkillFrameType.SPECIAL);
	}

}
