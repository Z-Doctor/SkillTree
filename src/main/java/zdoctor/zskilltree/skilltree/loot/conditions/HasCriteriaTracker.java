package zdoctor.zskilltree.skilltree.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import zdoctor.zskilltree.api.SkillTreeApi;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.ICriteriaPredicate;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class HasCriteriaTracker implements ILootCondition {
    private final LootContext.EntityTarget target;
    private final Map<ResourceLocation, ICriteriaPredicate> criteriaTrackers;

    public HasCriteriaTracker(LootContext.EntityTarget target, Map<ResourceLocation, ICriteriaPredicate> criteriaTrackers) {
        this.target = target;
        this.criteriaTrackers = criteriaTrackers;
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.get(this.target.getParameter());
        ISkillTreeTracker tracker = SkillTreeApi.getTracker(entity);
        if (tracker == null)
            return false;
        else if (!criteriaTrackers.isEmpty()) {
            for (Map.Entry<ResourceLocation, ICriteriaPredicate> entry : criteriaTrackers.entrySet()) {
                CriterionTracker trackable = tracker.getTracker(entry.getKey());
                if (trackable == null || !entry.getValue().test(tracker.getProgress(trackable)))
                    return false;
            }
        }
        return true;
    }

    @Override
    public Set<LootParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.target.getParameter());
    }


    public static abstract class Serializer<T extends HasCriteriaTracker> implements ILootSerializer<T> {
        private final String name;

        public Serializer(String name) {
            this.name = name;
        }

        private static ICriteriaPredicate deserializeCriterion(JsonElement element) {
            if (element.isJsonPrimitive()) {
                boolean flag = element.getAsBoolean();
                return new ICriteriaPredicate.CompletedCriteriaPredicate(flag);
            } else {
                Object2BooleanMap<String> object2BooleanMap = new Object2BooleanOpenHashMap<>();
                JsonObject jsonobject = JSONUtils.getJsonObject(element, "criterion data");
                jsonobject.entrySet().forEach((criterionEntry) -> {
                    boolean flag = JSONUtils.getBoolean(criterionEntry.getValue(), "criterion test");
                    object2BooleanMap.put(criterionEntry.getKey(), flag);
                });
                return new ICriteriaPredicate.CriteriaPredicate(object2BooleanMap);
            }
        }

        @Override
        public void serialize(JsonObject jsonObject, HasCriteriaTracker instance, JsonSerializationContext context) {
            if (!instance.criteriaTrackers.isEmpty()) {
                JsonObject pageObject = new JsonObject();
                instance.criteriaTrackers.forEach((trackerId, criteriaPredicate) -> pageObject.add(trackerId.toString(), criteriaPredicate.serialize()));
                jsonObject.add(name, pageObject);
            }
            jsonObject.add("entity", context.serialize(instance.target));
        }

        @Override
        public T deserialize(JsonObject jsonObject, JsonDeserializationContext context) {
            Map<ResourceLocation, ICriteriaPredicate> criteriaTrackers = new HashMap<>();

            JsonObject pages = JSONUtils.getJsonObject(jsonObject, name, new JsonObject());
            for (Map.Entry<String, JsonElement> entry : pages.entrySet()) {
                ResourceLocation pageLocation = new ResourceLocation(entry.getKey());
                ICriteriaPredicate criteriaPredicate = deserializeCriterion(entry.getValue());
                criteriaTrackers.put(pageLocation, criteriaPredicate);
            }
            LootContext.EntityTarget type = JSONUtils.deserializeClass(jsonObject, "entity", context, LootContext.EntityTarget.class);
            return create(type, criteriaTrackers);
        }

        public abstract T create(LootContext.EntityTarget type, Map<ResourceLocation, ICriteriaPredicate> criteriaTrackers);
    }
}
