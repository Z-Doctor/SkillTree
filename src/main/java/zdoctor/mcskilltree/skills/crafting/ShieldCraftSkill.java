package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemShield;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class ShieldCraftSkill extends ItemCrafterSkill {

	public ShieldCraftSkill() {
		super(ItemShield.class, "ShieldCraftSkill", Items.SHIELD);
		setFrameType(SkillFrameType.SPECIAL);
		addRequirement(new LevelRequirement(30));
		addRequirement(new SkillPointRequirement(5));
	}

}
