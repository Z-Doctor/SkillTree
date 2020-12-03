package zdoctor.zskilltree.skilltree.loot.conditions;

import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.common.Mod;

public class AdditionalConditions {
    public static final LootConditionType HAS_SKILL_PAGE = register("has_skill_page", new HasSkillPage.Serializer());
    public static final LootConditionType HAS_SKILL = register("has_skill", new HasSkill.Serializer());

    private static LootConditionType register(String registryName, ILootSerializer<? extends ILootCondition> serializer) {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(registryName), new LootConditionType(serializer));
    }

    public static void init() {
    }

}
