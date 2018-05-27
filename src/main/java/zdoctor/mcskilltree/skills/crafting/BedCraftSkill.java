package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemBed;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class BedCraftSkill extends ItemCrafterSkill {

	public BedCraftSkill() {
		super(ItemBed.class, "BedCraftSkill", Items.BED);
		addRequirement(new LevelRequirement(25));
		addRequirement(new SkillPointRequirement(1));
	}

}
