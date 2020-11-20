package zdoctor.zskilltree.data.generators;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.data.builders.SkillPageBuilder;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import java.util.function.Consumer;

public class SkillPageGenerator implements Consumer<Consumer<SkillPage>> {
    public static SkillPage miscPage;

    @Override
    public void accept(Consumer<SkillPage> consumer) {
        SkillPageBuilder.builder().atIndex(0)
                .withDisplay(new ItemStack(Items.WRITABLE_BOOK), "player_info", SkillPageAlignment.HORIZONTAL).
                register(consumer, "player_info");


        miscPage = SkillPageBuilder.builder().withDisplay(new ItemStack(Items.DIAMOND_SWORD), "misc_page").
                register(consumer, "misc_page");
    }

}
