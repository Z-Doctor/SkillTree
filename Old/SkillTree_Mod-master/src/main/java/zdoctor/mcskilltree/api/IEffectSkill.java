package zdoctor.mcskilltree.api;

import net.minecraft.entity.LivingEntity;

public interface IEffectSkill {

    /**
     * Applies the skill the the entity, as if they already had the skill.
     *
     * @param entity - The entity to apply the skill to: typically the owner of the skill.
     */
    void applySkill(LivingEntity entity);

    /**
     * Removes the effects from a skill from the entity
     * @param entity - The entity the skill was applied to: typically the owner of the skill.
     */
    void removeSkill(LivingEntity entity);
}
