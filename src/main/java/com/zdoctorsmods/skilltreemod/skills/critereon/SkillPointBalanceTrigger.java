package com.zdoctorsmods.skilltreemod.skills.critereon;

import java.util.function.Predicate;

import com.google.gson.JsonObject;
import com.zdoctorsmods.skilltreemod.SkillTreeMod;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityPredicate.Composite;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

// TODO finish setting up and add an advancement
public class SkillPointBalanceTrigger extends SimpleCriterionTrigger<SkillPointBalanceTrigger.TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("sp_balance");

    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, Composite pPlayer, DeserializationContext pContext) {
        int amount = pJson.get("amount").getAsInt();
        return new TriggerInstance(pPlayer, amount);
    }

    @Override
    protected void trigger(ServerPlayer pPlayer, Predicate<TriggerInstance> pTestTrigger) {
        // TODO Auto-generated method stub
        super.trigger(pPlayer, pTestTrigger);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final int amount;

        public TriggerInstance(EntityPredicate.Composite pPlayer, int amount) {
            super(SkillPointBalanceTrigger.ID, pPlayer);
            this.amount = amount;
        }

        public static TriggerInstance skillPointBalance(int amount) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, amount);
        }

        public boolean hasEnough(ServerPlayer player) {
            return SkillTreeMod.getSkillPointBalance(player) >= amount;
        }

        public JsonObject serializeToJson(SerializationContext pConditions) {
            JsonObject jsonobject = super.serializeToJson(pConditions);
            jsonobject.addProperty("amount", amount);
            return jsonobject;
        }
    }

}
