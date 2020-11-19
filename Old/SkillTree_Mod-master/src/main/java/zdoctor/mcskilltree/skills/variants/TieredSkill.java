package zdoctor.mcskilltree.skills.variants;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zdoctor.mcskilltree.api.ISkillTier;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skills.SkillDisplayInfo;

public abstract class TieredSkill extends Skill implements ISkillTier {
    public TieredSkill(String name, Item icon) {
        super(name, icon);
    }

    public TieredSkill(String name, ItemStack icon) {
        super(name, icon);
    }

    public TieredSkill(String name, SkillDisplayInfo displayInfo) {
        super(name, displayInfo);
    }
}
