package zdoctor.mcskilltree.skills.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import zdoctor.mcskilltree.skills.pages.ArmorCraftSkill;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class ArmorLeggingsCraftSkill extends ArmorCraftSkill {
	public ArmorLeggingsCraftSkill() {
		super(EntityEquipmentSlot.LEGS, "ArmorLeggingsCraftSkill", Items.DIAMOND_LEGGINGS);
	}

	@Override
	public List<ISkillRequirment> getRequirments(EntityLivingBase entity, boolean hasSkill) {
		List<ISkillRequirment> list = new ArrayList<>();
		int tier = SkillTreeApi.getSkillTier(entity, this);
		if (tier >= getMaxTier(entity))
			return list;
		list.add(new LevelRequirement(7 * (tier + 1)));
		list.add(new SkillPointRequirement(tier + 1));
		return list;
	}

	@Override
	public ItemStack getIcon(EntityLivingBase entity) {
		int tier = SkillTreeApi.getSkillTier(entity, this);
		switch (tier) {
		case 0:
			return new ItemStack(Items.LEATHER_LEGGINGS);
		case 1:
			return new ItemStack(Items.LEATHER_LEGGINGS);
		case 2:
			return new ItemStack(Items.GOLDEN_LEGGINGS);
		case 3:
			return new ItemStack(Items.IRON_LEGGINGS);
		case 4:
			return new ItemStack(Items.DIAMOND_LEGGINGS);
		default:
			return super.getIcon(entity);
		}
	}

}
