package com.zdoctorsmods.skilltreemod.skills.loot.critereon;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class SkillTriggers {
    public static final AlwaysTrigger ALWAYS_TRIGGER = register(new AlwaysTrigger());
    public static final SkillPurchasedTrigger SKILL_PURCHASED = register(new SkillPurchasedTrigger());

    public static <T extends CriterionTrigger<?>> T register(T pCriterion) {
        return CriteriaTriggers.register(pCriterion);
    }

    public static final void init() {
    }

}
