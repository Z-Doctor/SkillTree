package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemSpectralArrow;
import zdoctor.mcskilltree.skills.pages.CraftingSkillPage;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class SpectralArrowCraftSkill extends ItemCrafterSkill {

	public SpectralArrowCraftSkill() {
		super(ItemSpectralArrow.class, "SpectralArrowCraftSkill", Items.SPECTRAL_ARROW);
		addRequirement(new LevelRequirement(5));
		addRequirement(new SkillPointRequirement(1));
		setParent(CraftingSkillPage.ARROW_CRAFTER);
	}

}
