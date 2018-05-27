package zdoctor.mcskilltree.skills.attack;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemSword;
import zdoctor.mcskilltree.skills.AttackSkill;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.SkillAttributeModifier;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;
import zdoctor.skilltree.api.skills.interfaces.ISkillStackable;
import zdoctor.skilltree.api.skills.interfaces.ISkillTickable;
import zdoctor.skilltree.api.skills.interfaces.ISkillToggle;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.SkillSlot;
import zdoctor.skilltree.tabs.SkillTabs;

public class SwordProficiency extends AttackSkill implements ISkillToggle, ISkillTickable, ISkillStackable {
	public final SkillAttributeModifier SWORD_PROFICIENCY;
	public static final String ATTRIBUTE_NAME = "attackSkill.swordProficiency";

	public SwordProficiency() {
		super("SwordProficiency", SkillTabs.enchantItem(Items.DIAMOND_SWORD));
		SWORD_PROFICIENCY = new SkillAttributeModifier(ATTRIBUTE_NAME, 0, 0);
		setFrameType(SkillFrameType.SPECIAL);
	}

	@Override
	public void onSkillRePurchase(EntityLivingBase entity) {
		removeEntityModifier(entity, this);
	}

	@Override
	public void removeEntityModifier(EntityLivingBase entity, SkillBase skill) {
		for (AttributeModifier mod : entity.getEntityAttribute(getAttribute(entity, this)).getModifiers()) {
			if (mod.getName().equalsIgnoreCase(ATTRIBUTE_NAME)) {
				entity.getEntityAttribute(getAttribute(entity, skill)).removeModifier(mod);
			}
		}
	}

	@Override
	public void modifyEntity(EntityLivingBase entity, SkillBase skill) {
		int tier2 = SkillTreeApi.getSkillTier(entity, skill);
		if (!entity.getEntityAttribute(getAttribute(entity, skill)).hasModifier(getModifier(entity, skill))) {
			if (entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof ItemSword) {
				int tier = SkillTreeApi.getSkillTier(entity, skill);
				SkillAttributeModifier swordProficiency = new SkillAttributeModifier(ATTRIBUTE_NAME, tier, 0);
				entity.getEntityAttribute(getAttribute(entity, skill)).applyModifier(swordProficiency);
				entity.getEntityAttribute(getAttribute(entity, skill)).applyModifier(getModifier(entity, skill));
			}
		}
	}

	@Override
	public void onActiveTick(EntityLivingBase entity, SkillBase skill, SkillSlot skillSlot) {
		if (!(entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof ItemSword)) {
			if (entity.getEntityAttribute(getAttribute(entity, skill)).hasModifier(getModifier(entity, skill)))
				removeEntityModifier(entity, skill);
		} else if (!entity.getEntityAttribute(getAttribute(entity, skill)).hasModifier(getModifier(entity, skill)))
			modifyEntity(entity, skill);
	}

	// First one is free, but each level requires more xp
	@Override
	public List<ISkillRequirment> getRequirments(EntityLivingBase entity, boolean hasSkill) {
		if (hasSkill) {
			return Collections.singletonList(new LevelRequirement(SkillTreeApi.getSkillTier(entity, this)));
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	@Override
	public SkillAttributeModifier getModifier(EntityLivingBase entity, SkillBase skill) {
		return SWORD_PROFICIENCY;
	}

}
