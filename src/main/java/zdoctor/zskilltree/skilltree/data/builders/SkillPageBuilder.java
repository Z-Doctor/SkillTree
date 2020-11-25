package zdoctor.zskilltree.skilltree.data.builders;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;
import zdoctor.zskilltree.skilltree.skillpages.SkillPageDisplayInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SkillPageBuilder {
    private int index = -1;
    private SkillPageDisplayInfo display;
    private Map<String, Criterion> criteria = new HashMap<>();
    private IRequirementsStrategy requirementsStrategy = IRequirementsStrategy.AND;

    protected SkillPageBuilder(int index, @Nullable SkillPageDisplayInfo displayIn, Map<String, Criterion> criteriaIn, IRequirementsStrategy requirementsStrategyIn) {
        this.index = index;
        this.display = displayIn;
        this.criteria = criteriaIn;
        this.requirementsStrategy = requirementsStrategyIn;
    }

    protected SkillPageBuilder() {
    }

    public static SkillPageBuilder builder() {
        return new SkillPageBuilder();
    }

    public SkillPageBuilder withCriterion(String key, ICriterionInstance criterionIn) {
        return this.withCriterion(key, new Criterion(criterionIn));
    }

    public SkillPageBuilder withCriterion(String key, Criterion criterionIn) {
        if (this.criteria.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate criterion " + key);
        } else {
            this.criteria.put(key, criterionIn);
            return this;
        }
    }

    public SkillPageBuilder withRequirementsStrategy(IRequirementsStrategy strategy) {
        this.requirementsStrategy = strategy;
        return this;
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

    public SkillPageBuilder copy() {
        return new SkillPageBuilder(index, display, criteria, requirementsStrategy);
    }

    public SkillPage build(ResourceLocation id) {
        String[][] requirements = requirementsStrategy.createRequirements(criteria.keySet());
        return new SkillPage(index, id, display, criteria, requirements);
    }

    public SkillPage register(Consumer<SkillPage> consumer, String id) {
        SkillPage page = this.build(new ResourceLocation(ModMain.MODID, id));
        consumer.accept(page);
        return page;
    }
}
