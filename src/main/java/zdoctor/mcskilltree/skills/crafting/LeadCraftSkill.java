package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemLead;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class LeadCraftSkill extends ItemCrafterSkill {

	public LeadCraftSkill() {
		super(ItemLead.class, "LeadCraftSkill", Items.LEAD);
		addRequirement(new LevelRequirement(15));
		addRequirement(new SkillPointRequirement(1));
	}

}
