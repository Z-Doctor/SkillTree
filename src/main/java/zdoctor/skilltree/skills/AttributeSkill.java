package zdoctor.skilltree.skills;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zdoctor.skilltree.api.skills.ISkillAtribute;

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
}
