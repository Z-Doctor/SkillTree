package zdoctor.zskilltree.extra;

import net.minecraft.loot.LootParameter;
import net.minecraft.util.ResourceLocation;
import zdoctor.zskilltree.skilltree.skill.Skill;

public class SkillLootParameters {
    public static final LootParameter<Skill> SKILL = register("skill");
    public static final LootParameter<Skill> SKILL_TREE = register("skill_tree");

    private static <T> LootParameter<T> register(String id) {
        return new LootParameter<>(new ResourceLocation(id));
    }
}
