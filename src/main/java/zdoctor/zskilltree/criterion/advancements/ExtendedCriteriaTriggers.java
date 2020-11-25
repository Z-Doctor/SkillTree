package zdoctor.zskilltree.criterion.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import zdoctor.zskilltree.criterion.advancements.triggers.AdvancementUnlockedTrigger;
import zdoctor.zskilltree.criterion.advancements.triggers.SkillPageUnlockedTrigger;

public class ExtendedCriteriaTriggers {
    public static final AdvancementUnlockedTrigger Advancement_Unlocked = register(new AdvancementUnlockedTrigger());
    public static final SkillPageUnlockedTrigger SkillPage_Unlocked = register(new SkillPageUnlockedTrigger());

    public static <T extends ICriterionTrigger<?>> T register(T criterion) {
        return CriteriaTriggers.register(criterion);
    }

    public static void init() {
    }
}
