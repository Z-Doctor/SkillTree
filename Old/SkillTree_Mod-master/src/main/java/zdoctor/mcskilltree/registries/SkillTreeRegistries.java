package zdoctor.mcskilltree.registries;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skilltree.SkillTree;

public class SkillTreeRegistries {
    public static final IForgeRegistry<SkillTree> SKILL_TREES = RegistryManager.ACTIVE.getRegistry(SkillTree.class);
    public static final IForgeRegistry<Skill> SKILLS = RegistryManager.ACTIVE.getRegistry(Skill.class);
}
