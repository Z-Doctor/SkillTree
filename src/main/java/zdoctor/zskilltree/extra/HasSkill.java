//package zdoctor.zskilltree.extra;
//
//import com.google.common.collect.ImmutableSet;
//import net.minecraft.loot.LootConditionType;
//import net.minecraft.loot.LootContext;
//import net.minecraft.loot.LootParameter;
//import net.minecraft.loot.LootParameters;
//import net.minecraft.loot.conditions.ILootCondition;
//
//import java.util.Set;
//
//public class HasSkill implements ILootCondition {
//    private final SkillPredicate predicate;
//
//    public HasSkill(SkillPredicate predicate) {
//        this.predicate = predicate;
//    }
//
//    @Override
//    public Set<LootParameter<?>> getRequiredParameters() {
//        return ImmutableSet.of(LootParameters.THIS_ENTITY, LootParameters.KILLER_ENTITY,
//                LootParameters.DIRECT_KILLER_ENTITY, LootParameters.LAST_DAMAGE_PLAYER);
//    }
//
//    @Override
//    public boolean test(LootContext lootContext) {
////        LivingEntity entity = (LivingEntity) lootContext.get(predicate.getSource());
////        return entity != null && predicate.test(entity);
//        return false;
//    }
//
//    @Override
//    public LootConditionType func_230419_b_() {
//        return null;
//    }
//
//
////    public static class Serializer extends ILootCondition.AbstractSerializer<HasSkill> {
////
////        public Serializer() {
////            super(new ResourceLocation(McSkillTree.MODID, "has_skill"), HasSkill.class);
////        }
////
////        @Override
////        public void serialize(JsonObject json, HasSkill value, JsonSerializationContext context) {
////            json.add("predicate", value.predicate.serialize());
////        }
////
////        @Override
////        public HasSkill deserialize(JsonObject json, JsonDeserializationContext context) {
////            SkillPredicate predicate = SkillPredicate.deserialize(json.get("predicate"));
////            return new HasSkill(predicate);
////        }
////    }
//
//
//}
