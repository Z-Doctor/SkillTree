package zdoctor.zskilltree.skilltree.data.builders;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.PlayerPredicate;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Builder<T extends Builder<T, R>, R> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected Map<String, Criterion> criteria = new HashMap<>();
    protected IRequirementsStrategy requirementsStrategy = IRequirementsStrategy.AND;

    public LootConditionBuilder<T> makeTrigger(String key, Function<EntityPredicate.AndPredicate, ICriterionInstance> function) {
        Consumer<EntityPredicate.AndPredicate> consumer = predicate -> withTrigger(key, function.apply(predicate));
        return new LootConditionBuilder<>((T) this, consumer);
    }

    public T withTrigger(String key, ICriterionInstance criterionIn) {
        return this.withCriterion(key, new Criterion(criterionIn));
    }

    public T withCriterion(String key, Criterion criterionIn) {
        if (this.criteria.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate criterion " + key);
        } else {
            this.criteria.put(key, criterionIn);
            return (T) this;
        }
    }

    public T withRequirementsStrategy(IRequirementsStrategy strategy) {
        this.requirementsStrategy = strategy;
        return (T) this;
    }

    public R register(Consumer<R> consumer, String id) {
        return register(consumer, new ResourceLocation(ModMain.MODID, id));
    }

    public R register(Consumer<R> consumer, ResourceLocation id) {
        R built = build(id);
        consumer.accept(built);
        return built;
    }

    public abstract T copy();

    public abstract R build(ResourceLocation id);

    public static class LootConditionBuilder<T> {
        private final T $return;
        private final Consumer<EntityPredicate.AndPredicate> consumer;
        private final Set<ILootCondition> conditions = new LinkedHashSet<>();

        public LootConditionBuilder(T $return, Consumer<EntityPredicate.AndPredicate> consumer) {
            this.$return = $return;
            this.consumer = consumer;
        }

        private LootConditionBuilder() {
            this.$return = (T) this;
            this.consumer = $ -> {
            };
        }

        public static <T extends LootConditionBuilder<T>> LootConditionBuilder<T> create() {
            return new LootConditionBuilder<>();
        }

        public EntityPredicate.AndPredicate build() {
            return build(EntityPredicate.ANY);
        }

        public EntityPredicate.AndPredicate build(EntityPredicate predicate) {
            List<ILootCondition> conditions = new ArrayList<>();
            if (predicate != EntityPredicate.ANY)
                conditions.add(EntityHasProperty.func_237477_a_(LootContext.EntityTarget.THIS, predicate).build());
            conditions.addAll(this.conditions);

            return EntityPredicate.AndPredicate.serializePredicate(conditions.toArray(new ILootCondition[0]));
        }

        public T make() {
            return make(EntityPredicate.ANY);
        }

        public T make(EntityPredicate predicate) {
            consumer.accept(build(predicate));
            return $return;
        }

        public PlayerBuilder<T> withPlayer() {
            Consumer<PlayerPredicate> consumer = playerPredicate -> {
                EntityPredicate player = EntityPredicate.Builder.create().player(playerPredicate).build();
                make(player);
            };
            return new Builder.PlayerBuilder<>($return, consumer);
        }

        public LootConditionBuilder<T> withCondition(ILootCondition lootCondition) {
            conditions.add(lootCondition);
            return this;
        }

        public LootConditionBuilder<T> withConditions(ILootCondition... lootConditions) {
            conditions.addAll(Arrays.asList(lootConditions.clone()));
            return this;
        }
    }

    public static class PlayerBuilder<T> extends PlayerPredicateBuilder<PlayerBuilder<T>> {
        private final T $return;
        private final Consumer<PlayerPredicate> consumer;

        public PlayerBuilder(T $return, Consumer<PlayerPredicate> consumer) {
            this.$return = $return;
            this.consumer = consumer;
        }

        public PlayerPredicate build() {
            return build(this);
        }

        public EntityPredicate buildEntity() {
            return EntityPredicate.Builder.create().player(build()).build();
        }

        public EntityPredicate.AndPredicate buildEntityAnd() {
            return EntityPredicate.AndPredicate.createAndFromEntityCondition(buildEntity());
        }

        public T make() {
            consumer.accept(build());
            return $return;
        }
    }
}
