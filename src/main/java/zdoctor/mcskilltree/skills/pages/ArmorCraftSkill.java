package zdoctor.mcskilltree.skills.pages;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import zdoctor.mcskilltree.event.CraftingEvent;
import zdoctor.mcskilltree.skills.crafting.ItemCrafterSkill;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.interfaces.ISkillStackable;

public class ArmorCraftSkill extends ItemCrafterSkill implements ISkillStackable {

	private EntityEquipmentSlot slot;

	public ArmorCraftSkill(EntityEquipmentSlot slot, String name, ItemArmor icon) {
		super(ItemArmor.class, name, icon);
		this.slot = slot;
	}

	@Override
	public void onSkillRePurchase(EntityLivingBase entity) {
		// TODO Toast
	}

	@Override
	public int getMaxTier(EntityLivingBase entity) {
		// -1 for chain
		return ArmorMaterial.values().length - 1;
	}

	@Override
	public SkillFrameType getFrameType(EntityLivingBase entity) {
		return SkillTreeApi.getSkillTier(entity, this) >= 4 ? SkillFrameType.SPECIAL
				: SkillTreeApi.getSkillTier(entity, this) == 3 ? SkillFrameType.ROUNDED : SkillFrameType.NORMAL;
	}

	@Override
	public void craftEvent(CraftingEvent event) {
		super.craftEvent(event);
		if (getItemClass().isAssignableFrom(event.getRecipeResult().getItem().getClass())) {
			if (event.getResult() != Result.DENY) {

				if (((ItemArmor) event.getRecipeResult().getItem()).getEquipmentSlot() != slot)
					return;

				ItemArmor armor = (ItemArmor) event.getRecipeResult().getItem();
				int tier = SkillTreeApi.getSkillTier(event.getPlayer(), this);
				ArmorMaterial material = armor.getArmorMaterial();
				int craftDificulty;
				switch (material) {
				case LEATHER:
					craftDificulty = 1;
					break;
				case GOLD:
					craftDificulty = 2;
					break;
				case IRON:
					craftDificulty = 3;
					break;
				case DIAMOND:
					craftDificulty = 4;
					break;
				default:
					// -1 for chain
					craftDificulty = material.ordinal() - 1;
					break;
				}

				if (craftDificulty > tier)
					event.setResult(Result.DENY);
			}
		}
	}

}
