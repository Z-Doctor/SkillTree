package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class BowCraftSkill extends ItemCrafterSkill {

	public BowCraftSkill() {
		super(ItemBow.class, "BowCraftSkill", Items.BOW);
		setFrameType(SkillFrameType.SPECIAL);
		addRequirement(new LevelRequirement(30));
		addRequirement(new SkillPointRequirement(5));
	}

}
