package zdoctor.skilltree.api.skills;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.skills.interfaces.ISkillAtribute;
import zdoctor.skilltree.skills.SkillBase;

/**
 * For information about Attribute check
 * {@link https://minecraft.gamepedia.com/Attribute}
 *
 */
public abstract class AttributeSkill extends Skill implements ISkillAtribute {
	protected final IAttribute attribute;

	public AttributeSkill(String name, Item icon, IAttribute attributeIn) {
		this(name, new ItemStack(icon), attributeIn);
	}

	public AttributeSkill(String name, ItemStack icon, IAttribute attributeIn) {
		super(name, icon);
		this.attribute = attributeIn;
	}

	@Override
	public IAttribute getAttribute(EntityLivingBase entity, SkillBase skill) {
		return attribute;
	}

	@Override
	public void onSkillActivated(EntityLivingBase entity) {
		if (entity.getEntityAttribute(getAttribute(entity, this)) != null)
			modifyEntity(entity, this);
	}

	@Override
	public void onSkillDeactivated(EntityLivingBase entity) {
		if (entity.getEntityAttribute(getAttribute(entity, this)) != null)
			removeEntityModifier(entity, this);
	}

	@Override
	public void modifyEntity(EntityLivingBase entity, SkillBase skill) {
		SkillAttributeModifier modifier = getModifier(entity, skill);
		if (modifier != null && !entity.getEntityAttribute(getAttribute(entity, skill)).hasModifier(modifier)) {
			entity.getEntityAttribute(getAttribute(entity, skill)).applyModifier(modifier);
			ModMain.proxy.log.debug("Apply Modifier '{}' to '{}'", modifier, entity);
		}
	}

	@Override
	public void removeEntityModifier(EntityLivingBase entity, SkillBase skill) {
		SkillAttributeModifier modifier = getModifier(entity, skill);
		if (modifier != null && entity.getEntityAttribute(getAttribute(entity, skill)).hasModifier(modifier)) {
			entity.getEntityAttribute(getAttribute(entity, skill)).removeModifier(modifier);
			ModMain.proxy.log.debug("Removing Modifier '{}' to '{}'", modifier, entity);
		}
	}
}
