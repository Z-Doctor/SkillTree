package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemWritableBook;
import zdoctor.mcskilltree.skills.pages.CraftingSkillPage;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class WritingBookCraftSkill extends ItemCrafterSkill {

	public WritingBookCraftSkill() {
		super(ItemWritableBook.class, "WritingBookCraftSkill", Items.WRITABLE_BOOK);
		setParent(CraftingSkillPage.BOOK_CRAFTER);
		addRequirement(new LevelRequirement(15));
		addRequirement(new SkillPointRequirement(1));
	}

}
