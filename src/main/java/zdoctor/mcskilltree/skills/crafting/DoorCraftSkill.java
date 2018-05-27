package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemDoor;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class DoorCraftSkill extends ItemCrafterSkill {

	public DoorCraftSkill() {
		super(ItemDoor.class, "DoorCraftSkill", Items.OAK_DOOR);
		addRequirement(new LevelRequirement(25));
		addRequirement(new SkillPointRequirement(1));
	}

}
