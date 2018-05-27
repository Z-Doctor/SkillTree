package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import zdoctor.mcskilltree.skills.pages.CraftingSkillPage;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.requirements.DescriptionRequirment;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;

public class ArrowCraftSkill extends ItemCrafterSkill {
	public ArrowCraftSkill(Class<? extends Item> itemClass, String name, Item icon) {
		this(itemClass, name, new ItemStack(icon));
	}

	public ArrowCraftSkill(Class<? extends Item> itemClass, String name, ItemStack icon) {
		super(itemClass, name, icon);
	}

	public ArrowCraftSkill() {
		super(ItemArrow.class, "ArrowCraftSkill", Items.ARROW);
		addRequirement(new LevelRequirement(15));
		addRequirement(new SkillPointRequirement(1));
		setParent(CraftingSkillPage.BOW_CRAFTER);
	}

	@Override
	public boolean shouldDrawSkill(EntityLivingBase entity) {
		return super.shouldDrawSkill(entity) && (this == CraftingSkillPage.ARROW_CRAFTER
				|| SkillTreeApi.hasSkill(entity, CraftingSkillPage.ARROW_CRAFTER));
	}

}
