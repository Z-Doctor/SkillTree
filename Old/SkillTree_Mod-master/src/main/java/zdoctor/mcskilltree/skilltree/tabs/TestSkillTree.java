package zdoctor.mcskilltree.skilltree.tabs;

import com.google.common.collect.Maps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.registries.ObjectHolder;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skills.variants.EffectSkill;
import zdoctor.mcskilltree.skilltree.SkillTree;

import java.util.Map;

@ObjectHolder(McSkillTree.MODID)
public class TestSkillTree extends SkillTree {
    public static AttributeModifier MOVEMENT_MODIFIER = new AttributeModifier("MovementSkill", 0.2, AttributeModifier.Operation.MULTIPLY_BASE).setSaved(false);

    @ObjectHolder("test")
    public static final TestSkillTree TEST_TREE = null;

    public final Skill Speed_Skill;

    public TestSkillTree() {
        super("test", new ItemStack(Items.DIAMOND_SWORD));
        Speed_Skill = new EffectSkill("speed_skill", Items.FEATHER) {
            final Map<LivingEntity, EffectInstance> effectMap = Maps.newHashMap();

            @Override
            public void applySkill(LivingEntity entity) {
                IAttributeInstance movement = entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                if (!movement.hasModifier(MOVEMENT_MODIFIER))
                    movement.applyModifier(MOVEMENT_MODIFIER);
            }

            @Override
            public void removeSkill(LivingEntity entity) {
                IAttributeInstance movement = entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                if (movement.hasModifier(MOVEMENT_MODIFIER))
                    movement.removeModifier(MOVEMENT_MODIFIER);
            }
        };

        addSkills(Speed_Skill);
    }

}
