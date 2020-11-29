package zdoctor.zskilltree.skilltree.data.criterion.triggers;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.skilltree.data.criterion.ExtendedCriteriaTriggers;
import zdoctor.zskilltree.skilltree.events.CriterionTrackerEvent;
import zdoctor.zskilltree.skilltree.events.SkillTreeEvent;

@Mod.EventBusSubscriber
public class AdvancementUnlockedTrigger extends AbstractSkillTreeCriterionTrigger<AdvancementUnlockedTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(ModMain.MODID, "advancement_unlocked");

    @SubscribeEvent
    public static void onAdvancementUnlocked(AdvancementEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity)
            ExtendedCriteriaTriggers.ADVANCEMENT_UNLOCKED.triggerListeners((ServerPlayerEntity) event.getPlayer(), event.getAdvancement().getId());
    }

    @SubscribeEvent
    public static void onProgressRevoked(CriterionTrackerEvent.ProgressRevokedEvent event) {
        if (event.getOwner() instanceof ServerPlayerEntity) {
            ExtendedCriteriaTriggers.ADVANCEMENT_UNLOCKED.triggerListeners((ServerPlayerEntity) event.getOwner(), Instance.ANY);
        }
    }

    @SubscribeEvent
    public static void onSkillTreeReloaded(SkillTreeEvent.PlayerReloadedEvent event) {
        ExtendedCriteriaTriggers.ADVANCEMENT_UNLOCKED.triggerListeners(event.getPlayer(), Instance.ANY);
    }

    public static Instance with(String advancement) {
        return with(new ResourceLocation(advancement));
    }

    public static Instance with(ResourceLocation advancement) {
        return new Instance(EntityPredicate.AndPredicate.ANY_AND, advancement);
    }

    public static Instance with(EntityPredicate.AndPredicate predicate, ResourceLocation advancementId) {
        return new Instance(predicate, advancementId);
    }

    public void triggerListeners(ServerPlayerEntity player, ResourceLocation advancementId) {
        triggerListeners(player, instance -> instance.test(advancementId));
    }

    @Override
    protected Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        return new Instance(entityPredicate, ResourceLocation.tryCreate(JSONUtils.getString(json, "advancement")));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static class Instance extends CriterionInstance {
        public static final ResourceLocation ANY = new ResourceLocation("any");
        private final ResourceLocation advancementId;

        public Instance(EntityPredicate.AndPredicate playerCondition, ResourceLocation advancementId) {
            super(ID, playerCondition);
            this.advancementId = advancementId;
        }

        public boolean test(ResourceLocation advancementId) {
            return advancementId == ANY || this.advancementId.equals(advancementId);
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonObject = super.serialize(conditions);
            jsonObject.addProperty("advancement", advancementId.toString());
            return jsonObject;
        }
    }
}
