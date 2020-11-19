package zdoctor.zskilltree.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import zdoctor.zskilltree.advancements.criterion.AdvancementUnlockedTrigger;

public class ExtendedCriteriaTriggers {
    public static final AdvancementUnlockedTrigger Advancement_Unlocked = register(new AdvancementUnlockedTrigger());

    public static <T extends ICriterionTrigger<?>> T register(T criterion) {
        return CriteriaTriggers.register(criterion);
    }

    public static void init() {
    }
}
