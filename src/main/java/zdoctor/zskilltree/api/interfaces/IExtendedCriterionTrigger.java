package zdoctor.zskilltree.api.interfaces;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;

public interface IExtendedCriterionTrigger<T extends ICriterionInstance> extends ICriterionTrigger<T> {
    default boolean stopListeningOnCompletion() {
        return true;
    }
}
