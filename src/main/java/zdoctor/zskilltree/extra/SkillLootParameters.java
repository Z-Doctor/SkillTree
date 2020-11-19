package zdoctor.zskilltree.extra;

import net.minecraft.loot.LootParameter;
import net.minecraft.util.ResourceLocation;
import zdoctor.zskilltree.skill.Skill;

public class SkillLootParameters {
    public static final LootParameter<Skill> SKILL = register("skill");

    private static <T> LootParameter<T> register(String id) {
        return new LootParameter<>(new ResourceLocation(id));
    }
}
