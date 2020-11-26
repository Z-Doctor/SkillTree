package zdoctor.zskilltree.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.advancements.criterion.PlayerPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zdoctor.zskilltree.api.SkillTreeApi;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.Deserializable;
import zdoctor.zskilltree.api.interfaces.ICriteriaPredicate;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;

import java.util.HashMap;
import java.util.Map;

// TODO Do something similar for non-player entities
@Mixin(PlayerPredicate.class)
public abstract class MixinPlayerPredicate implements Deserializable {

    public Logger logger = LogManager.getLogger();
    private Map<ResourceLocation, ICriteriaPredicate> criteriaTrackers = new HashMap<>();

    @Inject(method = "deserialize", remap = false, at = @At(value = "RETURN"))
    private static void deserializeCriteria(JsonElement element, CallbackInfoReturnable<PlayerPredicate> cir) {
        if (cir.getReturnValue() != PlayerPredicate.ANY)
            ((Deserializable) cir.getReturnValue()).deserialize(element, cir.getReturnValue());
    }

    private static ICriteriaPredicate deserializeCriterion(JsonElement element) {
        if (element.isJsonPrimitive()) {
            boolean flag = element.getAsBoolean();
            return new ICriteriaPredicate.CompletedCriteriaPredicate(flag);
        } else {
            Object2BooleanMap<String> object2booleanmap = new Object2BooleanOpenHashMap<>();
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "criterion data");
            jsonobject.entrySet().forEach((criterionEntry) -> {
                boolean flag = JSONUtils.getBoolean(criterionEntry.getValue(), "criterion test");
                object2booleanmap.put(criterionEntry.getKey(), flag);
            });
            return new ICriteriaPredicate.CriteriaPredicate(object2booleanmap);
        }
    }

    @Inject(method = "serialize", remap = false, at = @At(value = "RETURN"))
    public void serializeCriteria(CallbackInfoReturnable<JsonElement> cir) {
        JsonObject jsonObject = cir.getReturnValue().getAsJsonObject();
        if (!criteriaTrackers.isEmpty()) {
            JsonObject pageObject = new JsonObject();
            criteriaTrackers.forEach((trackerId, criteriaPredicate) -> {
                pageObject.add(trackerId.toString(), criteriaPredicate.serialize());
            });
            jsonObject.add("pages", pageObject);
        }
    }

    @Inject(method = "test", remap = false, at = @At("RETURN"))
    public void extraTest(Entity player, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue())
            return;

        ISkillTreeTracker tracker = SkillTreeApi.getTracker(player);
        if (tracker == null) {
            cir.setReturnValue(false);
        } else if (!criteriaTrackers.isEmpty()) {
            for (Map.Entry<ResourceLocation, ICriteriaPredicate> entry : criteriaTrackers.entrySet()) {
                CriterionTracker trackable = tracker.getTracker(entry.getKey());
                if (trackable == null || entry.getValue().test(tracker.getProgress(trackable))) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @Override
    public void deserialize(JsonElement element, Object instance) {
        JsonObject player = JSONUtils.getJsonObject(element, "player");
        JsonObject pages = JSONUtils.getJsonObject(player, "pages", new JsonObject());
        for (Map.Entry<String, JsonElement> entry : pages.entrySet()) {
            ResourceLocation pageLocation = new ResourceLocation(entry.getKey());
            ICriteriaPredicate criteriaPredicate = deserializeCriterion(entry.getValue());
            criteriaTrackers.put(pageLocation, criteriaPredicate);
        }
    }


}
