package com.zdoctorsmods.skilltreemod.skills.loot.predicates;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class SkillLootConditions {

      // public static final LootItemConditionType SKILL_CHECK =
      // register("skilltree:skill_check",
      // new SkillCheck.Serializer());

      private static LootItemConditionType register(String pRegistryName,
                  Serializer<? extends LootItemCondition> pSerializer) {
            return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation(pRegistryName),
                        new LootItemConditionType(pSerializer));
      }

      public static void init() {

      }

}
