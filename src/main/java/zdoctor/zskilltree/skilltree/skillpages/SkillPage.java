package zdoctor.zskilltree.skilltree.skillpages;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.annotations.ClassNameMapper;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.data.builders.SkillPageBuilder;
import zdoctor.zskilltree.extra.ImageAsset;
import zdoctor.zskilltree.skilltree.skill.Skill;

import java.util.*;
import java.util.function.Consumer;

@ClassNameMapper(key = ModMain.MODID + ":skill_page")
public class SkillPage implements CriterionTracker, Comparable<SkillPage> {
    public static final SkillPage NONE = new SkillPage();
    private static final SkillPageDisplayInfo MISSING = new SkillPageDisplayInfo(ItemStack.EMPTY,
            new TranslationTextComponent("skillpage.missing.title"),
            new TranslationTextComponent("skillpage.missing.description")
    ).setHidden();
    private final Map<ResourceLocation, Skill> rootSkills = new HashMap<>();

    private int index;
    private ResourceLocation id;
    private SkillPageDisplayInfo displayInfo;
    private Map<String, Criterion> criteria;
    private String[][] requirements;

    private SkillPage() {
        id = new ResourceLocation(ModMain.MODID, "empty");
        displayInfo = MISSING;
        criteria = ImmutableMap.<String, Criterion>builder().build();
        requirements = new String[0][];
    }

    private SkillPage(SkillPage skillPage) {
        index = skillPage.getIndex();
        id = skillPage.getId();
        displayInfo = skillPage.getDisplayInfo();
        criteria = skillPage.getCriteria();
        requirements = skillPage.getRequirements();
    }

    public SkillPage(PacketBuffer buf) {
        id = buf.readResourceLocation();
        index = buf.readVarInt();
        if (buf.readBoolean())
            displayInfo = SkillPageDisplayInfo.read(buf);

        criteria = Criterion.criteriaFromNetwork(buf);

        requirements = new String[buf.readVarInt()][];
        for (int i = 0; i < requirements.length; ++i) {
            requirements[i] = new String[buf.readVarInt()];
            for (int j = 0; j < requirements[i].length; ++j) {
                requirements[i][j] = buf.readString();
            }
        }

        for (int i = buf.readVarInt(); i > 0; i--) {
            ResourceLocation skillId = buf.readResourceLocation();
//            Skill skill = ModMain.getInstance().getSkillManager().getSkill(skillId);
            rootSkills.put(skillId, null);
        }
    }

    public SkillPage(int index, ResourceLocation id, SkillPageDisplayInfo displayInfo, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
        this.id = id;
        this.displayInfo = displayInfo == null ? MISSING : displayInfo;
        this.index = index;

        this.criteria = ImmutableMap.copyOf(criteriaIn);
        if (requirementsIn == null)
            if (this.criteria.isEmpty())
                this.requirements = new String[0][];
            else
                this.requirements = IRequirementsStrategy.AND.createRequirements(this.criteria.keySet());
    }

    public static int compare(SkillPage in, SkillPage to) {
        // Null values will be treated like they are bigger to be pushed down the list
        if (in == null || to == null) {
            if (in != null)
                return -1;
            else if (to != null)
                return 1;
            return 0;
        }
        return Integer.compareUnsigned(in.getIndex(), to.getIndex());
    }

    public static SkillPage deserialize(ResourceLocation id, JsonObject json, ConditionArrayParser conditionParser) {
        int index = -1;
        if (JSONUtils.hasField(json, "index"))
            index = JSONUtils.getInt(json, "index");
        SkillPageDisplayInfo displayInfo = null;
        if (JSONUtils.hasField(json, "display")) {
            displayInfo = SkillPageDisplayInfo.deserialize(JSONUtils.getJsonObject(json, "display"));
        }

        Map<String, Criterion> criterion = new HashMap<>();
        String[][] requirements = null;

        if (json.has("criteria")) {
            criterion = Criterion.deserializeAll(JSONUtils.getJsonObject(json, "criteria"), conditionParser);
            if (!criterion.isEmpty()) {
                JsonArray jsonRequirements = JSONUtils.getJsonArray(json, "requirements", new JsonArray());
                requirements = new String[jsonRequirements.size()][];

                for (int i = 0; i < jsonRequirements.size(); ++i) {
                    JsonArray requirement = JSONUtils.getJsonArray(jsonRequirements.get(i), "requirements[" + i + "]");
                    requirements[i] = new String[requirement.size()];

                    for (int j = 0; j < requirement.size(); ++j) {
                        requirements[i][j] = JSONUtils.getString(requirement.get(j), "requirements[" + i + "][" + j + "]");
                    }
                }

                if (requirements.length == 0) {
                    requirements = new String[criterion.size()][];
                    int k = 0;

                    for (String s2 : criterion.keySet()) {
                        requirements[k++] = new String[]{s2};
                    }
                }

                for (String[] requirement : requirements) {
                    if (requirement.length == 0 && criterion.isEmpty()) {
                        throw new JsonSyntaxException("Requirement entry cannot be empty");
                    }

                    for (String s : requirement) {
                        if (!criterion.containsKey(s)) {
                            throw new JsonSyntaxException("Unknown required criterion '" + s + "'");
                        }
                    }
                }

                for (String s1 : criterion.keySet()) {
                    boolean flag = false;

                    for (String[] astring2 : requirements) {
                        if (ArrayUtils.contains(astring2, s1)) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        throw new JsonSyntaxException("Criterion '" + s1 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
                    }
                }
            }
        }
        return new SkillPage(index, id, displayInfo, criterion, requirements);
    }

    public void register(Consumer<SkillPage> consumer) {
        consumer.accept(this);
    }

    public boolean hasRootSkill(Skill skill) {
        return rootSkills.containsKey(skill.getId());
    }

    @Override
    public Map<String, Criterion> getCriteria() {
        return criteria;
    }

    @Override
    public String[][] getRequirements() {
        return requirements;
    }

    @Override
    public void writeTo(PacketBuffer buf) {
        buf.writeResourceLocation(id);
        buf.writeVarInt(index);
        if (displayInfo == null)
            buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            displayInfo.write(buf);
        }
        Criterion.serializeToNetwork(criteria, buf);
        int j = requirements == null ? 0 : requirements.length;
        buf.writeVarInt(j);
        if (j > 0)
            for (String[] requirements : requirements) {
                buf.writeVarInt(requirements.length);
                for (String requirement : requirements) {
                    buf.writeString(requirement);
                }
            }

        buf.writeVarInt(rootSkills.size());
        for (ResourceLocation id : rootSkills.keySet())
            buf.writeResourceLocation(id);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public SkillPageDisplayInfo getDisplayInfo() {
        return displayInfo;
    }

    @OnlyIn(Dist.CLIENT)
    public int getIndex() {
        return this.index;
    }

    @OnlyIn(Dist.CLIENT)
    public void setIndex(int index) {
        this.index = index;
    }

    @OnlyIn(Dist.CLIENT)
    public ITextComponent getPageName() {
        return getDisplayInfo().getPageName();
    }

    @OnlyIn(Dist.CLIENT)
    public SkillPageAlignment getAlignment() {
        return displayInfo.getAlignment();
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getIcon() {
        return displayInfo.getIcon();
    }

    @OnlyIn(Dist.CLIENT)
    public ImageAsset getBackgroundImage() {
        return displayInfo.getBackground();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean drawInForegroundOfTab() {
        return displayInfo.drawTitle();
    }

    @OnlyIn(Dist.CLIENT)
    public int getLabelColor() {
        return 4210752;
    }

    public JsonObject serialize() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("index", index);

        if (getDisplayInfo() != null)
            jsonobject.add("display", getDisplayInfo().serialize());

        JsonObject criteriaObject = new JsonObject();
        for (Map.Entry<String, Criterion> entry : getCriteria().entrySet())
            criteriaObject.add(entry.getKey(), entry.getValue().serialize());
        jsonobject.add("criteria", criteriaObject);

        JsonArray requirementArray = new JsonArray();
        for (String[] requirements : getRequirements()) {
            JsonArray array = new JsonArray();
            for (String requirement : requirements)
                array.add(requirement);

            requirementArray.add(array);
        }
        jsonobject.add("requirements", requirementArray);

        return jsonobject;
    }

    public SkillPage copy() {
        return new SkillPage(this);
    }

    @Override
    public String toString() {
        return "SkillPage{" +
                "index=" + index +
                ", id=" + id +
                ", displayInfo=" + displayInfo +
                '}';
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (!(obj instanceof SkillPage))
            return false;
        else {
            return getId().equals(((SkillPage) obj).getId());
        }
    }

    public void addSkill(Skill skill) {
        rootSkills.put(skill.getId(), skill);
    }

    public Collection<Skill> getSkills() {
        return rootSkills.values();
    }

    // TODO Add rarity to display info
    @OnlyIn(Dist.CLIENT)
    public List<ITextComponent> getTooltip(ClientPlayerEntity player, ITooltipFlag.TooltipFlags tooltipFlags) {
        List<ITextComponent> list = new ArrayList<>();
        if (displayInfo == null)
            return Collections.singletonList(new TranslationTextComponent("skillpage." + getId().getPath() + ".title"));

        IFormattableTextComponent title = (new StringTextComponent("")).append(this.getPageName()).mergeStyle(Rarity.EPIC.color);
//        title.mergeStyle(TextFormatting.ITALIC);
        list.add(title);

        if (tooltipFlags.isAdvanced())
            list.add(new StringTextComponent(getId().toString()));
        else
            list.add(getDisplayInfo().getDescription());

        return list;
    }

    @Override
    public int compareTo(SkillPage other) {
        return compare(this, other);
    }

    public Map<ResourceLocation, Skill> getRootSkills() {
        return ImmutableMap.copyOf(rootSkills);
    }

    public Skill putRootSkill(ResourceLocation key, Skill skill) {
        return rootSkills.put(key, skill);
    }

    public static class Builder extends SkillPageBuilder {
        public static Builder builder() {
            return new Builder();
        }
    }
}