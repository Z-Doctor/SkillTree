package zdoctor.zskilltree.criterion.advancements.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;
import zdoctor.zskilltree.ModMain;

public class SkillPageUnlockedTrigger extends AbstractCriterionTrigger<SkillPageUnlockedTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(ModMain.MODID, "skill_page_unlocked");

    @Override
    protected Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        return new Instance(entityPredicate);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void triggerListeners(ServerPlayerEntity player) {
        triggerListeners(player, instance -> instance.test(player));
    }

    public static class Instance extends CriterionInstance {
        private static final String template = "{\"player\":{\"pages\":{\"%s\":%s}}}";

        public Instance(EntityPredicate.AndPredicate player) {
            super(ID, player);
        }

        public static Instance with(ResourceLocation skillPageId, boolean hasPage) {
            JsonElement element = new JsonParser().parse(String.format(template, skillPageId, hasPage));
            return new Instance(EntityPredicate.AndPredicate.createAndFromEntityCondition(EntityPredicate.deserialize(element)));
        }

        public boolean test(ServerPlayerEntity player) {
            // TODO Make one for non-player entities?
            LootContext lootcontext = EntityPredicate.getLootContext(player, player);
            return getPlayerCondition().testContext(lootcontext);
        }
    }
}
