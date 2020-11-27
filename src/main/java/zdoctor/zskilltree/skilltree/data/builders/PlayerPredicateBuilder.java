package zdoctor.zskilltree.skilltree.data.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PlayerPredicate;
import net.minecraft.stats.Stat;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class PlayerPredicateBuilder {
    private MinMaxBounds.IntBound level = MinMaxBounds.IntBound.UNBOUNDED;
    private GameType gamemode = GameType.NOT_SET;
    private final Map<Stat<?>, MinMaxBounds.IntBound> stats = new HashMap<>();
    private final Object2BooleanMap<ResourceLocation> recipes = new Object2BooleanOpenHashMap<>();
    private final Map<ResourceLocation, IAdvancementPredicate> advancements = new HashMap<>();


    private PlayerPredicateBuilder() {
    }

    public static PlayerPredicateBuilder create() {
        return new PlayerPredicateBuilder();
    }

    public static IAdvancementPredicate deserializeAdvancementPredicate(JsonElement element) {
        if (element.isJsonPrimitive()) {
            boolean flag = element.getAsBoolean();
            return new CompletedAdvancementPredicate(flag);
        } else {
            Object2BooleanMap<String> object2booleanmap = new Object2BooleanOpenHashMap<>();
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "criterion data");
            jsonobject.entrySet().forEach((criterionEntry) -> {
                boolean flag1 = JSONUtils.getBoolean(criterionEntry.getValue(), "criterion test");
                object2booleanmap.put(criterionEntry.getKey(), flag1);
            });
            return new CriteriaPredicate(object2booleanmap);
        }
    }

    private static <T> ResourceLocation getRegistryKeyForStat(Stat<T> stat) {
        return stat.getType().getRegistry().getKey(stat.getValue());
    }

    public PlayerPredicateBuilder withBounds(MinMaxBounds.IntBound level) {
        this.level = level;
        return this;
    }

    public PlayerPredicateBuilder withMode(GameType gamemode) {
        this.gamemode = gamemode;
        return this;
    }

    public PlayerPredicateBuilder addStat(Stat<?> stat, MinMaxBounds.IntBound bound) {
        this.stats.put(stat, bound);
        return this;
    }

    public PlayerPredicateBuilder addAllStats(Map<Stat<?>, MinMaxBounds.IntBound> stats) {
        this.stats.putAll(stats);
        return this;
    }

    public PlayerPredicateBuilder hasRecipe(ResourceLocation recipe) {
        return putRecipe(recipe, true);
    }

    public PlayerPredicateBuilder withoutRecipe(ResourceLocation recipe) {
        return putRecipe(recipe, false);
    }

    private PlayerPredicateBuilder putRecipe(ResourceLocation recipe, boolean hasRecipe) {
        this.recipes.put(recipe, hasRecipe);
        return this;
    }

    public PlayerPredicateBuilder hasAdvancement(ResourceLocation advancementId) {
        return putAdvancement(advancementId, true);
    }

    public PlayerPredicateBuilder withoutAdvancement(ResourceLocation advancementId) {
        return putAdvancement(advancementId, false);
    }

    private PlayerPredicateBuilder putAdvancement(ResourceLocation recipe, boolean hasAdvancement) {
        this.advancements.put(recipe, new CompletedAdvancementPredicate(hasAdvancement));
        return this;
    }

    public CriterionBuilder withCriteria(ResourceLocation advancementId) {
        return new CriterionBuilder(this, advancementId);
    }

    public PlayerPredicate build() {
        return PlayerPredicate.deserialize(serialize());
    }

    public JsonElement serialize() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.add("level", this.level.serialize());
        if (this.gamemode != GameType.NOT_SET) {
            jsonobject.addProperty("gamemode", this.gamemode.getName());
        }

        if (!this.stats.isEmpty()) {
            JsonArray jsonarray = new JsonArray();
            this.stats.forEach((stat, value) -> {
                JsonObject jsonobject3 = new JsonObject();
                jsonobject3.addProperty("type", ForgeRegistries.STAT_TYPES.getKey(stat.getType()).toString());
                jsonobject3.addProperty("stat", getRegistryKeyForStat(stat).toString());
                jsonobject3.add("value", value.serialize());
                jsonarray.add(jsonobject3);
            });
            jsonobject.add("stats", jsonarray);
        }

        if (!this.recipes.isEmpty()) {
            JsonObject jsonobject1 = new JsonObject();
            this.recipes.forEach((recipeID, unlocked) -> jsonobject1.addProperty(recipeID.toString(), unlocked));
            jsonobject.add("recipes", jsonobject1);
        }

        if (!this.advancements.isEmpty()) {
            JsonObject jsonobject2 = new JsonObject();
            this.advancements.forEach((advancementID, playerAdvancements) -> jsonobject2.add(advancementID.toString(), playerAdvancements.serialize()));
            jsonobject.add("advancements", jsonobject2);
        }

        return jsonobject;
    }

    public interface IAdvancementPredicate extends Predicate<AdvancementProgress> {
        JsonElement serialize();
    }

    public static class CriterionBuilder {
        private final PlayerPredicateBuilder builder;
        private final ResourceLocation advancementId;
        private final Object2BooleanMap<String> object2booleanMap = new Object2BooleanOpenHashMap<>();

        public CriterionBuilder(PlayerPredicateBuilder builder, ResourceLocation advancementId) {
            this.builder = builder;
            this.advancementId = advancementId;
        }

        public CriterionBuilder has(String key) {
            return putCriteria(key, true);
        }

        public CriterionBuilder without(String key) {
            return putCriteria(key, false);
        }

        private CriterionBuilder putCriteria(String key, boolean isObtained) {
            object2booleanMap.put(key, isObtained);
            return this;
        }

        public PlayerPredicateBuilder build() {
            CriteriaPredicate predicate = new CriteriaPredicate(object2booleanMap);
            builder.advancements.put(advancementId, predicate);
            return builder;
        }
    }

    public static class CriteriaPredicate implements IAdvancementPredicate {
        private final Object2BooleanMap<String> completion;

        public CriteriaPredicate(Object2BooleanMap<String> completion) {
            this.completion = completion;
        }

        @Override
        public JsonElement serialize() {
            JsonObject jsonobject = new JsonObject();
            this.completion.forEach(jsonobject::addProperty);
            return jsonobject;
        }

        @Override
        public boolean test(AdvancementProgress progress) {
            for (Object2BooleanMap.Entry<String> entry : this.completion.object2BooleanEntrySet()) {
                CriterionProgress criterionprogress = progress.getCriterionProgress(entry.getKey());
                if (criterionprogress == null || criterionprogress.isObtained() != entry.getBooleanValue()) {
                    return false;
                }
            }

            return true;
        }
    }

    public static class CompletedAdvancementPredicate implements IAdvancementPredicate {
        private final boolean completion;

        public CompletedAdvancementPredicate(boolean completion) {
            this.completion = completion;
        }

        @Override
        public JsonElement serialize() {
            return new JsonPrimitive(this.completion);
        }

        @Override
        public boolean test(AdvancementProgress p_test_1_) {
            return p_test_1_.isDone() == this.completion;
        }
    }
}
