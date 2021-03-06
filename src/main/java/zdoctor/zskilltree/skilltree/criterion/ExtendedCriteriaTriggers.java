package zdoctor.zskilltree.skilltree.criterion;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import zdoctor.zskilltree.skilltree.criterion.triggers.AdvancementUnlockedTrigger;
import zdoctor.zskilltree.skilltree.criterion.triggers.SkillUnlockedTrigger;

public class ExtendedCriteriaTriggers {
    public static final AdvancementUnlockedTrigger ADVANCEMENT_UNLOCKED = register(new AdvancementUnlockedTrigger());
//    public static final SkillPageTrigger SKILL_PAGE_TRIGGER = register(new SkillPageTrigger(false));
//    public static final SkillPageTrigger ANY_SKILL_PAGE_TRIGGER = register(new SkillPageTrigger(true));
    public static final SkillUnlockedTrigger SKILL_UNLOCKED = register(new SkillUnlockedTrigger());

    public static <T extends ICriterionTrigger<?>> T register(T criterion) {
        return CriteriaTriggers.register(criterion);
    }

    public static void init() {
    }
}
