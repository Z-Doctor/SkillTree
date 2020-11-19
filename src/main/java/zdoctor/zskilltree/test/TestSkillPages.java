package zdoctor.zskilltree.test;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.skillpages.SkillPage;

import java.util.function.Consumer;

public class TestSkillPages implements Consumer<Consumer<SkillPage>> {
    public static SkillPage miscPage;

    @Override
    public void accept(Consumer<SkillPage> consumer) {
        SkillPage.Builder.builder().withDisplay(new ItemStack(Items.WRITABLE_BOOK),
                "player_info", SkillPageAlignment.HORIZONTAL).atIndex(0).
                register(consumer, "player_info");


        miscPage = SkillPage.Builder.builder().withDisplay(new ItemStack(Items.DIAMOND_SWORD), "misc_page").
                register(consumer, "misc_page");
    }
}
