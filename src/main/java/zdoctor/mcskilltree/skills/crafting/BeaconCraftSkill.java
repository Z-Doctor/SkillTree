package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class BeaconCraftSkill extends ItemCrafterSkill {

	public BeaconCraftSkill() {
		super(Blocks.BEACON, "BeaconCraftSkill", new ItemStack(Blocks.BEACON));
		addRequirement(new LevelRequirement(30));
		addRequirement(new SkillPointRequirement(5));
	}

}
