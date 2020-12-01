package zdoctor.zskilltree.skilltree.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.skilltree.criterion.Skill;
import zdoctor.zskilltree.skilltree.criterion.SkillPage;
import zdoctor.zskilltree.skilltree.displays.SkillDisplayInfo;

import java.util.HashMap;

public class SkillBuilder extends CriterionBuilder<SkillBuilder, Skill> {
    private SkillDisplayInfo display;
    private ResourceLocation parentPage;

    private boolean defaultSkill;

    protected SkillBuilder() {
        requirementsStrategy = IRequirementsStrategy.OR;
    }

    protected SkillBuilder(SkillBuilder skillBuilder) {
        this.display = skillBuilder.display.copy();
        this.criteria = new HashMap<>(skillBuilder.criteria);
        this.requirementsStrategy = skillBuilder.requirementsStrategy;
    }

    public static SkillBuilder builder() {
        return new SkillBuilder();
    }

    // TODO Add an attributes option that a skill can add through a json array
    //  each attribute will be a string and the skill will register itself to the attribute listener if one
    //  of that name exists. Can be user created (maybe can also be tied to mc functions? if so then a command will
    //  need to be added)
    public static SkillBuilder deserialize(JsonObject json, ConditionArrayParser conditionParser) {
        SkillBuilder builder = builder();

        if (JSONUtils.hasField(json, "page"))
            builder.onPage(ResourceLocation.tryCreate(JSONUtils.getString(json, "page")));

        if (!JSONUtils.hasField(json, "display"))
            throw new JsonSyntaxException("Skill display cannot be empty");
        builder.withDisplay(new SkillDisplayInfo(JSONUtils.getJsonObject(json, "display")));

        if (json.has("criteria")) {
            builder.criteria = Criterion.deserializeAll(JSONUtils.getJsonObject(json, "criteria"), conditionParser);
            if (!builder.criteria.isEmpty() && json.has("requirements")) {
                JsonArray jsonRequirements = JSONUtils.getJsonArray(json, "requirements", new JsonArray());
                String[][] requirements = new String[jsonRequirements.size()][];

                for (int i = 0; i < jsonRequirements.size(); ++i) {
                    JsonArray requirement = JSONUtils.getJsonArray(jsonRequirements.get(i), "requirements[" + i + "]");
                    requirements[i] = new String[requirement.size()];

                    for (int j = 0; j < requirement.size(); ++j) {
                        requirements[i][j] = JSONUtils.getString(requirement.get(j), "requirements[" + i + "][" + j + "]");
                    }
                }

            } else if (json.has("requirements"))
                throw new JsonSyntaxException("Skill has requirements but defines no criteria: " + json.getAsString());

        }
        return builder;
    }

    public SkillBuilder onPage(SkillPage page) {
        return onPage(page.getRegistryName());
    }

    public SkillBuilder onPage(String page) {
        return onPage(new ResourceLocation(ModMain.MODID, page));
    }

    public SkillBuilder onPage(ResourceLocation page) {
        parentPage = page;
        return this;
    }

    public ResourceLocation getPageId() {
        return parentPage;
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

    public SkillBuilder markDefault() {
        defaultSkill = true;
        return this;
    }

    @Override
    public SkillBuilder copy() {
        return new SkillBuilder(this);
    }

    @Override
    public Skill build(ResourceLocation id) {
        if (parentPage == null)
            throw new JsonSyntaxException("Parent page is null");
        if (criteria.isEmpty() && !defaultSkill)
            withTrigger("unlocked", new ImpossibleTrigger.Instance());
        String[][] requirements = requirementsStrategy.createRequirements(criteria.keySet());
        return new Skill(id, display, criteria, requirements).setVisibilityContext(entityAndPredicate).setParentPage(parentPage);
    }
}