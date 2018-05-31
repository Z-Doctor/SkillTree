package zdoctor.skilltree.api.skills.interfaces;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import zdoctor.skilltree.api.skills.SkillAttributeModifier;

public interface ISkillAtribute {
	public void modifyEntity(EntityLivingBase entity, ISkill skill);

	public void removeEntityModifier(EntityLivingBase entity, ISkill skill);

	@Nullable
	public SkillAttributeModifier getModifier(EntityLivingBase entity, ISkill skill);

	public IAttribute getAttribute(EntityLivingBase entity, ISkill skill);
}
