package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemBoat;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class BoatCraftSkill extends ItemCrafterSkill {

	public BoatCraftSkill() {
		super(ItemBoat.class, "BoatCraftSkill", Items.BOAT);
		addRequirement(new LevelRequirement(25));
		addRequirement(new SkillPointRequirement(1));
	}

}
