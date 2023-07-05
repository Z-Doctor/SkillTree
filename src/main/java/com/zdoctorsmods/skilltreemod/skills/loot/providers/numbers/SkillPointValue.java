package com.zdoctorsmods.skilltreemod.skills.loot.providers.numbers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.zdoctorsmods.skilltreemod.SkillTree;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SkillPointValue implements NumberProvider {
    final LootContext.EntityTarget target;

    SkillPointValue(LootContext.EntityTarget pTarget) {
        this.target = pTarget;
    }

    @Override
    public float getFloat(LootContext pLootContext) {
        Entity entity = pLootContext.getParamOrNull(this.target.getParam());
        if (entity == null)
            return -1;
        if (entity instanceof ServerPlayer player) {
            return SkillTree.getSkillPointBalance(player);
        } else
            return -1;
    }

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.SKILL_POINTS;
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<SkillPointValue> {

        @Override
        public void serialize(JsonObject pJson, SkillPointValue pValue, JsonSerializationContext context) {
            pJson.add("target", context.serialize(pValue.target));
        }

        @Override
        public SkillPointValue deserialize(JsonObject pJson, JsonDeserializationContext context) {
            LootContext.EntityTarget target = GsonHelper.getAsObject(pJson, "target", context,
                    LootContext.EntityTarget.class);
            return new SkillPointValue(target);
        }
    }

}
