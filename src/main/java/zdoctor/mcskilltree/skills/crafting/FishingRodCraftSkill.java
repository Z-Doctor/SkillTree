package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemFishingRod;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class FishingRodCraftSkill extends ItemCrafterSkill {

	public FishingRodCraftSkill() {
		super(ItemFishingRod.class, "FishingRodCraftSkill", Items.FISHING_ROD);
		addRequirement(new LevelRequirement(15));
		addRequirement(new SkillPointRequirement(1));
	}

}
