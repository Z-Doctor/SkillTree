package zdoctor.mcskilltree.api;

import zdoctor.mcskilltree.skills.Skill;

@FunctionalInterface
public interface ISkillGetter<T> {
    T get(Skill skill, ISkillHandler handler);
}