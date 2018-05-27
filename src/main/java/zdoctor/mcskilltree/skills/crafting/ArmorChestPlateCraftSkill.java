package zdoctor.mcskilltree.skills.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import zdoctor.mcskilltree.event.CraftingEvent;
import zdoctor.mcskilltree.skills.pages.ArmorCraftSkill;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;
import zdoctor.skilltree.api.skills.interfaces.ISkillStackable;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class ArmorChestPlateCraftSkill extends ArmorCraftSkill {
	public ArmorChestPlateCraftSkill() {
		super(EntityEquipmentSlot.CHEST, "ArmorChestPlateCraftSkill", Items.DIAMOND_CHESTPLATE);
	}

	@Override
	public List<ISkillRequirment> getRequirments(EntityLivingBase entity, boolean hasSkill) {
		List<ISkillRequirment> list = new ArrayList<>();
		int tier = SkillTreeApi.getSkillTier(entity, this);
		if (tier >= getMaxTier(entity))
			return list;
		list.add(new LevelRequirement(10 * (tier + 1)));
		list.add(new SkillPointRequirement(tier + 1));
		return list;
	}

	@Override
	public ItemStack getIcon(EntityLivingBase entity) {
		int tier = SkillTreeApi.getSkillTier(entity, this);
		switch (tier) {
		case 0:
			return new ItemStack(Items.LEATHER_CHESTPLATE);
		case 1:
			return new ItemStack(Items.LEATHER_CHESTPLATE);
		case 2:
			return new ItemStack(Items.GOLDEN_CHESTPLATE);
		case 3:
			return new ItemStack(Items.IRON_CHESTPLATE);
		case 4:
			return new ItemStack(Items.DIAMOND_CHESTPLATE);
		default:
			return super.getIcon(entity);
		}
	}

}
