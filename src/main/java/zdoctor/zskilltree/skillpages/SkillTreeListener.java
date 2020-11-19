package zdoctor.zskilltree.skillpages;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.handlers.PlayerSkillTreeTracker;

public class SkillTreeListener<T extends ICriterionInstance> extends ICriterionTrigger.Listener<T> {
    private final T criterionInstance;
    private final CriterionTracker trackable;
    private final String criterionName;

    public SkillTreeListener(T criterionInstanceIn, CriterionTracker trackableIn, String criterionNameIn) {
        super(criterionInstanceIn, null, criterionNameIn);
        this.criterionInstance = criterionInstanceIn;
        this.trackable = trackableIn;
        this.criterionName = criterionNameIn;
    }

    @Override
    public T getCriterionInstance() {
        return criterionInstance;
    }

    @Override
    public void grantCriterion(PlayerAdvancements playerAdvancementsIn) {
        if (playerAdvancementsIn instanceof PlayerSkillTreeTracker.SkillTreeAdvancementWrapper) {
            PlayerSkillTreeTracker handler = ((PlayerSkillTreeTracker.SkillTreeAdvancementWrapper) playerAdvancementsIn).getSkillTreeHandler();
            handler.grantCriterion(trackable, criterionName);
        }
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            SkillTreeListener<T> listener = (SkillTreeListener<T>) other;
            if (!this.criterionInstance.equals(listener.criterionInstance)) {
                return false;
            } else {
                return this.trackable.equals(listener.trackable) && this.criterionName.equals(listener.criterionName);
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int i = this.criterionInstance.hashCode();
        i = 31 * i + this.trackable.hashCode();
        return 31 * i + this.criterionName.hashCode();
    }
}
