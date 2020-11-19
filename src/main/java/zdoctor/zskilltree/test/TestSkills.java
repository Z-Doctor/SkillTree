package zdoctor.zskilltree.test;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import zdoctor.zskilltree.skill.Skill;

import java.util.function.Consumer;

public class TestSkills implements Consumer<Consumer<Skill>> {
    @Override
    public void accept(Consumer<Skill> consumer) {

        Skill.Builder.builder().withDisplay(new ItemStack(Items.DIAMOND_SWORD), "attack_skill")
                .onPage(TestSkillPages.miscPage).register(consumer, "attack_skill");

        Skill.Builder.builder().withDisplay(new ItemStack(Items.SHIELD), "defense_skill")
                .onPage(TestSkillPages.miscPage).register(consumer, "defense_skill");

        Skill.Builder.builder().withDisplay(new ItemStack(Items.SHEARS), "utility_skill")
                .onPage(TestSkillPages.miscPage).register(consumer, "utility_skill");
    }
}
