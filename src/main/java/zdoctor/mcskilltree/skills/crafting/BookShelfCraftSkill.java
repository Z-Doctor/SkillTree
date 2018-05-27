package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import zdoctor.mcskilltree.skills.pages.CraftingSkillPage;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class BookShelfCraftSkill extends ItemCrafterSkill {

	public BookShelfCraftSkill() {
		super(Blocks.BOOKSHELF, "BookShelfCraftSkill", new ItemStack(Blocks.BOOKSHELF));
		setFrameType(SkillFrameType.ROUNDED);
		setParent(CraftingSkillPage.BOOK_CRAFTER);
		addRequirement(new LevelRequirement(25));
		addRequirement(new SkillPointRequirement(5));
	}

}
