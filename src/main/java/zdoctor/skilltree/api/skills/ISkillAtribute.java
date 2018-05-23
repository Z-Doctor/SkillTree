package zdoctor.skilltree.api.skills;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.skills.AttributeSkill;

public interface ISkillAtribute {
	public default void modifyEntity(EntityLivingBase entity) {
		if (!entity.getEntityAttribute(getAttribute()).hasModifier(getModifier())) {
			entity.getEntityAttribute(getAttribute()).applyModifier(getModifier());
			ModMain.proxy.log.debug("Apply Modifier '{}' to '{}'", getModifier(), entity);
		}
	}

	public default void removeEntityModifier(EntityLivingBase entity) {
		if (entity.getEntityAttribute(getAttribute()).hasModifier(getModifier())) {
			entity.getEntityAttribute(getAttribute()).removeModifier(getModifier());
			ModMain.proxy.log.debug("Removing Modifier '{}' to '{}'", getModifier(), entity);
		}
	}

	public AttributeModifier getModifier();

	public IAttribute getAttribute();
}
