package zdoctor.zskilltree.skilltree.loot.conditions;

import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import zdoctor.zskilltree.api.interfaces.ICriteriaPredicate;

import java.util.Collections;
import java.util.Map;

public class HasSkillPage extends HasCriteriaTracker {

    private HasSkillPage(LootContext.EntityTarget target, Map<ResourceLocation, ICriteriaPredicate> criteriaTrackers) {
        super(target, criteriaTrackers);
    }

    public static ILootCondition.IBuilder builder(LootContext.EntityTarget target, Map<ResourceLocation, ICriteriaPredicate> criteriaTrackers) {
        return () -> new HasSkillPage(target, criteriaTrackers);
    }

    public static ILootCondition.IBuilder builder(LootContext.EntityTarget target, ResourceLocation pageId) {
        return () -> new HasSkillPage(target, Collections.singletonMap(pageId, new ICriteriaPredicate.CompletedCriteriaPredicate(true)));
    }

    @Override
    public LootConditionType func_230419_b_() {
        return AdditionalConditions.HAS_SKILL_PAGE;
    }

    public static class Serializer extends HasCriteriaTracker.Serializer<HasSkillPage> {
        public Serializer() {
            super("pages");
        }

        @Override
        public HasSkillPage create(LootContext.EntityTarget type, Map<ResourceLocation, ICriteriaPredicate> criteriaTrackers) {
            return new HasSkillPage(type, criteriaTrackers);
        }

    }
}
