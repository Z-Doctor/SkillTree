package zdoctor.mcskilltree.skills.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import zdoctor.mcskilltree.event.CraftingEvent;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;
import zdoctor.skilltree.api.skills.interfaces.ISkillStackable;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class AxeCraftSkill extends ItemCrafterSkill implements ISkillStackable {
	public AxeCraftSkill() {
		super(ItemAxe.class, "AxeCrafter", Items.DIAMOND_AXE);
		setFrameType(SkillFrameType.ROUNDED);
	}

	@Override
	public void onSkillRePurchase(EntityLivingBase entity) {
		// TODO Toast
	}

	@Override
	public List<ISkillRequirment> getRequirments(EntityLivingBase entity, boolean hasSkill) {
		List<ISkillRequirment> list = new ArrayList<>();
		if (hasSkill) {
			int tier = SkillTreeApi.getSkillTier(entity, this);
			if (tier >= getMaxTier(entity))
				return list;
			list.add(new LevelRequirement(8 * tier));
			list.add(new SkillPointRequirement(tier));
		} else {
			list = super.getRequirments(entity, hasSkill);
			list.add(new LevelRequirement(10));
		}
		return list;
	}

	@Override
	public ItemStack getIcon(EntityLivingBase entity) {
		int tier = SkillTreeApi.getSkillTier(entity, this);
		switch (tier) {
		case 0:
			return new ItemStack(Items.WOODEN_AXE);
		case 1:
			return new ItemStack(Items.WOODEN_AXE);
		case 2:
			return new ItemStack(Items.STONE_AXE);
		case 3:
			return new ItemStack(Items.GOLDEN_AXE);
		case 4:
			return new ItemStack(Items.IRON_AXE);
		case 5:
			return new ItemStack(Items.DIAMOND_AXE);
		default:
			return super.getIcon(entity);
		}
	}

	@Override
	public int getMaxTier(EntityLivingBase entity) {
		return ToolMaterial.values().length;
	}

	@Override
	public void craftEvent(CraftingEvent event) {
		super.craftEvent(event);
		if (event.getRecipeResult().getItem().getClass().isAssignableFrom(getItemClass())) {
			if (event.getResult() != Result.DENY) {
				ItemAxe axe = (ItemAxe) event.getRecipeResult().getItem();
				int tier = SkillTreeApi.getSkillTier(event.getPlayer(), this);
				ToolMaterial material = ToolMaterial.valueOf(axe.getToolMaterialName());
				int craftDificulty;
				switch (material) {
				case WOOD:
					craftDificulty = 1;
					break;
				case STONE:
					craftDificulty = 2;
					break;
				case GOLD:
					craftDificulty = 3;
					break;
				case IRON:
					craftDificulty = 4;
					break;
				case DIAMOND:
					craftDificulty = 5;
					break;
				default:
					craftDificulty = material.getHarvestLevel();
					break;
				}

				if (craftDificulty > tier)
					event.setResult(Result.DENY);
			}
		}
	}
}
