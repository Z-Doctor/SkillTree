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

public class ArmorHelmetCraftSkill extends ArmorCraftSkill {
	public ArmorHelmetCraftSkill() {
		super(EntityEquipmentSlot.HEAD, "ArmorHelmetCraftSkill", Items.DIAMOND_HELMET);
	}

	@Override
	public List<ISkillRequirment> getRequirments(EntityLivingBase entity, boolean hasSkill) {
		List<ISkillRequirment> list = new ArrayList<>();
		int tier = SkillTreeApi.getSkillTier(entity, this);
		if (tier >= getMaxTier(entity))
			return list;
		list.add(new LevelRequirement(5 * (tier + 1)));
		list.add(new SkillPointRequirement(tier + 1));
		return list;
	}

	@Override
	public ItemStack getIcon(EntityLivingBase entity) {
		int tier = SkillTreeApi.getSkillTier(entity, this);
		switch (tier) {
		case 0:
			return new ItemStack(Items.LEATHER_HELMET);
		case 1:
			return new ItemStack(Items.LEATHER_HELMET);
		case 2:
			return new ItemStack(Items.GOLDEN_HELMET);
		case 3:
			return new ItemStack(Items.IRON_HELMET);
		case 4:
			return new ItemStack(Items.DIAMOND_HELMET);
		default:
			return super.getIcon(entity);
		}
	}

}
