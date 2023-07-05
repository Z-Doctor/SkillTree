package com.zdoctorsmods.skilltreemod.skills.loot.critereon;

import com.google.gson.JsonObject;
import com.zdoctorsmods.skilltreemod.SkillTree;
import com.zdoctorsmods.skilltreemod.skills.Skill;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityPredicate.Composite;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SkillPurchasedTrigger extends SimpleCriterionTrigger<SkillPurchasedTrigger.TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation(SkillTree.MODID, "skill_purchased");

    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, Composite pPlayer, DeserializationContext pContext) {
        SkillPredicate predicate = SkillPredicate.fromJson(pJson.get("skill"));
        return new TriggerInstance(pPlayer, predicate);
    }

    public void trigger(ServerPlayer player, Skill skill) {
        this.trigger(player, instance -> {
            return instance.matches(skill);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final SkillPredicate skill;

        public TriggerInstance(EntityPredicate.Composite pPlayer, SkillPredicate skill) {
            super(SkillPurchasedTrigger.ID, pPlayer);
            this.skill = skill;
        }

        public boolean matches(Skill skill) {
            return this.skill.matches(skill);
        }

        public JsonObject serializeToJson(SerializationContext pConditions) {
            JsonObject jsonobject = super.serializeToJson(pConditions);
            jsonobject.add("skill", this.skill.serializeToJson());
            return jsonobject;
        }
    }

}
