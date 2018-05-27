package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemEmptyMap;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class MapCraftSkill extends ItemCrafterSkill {

	public MapCraftSkill() {
		super(ItemEmptyMap.class, "MapCraftSkill", Items.MAP);
		addRequirement(new LevelRequirement(15));
		addRequirement(new SkillPointRequirement(1));
	}

}
