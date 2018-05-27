package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemArrow;
import zdoctor.mcskilltree.skills.pages.CraftingSkillPage;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class ArrowCraftSkill extends ItemCrafterSkill {

	public ArrowCraftSkill() {
		super(ItemArrow.class, "ArrowCraftSkill", Items.ARROW);
		addRequirement(new LevelRequirement(15));
		addRequirement(new SkillPointRequirement(1));
		setParent(CraftingSkillPage.BOW_CRAFTER);
	}

}
