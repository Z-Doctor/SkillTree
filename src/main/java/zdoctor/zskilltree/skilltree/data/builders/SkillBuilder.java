package zdoctor.zskilltree.skilltree.data.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.PlayerPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.interfaces.ICriteriaPredicate;
import zdoctor.zskilltree.skilltree.loot.conditions.HasSkillPage;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skill.SkillDisplayInfo;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;
import zdoctor.zskilltree.skilltree.triggers.SkillUnlockedTrigger;

import java.util.*;
import java.util.function.Consumer;

public class SkillBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<ResourceLocation, ICriteriaPredicate> conditions = new LinkedHashMap<>();
    private PlayerPredicate playerPredicate = PlayerPredicate.ANY;
    private SkillDisplayInfo display;
    private Map<String, Criterion> criteria = new HashMap<>();
    private String[][] requirements;
    private IRequirementsStrategy requirementsStrategy = IRequirementsStrategy.AND;

    private ResourceLocation skillPage;

    protected SkillBuilder() {
    }

    public SkillBuilder(SkillBuilder skillBuilder) {
        this.display = skillBuilder.display;
        this.criteria = new HashMap<>(skillBuilder.criteria);
        String[][] temp = new String[requirements.length][];
        for (int i = 0; i < temp.length; i++)
            temp[i] = Arrays.copyOf(requirements[i], requirements.length);
        this.requirements = temp;
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
        builder.withDisplay(SkillDisplayInfo.deserialize(JSONUtils.getJsonObject(json, "display")));

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
                throw new JsonSyntaxException("Skill has requirements but defines no criteria");

        }
        return builder;
    }

    public static JsonElement serialize(Skill skill) {
        JsonObject jsonobject = new JsonObject();

        if (skill.getParentPage() != null)
            jsonobject.addProperty("page", skill.getParentPage().toString());

        jsonobject.add("display", skill.getDisplayInfo().serialize());

        JsonObject criteriaObject = new JsonObject();
        for (Map.Entry<String, Criterion> entry : skill.getCriteria().entrySet())
            criteriaObject.add(entry.getKey(), entry.getValue().serialize());
        jsonobject.add("criteria", criteriaObject);

        JsonArray requirementArray = new JsonArray();
        for (String[] requirements : skill.getRequirements()) {
            JsonArray array = new JsonArray();
            for (String requirement : requirements)
                array.add(requirement);

            requirementArray.add(array);
        }
        jsonobject.add("requirements", requirementArray);
        return jsonobject;
    }

    public SkillBuilder setPlayerPredicate(PlayerPredicate playerPredicate) {
        this.playerPredicate = playerPredicate;
        return this;
    }

    public SkillBuilder onPage(SkillPage page) {
        return onPage(page.getRegistryName());
    }

    public SkillBuilder onPage(String page) {
        return onPage(new ResourceLocation(ModMain.MODID, page));
    }

    public SkillBuilder onPage(ResourceLocation page) {
        if (skillPage != null)
            conditions.remove(skillPage);
        skillPage = page;
        conditions.put(skillPage, new ICriteriaPredicate.CompletedCriteriaPredicate(true));
//        this.criteria.compute("onPage", (key, old) -> new Criterion(SkillUnlockedTrigger.Instance.with(page)));
//        this.criteria.compute("onPage", (key, old) -> new Criterion(SkillPageTrigger.Instance.with(page)));
        return this;
    }

    public ResourceLocation getPageId() {
        return skillPage;
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
        return new SkillBuilder(this);
    }

    public Skill build(ResourceLocation id) {
        if (requirements == null)
            requirements = requirementsStrategy.createRequirements(criteria.keySet());
        if (criteria.size() > 0) {
            if (requirements.length == 0) {
                LOGGER.error("Skill has {} criteria, but 0 requirements. This should not happen at this stage. Correcting using {} strategy.",
                        criteria.size(), requirementsStrategy == IRequirementsStrategy.AND ? "AND" : requirementsStrategy == IRequirementsStrategy.OR ? "OR"
                                : "custom " + requirementsStrategy.toString());
                requirements = requirementsStrategy.createRequirements(criteria.keySet());
            }

            for (String[] requirements : requirements) {
                for (String requirement : requirements) {
                    if (!criteria.containsKey(requirement)) {
                        throw new JsonSyntaxException("Unknown required criterion '" + requirement + "'");
                    }
                }
            }

            for (String key : criteria.keySet()) {
                boolean flag = false;
                for (String[] requirements : requirements) {
                    if (ArrayUtils.contains(requirements, key)) {
                        if (flag)
                            throw new JsonSyntaxException("Duplicate requirement '" + key + "' detected. This isn't supported behaviour, all requirements should be required once");
                        flag = true;
                    }
                }
                if (!flag) {
                    throw new JsonSyntaxException("Criterion '" + key + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
                }

            }
        } else if (requirements.length != 0)
            throw new JsonSyntaxException("Requirements defined but not criteria detected");
        if (skillPage == null)
            throw new JsonSyntaxException("Parent page is null");
        return new Skill(id, display, criteria, requirements).setPage(skillPage);
    }

    public Skill register(Consumer<Skill> consumer, String id) {
        return register(consumer, new ResourceLocation(ModMain.MODID, id));
    }

    public Skill register(Consumer<Skill> consumer, ResourceLocation id) {
        Skill skill = this.buildGenerator(id);
        consumer.accept(skill);
        return skill;
    }

    private Skill buildGenerator(ResourceLocation id) {
        List<ILootCondition> conditionsList = new ArrayList<>();
        // Gets player
        if (playerPredicate != PlayerPredicate.ANY) {
            EntityPredicate player = EntityPredicate.Builder.create().player(playerPredicate).build();
            conditionsList.add(EntityHasProperty.func_237477_a_(LootContext.EntityTarget.THIS, player).build());
        }
        // Gets skill
        if (!conditions.isEmpty()) {
            conditionsList.add(HasSkillPage.builder(LootContext.EntityTarget.THIS, this.conditions).build());
        }
        EntityPredicate.AndPredicate prerequisites = EntityPredicate.AndPredicate.serializePredicate(conditionsList.toArray(new ILootCondition[0]));
        withCriterion("prerequisites", SkillUnlockedTrigger.Instance.of(prerequisites, id));
        return build(id);
    }

}