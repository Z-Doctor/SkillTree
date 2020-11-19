package zdoctor.mcskilltree.skills.variants;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zdoctor.mcskilltree.api.IEffectSkill;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skills.SkillDisplayInfo;

public abstract class EffectSkill extends Skill implements IEffectSkill {

    public EffectSkill(String name, Item icon) {
        super(name, icon);
    }

    public EffectSkill(String name, ItemStack icon) {
        super(name, icon);
    }

    public EffectSkill(String name, SkillDisplayInfo displayInfo) {
        super(name, displayInfo);
    }
}
