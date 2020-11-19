package zdoctor.zskilltree;

import com.google.common.collect.Maps;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public class SkillTreeCriteriaTriggers {
    private static final Map<ResourceLocation, ICriterionTrigger<?>> REGISTRY = Maps.newHashMap();

    public static final ImpossibleTrigger IMPOSSIBLE = CriteriaTriggers.IMPOSSIBLE;

    public static <T extends ICriterionTrigger<?>> T register(T criterion) {
        if (REGISTRY.containsKey(criterion.getId())) {
            throw new IllegalArgumentException("Duplicate criterion id " + criterion.getId());
        } else {
            REGISTRY.put(criterion.getId(), criterion);
            return criterion;
        }
    }

    @Nullable
    public static <T extends ICriterionInstance> ICriterionTrigger<T> get(ResourceLocation id) {
        return (ICriterionTrigger<T>) REGISTRY.get(id);
    }

    public static Iterable<? extends ICriterionTrigger<?>> getAll() {
        return REGISTRY.values();
    }
}
