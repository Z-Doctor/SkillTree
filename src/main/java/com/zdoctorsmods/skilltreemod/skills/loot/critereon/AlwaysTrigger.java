package com.zdoctorsmods.skilltreemod.skills.loot.critereon;

import com.google.gson.JsonObject;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;

public class AlwaysTrigger implements CriterionTrigger<AlwaysTrigger.TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("always");

    public ResourceLocation getId() {
        return ID;
    }

    public AlwaysTrigger.TriggerInstance createInstance(JsonObject pObject, DeserializationContext pConditions) {
        return new AlwaysTrigger.TriggerInstance();
    }

    public static class TriggerInstance implements CriterionTriggerInstance {
        public ResourceLocation getCriterion() {
            return AlwaysTrigger.ID;
        }

        public JsonObject serializeToJson(SerializationContext pConditions) {
            return new JsonObject();
        }
    }

    @Override
    public void addPlayerListener(PlayerAdvancements pPlayerAdvancements, Listener<TriggerInstance> pListener) {
        pListener.run(pPlayerAdvancements);
    }

    @Override
    public void removePlayerListener(PlayerAdvancements pPlayerAdvancements, Listener<TriggerInstance> pListener) {
    }

    @Override
    public void removePlayerListeners(PlayerAdvancements pPlayerAdvancements) {
    }
}