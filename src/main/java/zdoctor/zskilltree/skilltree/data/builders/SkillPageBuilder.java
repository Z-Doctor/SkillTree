package zdoctor.zskilltree.skilltree.data.builders;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.skilltree.loot.conditions.HasSkillPage;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;
import zdoctor.zskilltree.skilltree.skillpages.SkillPageDisplayInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SkillPageBuilder extends Builder<SkillPageBuilder, SkillPage> {
    private int index = -1;
    private SkillPageDisplayInfo display;
    private EntityPredicate.AndPredicate visibility = EntityPredicate.AndPredicate.ANY_AND;

    private boolean unlockable;

    protected SkillPageBuilder() {
    }

    protected SkillPageBuilder(SkillPageBuilder builder) {
        criteria = new HashMap<>(builder.criteria);
        requirementsStrategy = builder.requirementsStrategy;
        index = builder.index;
        display = builder.display.copy();
    }

    protected SkillPageBuilder(int index, @Nullable SkillPageDisplayInfo displayIn, Map<String, Criterion> criteriaIn, IRequirementsStrategy requirementsStrategyIn) {
        this.index = index;
        this.display = displayIn;
        this.criteria = criteriaIn;
        this.requirementsStrategy = requirementsStrategyIn;
    }

    public static SkillPageBuilder builder() {
        return new SkillPageBuilder();
    }

    public void createVisibility() {
//        private final Map<String, ILootCondition> visibility = new LinkedHashMap<>();
    }

    public SkillPageBuilder withDisplay(ItemStack icon, ITextComponent title, ITextComponent description, SkillPageAlignment alignment, ImageAsset background) {
        return this.withDisplay(new SkillPageDisplayInfo(icon, title, description, background, alignment));
    }

    public SkillPageBuilder withDisplay(IItemProvider itemIn, ITextComponent title, ITextComponent description, ImageAsset background, SkillPageAlignment alignment) {
        return this.withDisplay(new SkillPageDisplayInfo(new ItemStack(itemIn.asItem()), title, description, background, alignment));
    }

    public SkillPageBuilder withDisplay(ItemStack icon, String name) {
        return withDisplay(icon, name, SkillPageAlignment.VERTICAL);
    }

    public SkillPageBuilder withDisplay(ItemStack icon, String name, SkillPageAlignment alignment) {
        return withDisplay(new SkillPageDisplayInfo(icon,
                new TranslationTextComponent("skillpage." + name + ".title"),
                new TranslationTextComponent("skillpage." + name + ".description"),
                alignment));
    }

    public SkillPageBuilder withDisplay(SkillPageDisplayInfo displayIn) {
        this.display = displayIn;
        return this;
    }

    public SkillPageBuilder atIndex(int i) {
        index = i;
        return this;
    }

    @Override
    public SkillPageBuilder copy() {
        return new SkillPageBuilder(this);
    }

    @Override
    public SkillPage build(ResourceLocation id) {
        if (unlockable) {
            visibility = LootConditionBuilder.create().withCondition(HasSkillPage.builder(LootContext.EntityTarget.THIS, id).build()).build();
        }

        String[][] requirements = requirementsStrategy.createRequirements(criteria.keySet());
        return new SkillPage(index, id, display, criteria, requirements).setVisibilityContext(visibility);
    }

    public SkillPageBuilder unlockable() {
        unlockable = true;
        return this;
    }
}
