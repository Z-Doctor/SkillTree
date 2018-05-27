package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class BrewingStandCraftSkill extends ItemCrafterSkill {

	public BrewingStandCraftSkill() {
		super(Blocks.BREWING_STAND, "BrewingStandCraftSkill", Items.BREWING_STAND);
		setFrameType(SkillFrameType.SPECIAL);
		addRequirement(new LevelRequirement(30));
		addRequirement(new SkillPointRequirement(5));
	}

}
