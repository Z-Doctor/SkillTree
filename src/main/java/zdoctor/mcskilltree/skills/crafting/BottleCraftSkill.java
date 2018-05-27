package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemGlassBottle;
import zdoctor.mcskilltree.skills.pages.CraftingSkillPage;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class BottleCraftSkill extends ItemCrafterSkill {

	public BottleCraftSkill() {
		super(ItemGlassBottle.class, "BottleCraftSkill", Items.GLASS_BOTTLE);
		setParent(CraftingSkillPage.BREWINGSTAND_CRAFTER);
		addRequirement(new LevelRequirement(5));
		addRequirement(new SkillPointRequirement(1));
	}

}
