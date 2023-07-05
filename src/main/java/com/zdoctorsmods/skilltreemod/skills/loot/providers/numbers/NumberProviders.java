package com.zdoctorsmods.skilltreemod.skills.loot.providers.numbers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraftforge.registries.RegisterEvent;

import com.zdoctorsmods.skilltreemod.SkillTree;

public class NumberProviders {
    public static final LootNumberProviderType SKILL_POINTS = new LootNumberProviderType(
            new SkillPointValue.Serializer());

    public static void init(RegisterEvent event) {
        event.register(Registries.LOOT_NUMBER_PROVIDER_TYPE, new ResourceLocation(SkillTree.MODID, "skill_points"),
                () -> SKILL_POINTS);
    }
}
