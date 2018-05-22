package zdoctor.skilltree.skills;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zdoctor.skilltree.ModMain;

/**
 * For information about Attribute check
 * {@link https://minecraft.gamepedia.com/Attribute}
 *
 */
public class AttributeSkill extends Skill {
	protected final AttributeModifier modifier;
	protected final IAttribute attribute;

	public AttributeSkill(String name, Item icon, IAttribute attributeIn, AttributeModifier modifierIn) {
		this(name, new ItemStack(icon), attributeIn, modifierIn);
	}

	public AttributeSkill(String name, ItemStack icon, IAttribute attributeIn, AttributeModifier modifierIn) {
		super(name, icon);
		this.attribute = attributeIn;
		this.modifier = modifierIn;
		modifierIn.setSaved(false);
	}

	public void modifyEntity(EntityLivingBase entity) {
		if (!entity.getEntityAttribute(getAttribute()).hasModifier(getModifier())) {
			entity.getEntityAttribute(getAttribute()).applyModifier(getModifier());
			ModMain.proxy.log.debug("Apply Modifier '{}' to '{}'", getModifier(), entity);
		}
	}

	public void removeEntityModifier(EntityLivingBase entity) {
		if (entity.getEntityAttribute(getAttribute()).hasModifier(getModifier())) {
			entity.getEntityAttribute(getAttribute()).removeModifier(getModifier());
			ModMain.proxy.log.debug("Removing Modifier '{}' to '{}'", getModifier(), entity);
		}
	}

	public AttributeModifier getModifier() {
		return modifier;
	}

	public IAttribute getAttribute() {
		return attribute;
	}

	@Override
	public void onSkillActivated(EntityLivingBase entity) {
		if (entity.getEntityAttribute(getAttribute()) != null)
			modifyEntity(entity);
	}

	@Override
	public void onSkillDeactivated(EntityLivingBase entity) {
		if (entity.getEntityAttribute(getAttribute()) != null)
			removeEntityModifier(entity);
	}
}
