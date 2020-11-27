package zdoctor.zskilltree.skilltree.loot.conditions;

import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;
import zdoctor.zskilltree.api.interfaces.ICriteriaPredicate;

import java.util.Map;

public class HasSkill extends HasCriteriaTracker {

    private HasSkill(LootContext.EntityTarget target, Map<ResourceLocation, ICriteriaPredicate> criteriaTrackers) {
        super(target, criteriaTrackers);
    }

    public static IBuilder builder(LootContext.EntityTarget target, Map<ResourceLocation, ICriteriaPredicate> criteriaTrackers) {
        return () -> new HasSkill(target, criteriaTrackers);
    }

    @Override
    public LootConditionType func_230419_b_() {
        return AdditionalConditions.HAS_SKILL;
    }

    public static class Serializer extends HasCriteriaTracker.Serializer<HasSkill> {
        public Serializer() {
            super("skills");
        }

        @Override
        public HasSkill create(LootContext.EntityTarget type, Map<ResourceLocation, ICriteriaPredicate> criteriaTrackers) {
            return new HasSkill(type, criteriaTrackers);
        }

    }
}
