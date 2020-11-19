package zdoctor.mcskilltree.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.skills.criterion.SkillPredicate;

import java.util.Set;

public class HasSkill implements ILootCondition {
    private final SkillPredicate predicate;

    public HasSkill(SkillPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public Set<LootParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootParameters.THIS_ENTITY, LootParameters.KILLER_ENTITY,
                LootParameters.DIRECT_KILLER_ENTITY, LootParameters.LAST_DAMAGE_PLAYER);
    }

    @Override
    public boolean test(LootContext lootContext) {
        LivingEntity entity = (LivingEntity) lootContext.get(predicate.getSource());
        return entity != null && predicate.test(entity);
    }


    public static class Serializer extends ILootCondition.AbstractSerializer<HasSkill> {

        public Serializer() {
            super(new ResourceLocation(McSkillTree.MODID, "has_skill"), HasSkill.class);
        }

        @Override
        public void serialize(JsonObject json, HasSkill value, JsonSerializationContext context) {
            json.add("predicate", value.predicate.serialize());
        }

        @Override
        public HasSkill deserialize(JsonObject json, JsonDeserializationContext context) {
            SkillPredicate predicate = SkillPredicate.deserialize(json.get("predicate"));
            return new HasSkill(predicate);
        }
    }


}
