package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class TNTCraftSkill extends ItemCrafterSkill {

	public TNTCraftSkill() {
		super(Blocks.TNT, "TNTCraftSkill", new ItemStack(Blocks.TNT));
		addRequirement(new LevelRequirement(15));
		addRequirement(new SkillPointRequirement(1));
	}

}
