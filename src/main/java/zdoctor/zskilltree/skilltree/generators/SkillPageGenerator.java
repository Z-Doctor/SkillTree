package zdoctor.zskilltree.skilltree.generators;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import zdoctor.zskilltree.api.ImageAssets;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.skilltree.builders.SkillPageBuilder;
import zdoctor.zskilltree.skilltree.criterion.triggers.AdvancementUnlockedTrigger;
import zdoctor.zskilltree.skilltree.criterion.SkillPage;

import java.util.function.Consumer;

public class SkillPageGenerator implements Consumer<Consumer<SkillPage>> {
    public static SkillPage miscPage;

    @Override
    public void accept(Consumer<SkillPage> consumer) {
        // TODO Make it so that you can add an alternative condition of them already having it unlocked
        SkillPageBuilder.builder().atIndex(0)
                .withDisplay(new ItemStack(Items.WRITABLE_BOOK), "player_info", SkillPageAlignment.HORIZONTAL)
                .register(consumer, "player_info");


        miscPage = SkillPageBuilder.builder().withDisplay(new ItemStack(Items.DIAMOND_SWORD), "misc_page")
                .unlockable().register(consumer, "misc_page");

        SkillPage.builder().withDisplay(Items.ENCHANTING_TABLE, new StringTextComponent("Enchanter"),
                new StringTextComponent("Become an Enchanter"), ImageAssets.NETHERRACK_TILE, SkillPageAlignment.VERTICAL)
//                .makeTrigger("has_advancement", predicate -> AdvancementUnlockedTrigger.with("story/enchant_item"))
                .makeTrigger("has_advancement", predicate -> AdvancementUnlockedTrigger.with(predicate, new ResourceLocation("story/enchant_item")))
//                .buildTrigger(PlayerPredicateBuilder.Builder.create().withBounds(MinMaxBounds.IntBound.atLeast(20)).buildEntity())
                .make().unlockable().register(consumer, "enchanting_branch");
    }
}
