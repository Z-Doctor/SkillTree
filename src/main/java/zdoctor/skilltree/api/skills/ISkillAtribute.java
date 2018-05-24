package zdoctor.skilltree.api.skills;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.skills.SkillBase;

public interface ISkillAtribute {
	public default void modifyEntity(EntityLivingBase entity, SkillBase skill) {
		SkillAttributeModifier modifier = getModifier(entity, skill);
		if (modifier != null && !entity.getEntityAttribute(getAttribute(entity, skill)).hasModifier(modifier)) {
			entity.getEntityAttribute(getAttribute(entity, skill)).applyModifier(modifier);
			ModMain.proxy.log.debug("Apply Modifier '{}' to '{}'", modifier, entity);
		}
	}

	public default void removeEntityModifier(EntityLivingBase entity, SkillBase skill) {
		SkillAttributeModifier modifier = getModifier(entity, skill);
		if (modifier != null && entity.getEntityAttribute(getAttribute(entity, skill)).hasModifier(modifier)) {
			entity.getEntityAttribute(getAttribute(entity, skill)).removeModifier(modifier);
			ModMain.proxy.log.debug("Removing Modifier '{}' to '{}'", modifier, entity);
		}
	}

	@Nullable
	public SkillAttributeModifier getModifier(EntityLivingBase entity, SkillBase skill);

	public IAttribute getAttribute(EntityLivingBase entity, SkillBase skill);
}
