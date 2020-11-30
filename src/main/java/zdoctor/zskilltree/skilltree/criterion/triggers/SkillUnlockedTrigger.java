package zdoctor.zskilltree.skilltree.criterion.triggers;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import zdoctor.zskilltree.ModMain;

// TODO Make one for skill pages, perhaps also make a underlying base class
public class SkillUnlockedTrigger extends AbstractCriterionTrigger<SkillUnlockedTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(ModMain.MODID, "skill_unlocked");

    @Override
    protected Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        ResourceLocation skillId = ResourceLocation.tryCreate(JSONUtils.getString(json, "skill"));
        return new Instance(entityPredicate, skillId);
    }

    public void triggerListeners(ServerPlayerEntity serverPlayer, ResourceLocation skillId) {
        super.triggerListeners(serverPlayer, instance -> instance.test(skillId));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static class Instance extends CriterionInstance {
        private final ResourceLocation skillId;

        public Instance(EntityPredicate.AndPredicate player, ResourceLocation skillId) {
            super(ID, player);
            this.skillId = skillId;
        }

        public static Instance of(EntityPredicate.AndPredicate player, ResourceLocation skillId) {
            return new Instance(player, skillId);
        }

        public boolean test(ResourceLocation id) {
            return skillId.equals(id);
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.addProperty("skill", skillId.toString());
            return jsonobject;
        }
    }
}
