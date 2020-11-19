package zdoctor.mcskilltree.world.storage.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameter;
import zdoctor.mcskilltree.skills.Skill;

public class SkillLootParameters {
    public static final LootParameter<Skill> SKILL = register("skill");

    private static <T> LootParameter<T> register(String id) {
        return new LootParameter<>(new ResourceLocation(id));
    }
}
