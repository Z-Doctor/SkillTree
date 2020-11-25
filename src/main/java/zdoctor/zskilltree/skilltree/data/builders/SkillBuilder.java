package zdoctor.zskilltree.skilltree.data.builders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skill.SkillDisplayInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class SkillBuilder {
    Set<ResourceLocation> parents = new HashSet<>();
    Set<ResourceLocation> child = new HashSet<>();

    private SkillDisplayInfo display;
    private Map<String, Criterion> criteria = new HashMap<>();
    private IRequirementsStrategy requirementsStrategy = IRequirementsStrategy.AND;

    protected SkillBuilder(SkillDisplayInfo displayIn, Map<String, Criterion> criteriaIn, IRequirementsStrategy requirementsStrategyIn) {
        this.display = displayIn;
        this.criteria = criteriaIn;
        this.requirementsStrategy = requirementsStrategyIn;
    }

    protected SkillBuilder() {
    }

    public static SkillBuilder builder() {
        return new SkillBuilder();
    }

    public SkillBuilder withCriterion(String key, ICriterionInstance criterionIn) {
        return this.withCriterion(key, new Criterion(criterionIn));
    }

    public SkillBuilder withCriterion(String key, Criterion criterionIn) {
        if (this.criteria.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate criterion " + key);
        } else {
            this.criteria.put(key, criterionIn);
            return this;
        }
    }

    public SkillBuilder withRequirementsStrategy(IRequirementsStrategy strategy) {
        this.requirementsStrategy = strategy;
        return this;
    }

    public SkillBuilder withDisplay(ItemStack icon, ITextComponent title, ITextComponent description, ImageAsset background) {
        return this.withDisplay(new SkillDisplayInfo(icon, title, description, background));
    }

    public SkillBuilder withDisplay(IItemProvider itemIn, ITextComponent title, ITextComponent description, ImageAsset background) {
        return this.withDisplay(new SkillDisplayInfo(new ItemStack(itemIn.asItem()), title, description, background));
    }

    public SkillBuilder withDisplay(ItemStack icon, String name) {
        return withDisplay(new SkillDisplayInfo(icon,
                new TranslationTextComponent("skillpage." + name + ".title"),
                new TranslationTextComponent("skillpage." + name + ".description")));
    }

    public SkillBuilder withDisplay(SkillDisplayInfo displayIn) {
        this.display = displayIn;
        return this;
    }

    public SkillBuilder copy() {
        return new SkillBuilder(display, criteria, requirementsStrategy);
    }

    public Skill build(ResourceLocation id) {
        String[][] requirements = requirementsStrategy.createRequirements(criteria.keySet());
        return new Skill(id, display, criteria, requirements);
    }

    public JsonElement serialize() {
        JsonObject jsonobject = new JsonObject();
//        if (pageId != null)
//            jsonobject.addProperty("page", pageId.toString());
//        if (this.display == null)
//            throw new NullPointerException("Tried to serialize Skill Builder with no display");
//        jsonobject.add("display", this.display.serialize());
        return jsonobject;
    }

    public Skill register(Consumer<Skill> consumer, String id) {
        Skill skill = this.build(new ResourceLocation(ModMain.MODID, id));
        consumer.accept(skill);
        return skill;
    }
}
