package com.zdoctorsmods.skilltreemod.skills.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * A LootItemCondition that checks the skills of an entity
 * And for players the amount of skill points
 */
public class SkillCheck implements LootItemCondition {
    @Override
    public boolean test(LootContext t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'test'");
    }

    @Override
    public LootItemConditionType getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<SkillCheck> {

        @Override
        public void serialize(JsonObject pJson, SkillCheck pValue, JsonSerializationContext pSerializationContext) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'serialize'");
        }

        @Override
        public SkillCheck deserialize(JsonObject pJson, JsonDeserializationContext pSerializationContext) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'deserialize'");
        }
    }

}
