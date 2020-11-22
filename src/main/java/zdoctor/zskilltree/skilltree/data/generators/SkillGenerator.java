package zdoctor.zskilltree.skilltree.data.generators;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import zdoctor.zskilltree.skilltree.skill.Skill;

import java.util.function.Consumer;

public class SkillGenerator implements Consumer<Consumer<Skill>> {
    @Override
    public void accept(Consumer<Skill> consumer) {

        Skill.Builder.builder().withDisplay(new ItemStack(Items.DIAMOND_SWORD), "attack_skill")
                .onPage(SkillPageGenerator.miscPage).register(consumer, "attack_skill");

        Skill.Builder.builder().withDisplay(new ItemStack(Items.SHIELD), "defense_skill")
                .onPage(SkillPageGenerator.miscPage).register(consumer, "defense_skill");

        Skill.Builder.builder().withDisplay(new ItemStack(Items.SHEARS), "utility_skill")
                .onPage(SkillPageGenerator.miscPage).register(consumer, "utility_skill");
    }
}
