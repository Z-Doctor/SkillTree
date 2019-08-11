package zdoctor.skilltree.api.skills.interfaces;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.skills.SkillAttributeModifier;
import zdoctor.skilltree.skills.SkillBase;

public interface ISkillAtribute {
	public void modifyEntity(EntityLivingBase entity, SkillBase skill);

	public void removeEntityModifier(EntityLivingBase entity, SkillBase skill);

	@Nullable
	public SkillAttributeModifier getModifier(EntityLivingBase entity, SkillBase skill);

	public IAttribute getAttribute(EntityLivingBase entity, SkillBase skill);
}
