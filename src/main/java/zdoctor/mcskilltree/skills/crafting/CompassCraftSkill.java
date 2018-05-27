package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemCompass;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class CompassCraftSkill extends ItemCrafterSkill {

	public CompassCraftSkill() {
		super(ItemCompass.class, "CompassCraftSkill", Items.COMPASS);
		addRequirement(new LevelRequirement(15));
		addRequirement(new SkillPointRequirement(1));
	}

}
