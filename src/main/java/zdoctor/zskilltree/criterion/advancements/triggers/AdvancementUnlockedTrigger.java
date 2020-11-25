package zdoctor.zskilltree.criterion.advancements.triggers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.criterion.AbstractSkillTreeCriterionTrigger;
import zdoctor.zskilltree.criterion.advancements.ExtendedCriteriaTriggers;
import zdoctor.zskilltree.skilltree.events.CriterionTrackerEvent;
import zdoctor.zskilltree.skilltree.events.SkillTreeEvent;

@Mod.EventBusSubscriber
public class AdvancementUnlockedTrigger extends AbstractSkillTreeCriterionTrigger<AdvancementUnlockedTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(ModMain.MODID, "advancement_unlocked");

    @SubscribeEvent
    public static void onAdvancementUnlocked(AdvancementEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity)
            ExtendedCriteriaTriggers.Advancement_Unlocked.test((ServerPlayerEntity) event.getPlayer());
    }

    @SubscribeEvent
    public static void onProgressRevoked(CriterionTrackerEvent.ProgressRevokedEvent event) {
        if (event.getOwner() instanceof ServerPlayerEntity) {
            ExtendedCriteriaTriggers.Advancement_Unlocked.test((ServerPlayerEntity) event.getOwner());
        }
    }

    @SubscribeEvent
    public static void onSkillTreeReloaded(SkillTreeEvent.PlayerReloadedEvent event) {
        ExtendedCriteriaTriggers.Advancement_Unlocked.test(event.getPlayer());
    }

    @Override
    protected Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        EntityPredicate.AndPredicate playerCondition = EntityPredicate.AndPredicate.deserializeJSONObject(json, "player", conditionsParser);
//        PlayerPredicate playerPredicate = PlayerPredicate.deserialize(json);
        return new Instance(playerCondition);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void test(ServerPlayerEntity player) {
//        LootContext lootcontext = EntityPredicate.getLootContext(player, player);
//        triggerListeners(player, instance -> instance.getPlayerCondition().testContext(lootcontext));
        triggerListeners(player, instance -> true);
    }

    public static class Instance extends CriterionInstance {
//        private final PlayerPredicate playerPredicate;

        private static final String template = "{\"player\":{\"advancements\":{\"%s\":%s}}}";

        public Instance(EntityPredicate.AndPredicate playerCondition) {
            super(ID, playerCondition);
//            this.playerPredicate = playerPredicate;
        }

        public static Instance with(String advancement, boolean isObtained) {
            JsonElement element = new JsonParser().parse(String.format(template, advancement, isObtained));
            return new Instance(EntityPredicate.AndPredicate.createAndFromEntityCondition(EntityPredicate.deserialize(element)));
        }

    }
}
