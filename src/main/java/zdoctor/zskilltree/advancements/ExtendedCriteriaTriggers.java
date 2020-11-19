package zdoctor.zskilltree.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import zdoctor.zskilltree.advancements.criterion.AdvancementUnlockedTrigger;
import zdoctor.zskilltree.api.interfaces.IProgressTracker;
import zdoctor.zskilltree.skillpages.SkillPage;

public class ExtendedCriteriaTriggers {
    public static final AdvancementUnlockedTrigger Advancement_Unlocked = register(new AdvancementUnlockedTrigger());

    public static <T extends ICriterionTrigger<?>> T register(T criterion) {
        return CriteriaTriggers.register(criterion);
    }

    public static void init() {
    }

    public static class SkillPageListener<T extends ICriterionInstance> {
        private final T criterionInstance;
        private final SkillPage skillPage;
        private final String criterionName;

        public SkillPageListener(T criterionInstanceIn, SkillPage skillPageIn, String criterionNameIn) {
            this.criterionInstance = criterionInstanceIn;
            this.skillPage = skillPageIn;
            this.criterionName = criterionNameIn;
        }

        public T getCriterionInstance() {
            return this.criterionInstance;
        }

        public void grantCriterion(IProgressTracker skillTreeHandler) {
//            skillTreeHandler.grantCriterion(this.skillPage, this.criterionName);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other != null && this.getClass() == other.getClass()) {
                SkillPageListener<?> listener = (SkillPageListener<?>) other;
                if (!this.criterionInstance.equals(listener.criterionInstance)) {
                    return false;
                } else {
                    return this.skillPage.equals(listener.skillPage) && this.criterionName.equals(listener.criterionName);
                }
            } else {
                return false;
            }
        }

        public int hashCode() {
            int i = this.criterionInstance.hashCode();
            i = 31 * i + this.skillPage.hashCode();
            return 31 * i + this.criterionName.hashCode();
        }
    }
}
