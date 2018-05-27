package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemTippedArrow;
import zdoctor.mcskilltree.skills.pages.CraftingSkillPage;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class TippedArrowCraftSkill extends ItemCrafterSkill {

	public TippedArrowCraftSkill() {
		super(ItemTippedArrow.class, "TipArrowCraftSkill", Items.TIPPED_ARROW);
		addRequirement(new LevelRequirement(30));
		addRequirement(new SkillPointRequirement(5));
		setParent(CraftingSkillPage.ARROW_CRAFTER);
		setFrameType(SkillFrameType.SPECIAL);
	}

}
