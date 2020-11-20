package zdoctor.zskilltree.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.PlayerPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.advancements.ExtendedCriteriaTriggers;
import zdoctor.zskilltree.criterion.AbstractSkillTreeCriterionTrigger;
import zdoctor.zskilltree.skilltree.events.SkillPageEvent;
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
    public static void onSkillPageRevoked(SkillPageEvent.SkillPageRevokedEvent event) {
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
        PlayerPredicate playerPredicate = PlayerPredicate.deserialize(json);
        return new Instance(playerCondition, playerPredicate);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void test(ServerPlayerEntity player) {
        triggerListeners(player, instance -> instance.playerPredicate.test(player));
    }

    public static class Instance extends CriterionInstance {
        private final PlayerPredicate playerPredicate;

        public Instance(EntityPredicate.AndPredicate playerCondition, PlayerPredicate playerPredicate) {
            super(ID, playerCondition);
            this.playerPredicate = playerPredicate;
        }
    }
}
