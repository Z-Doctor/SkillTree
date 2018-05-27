package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemBook;
import zdoctor.mcskilltree.skills.pages.CraftingSkillPage;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class BookCraftSkill extends ItemCrafterSkill {

	public BookCraftSkill() {
		super(ItemBook.class, "BookCraftSkill", Items.BOOK);
		setParent(CraftingSkillPage.PAPER_CRAFTER);
		addRequirement(new LevelRequirement(15));
		addRequirement(new SkillPointRequirement(1));
	}

}
