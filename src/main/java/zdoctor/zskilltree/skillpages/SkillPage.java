package zdoctor.zskilltree.skillpages;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
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
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.api.interfaces.ITrackCriterion;
import zdoctor.zskilltree.extra.ImageAsset;
import zdoctor.zskilltree.skill.Skill;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class SkillPage implements ITrackCriterion {
    public static final SkillPage NONE = new SkillPage();
    private static final SkillPageDisplayInfo MISSING = new SkillPageDisplayInfo(ItemStack.EMPTY,
            new TranslationTextComponent("skillpage.missing.title"),
            new TranslationTextComponent("skillpage.missing.description")
    ).setHidden();
    private final Map<ResourceLocation, Skill> children = new HashMap<>();
    private int index;
    private Map<String, Criterion> criteria;
    private String[][] requirements;
    private ResourceLocation id;
    private SkillPageDisplayInfo displayInfo;

    public SkillPage() {
        id = new ResourceLocation(ModMain.MODID, "empty");
        displayInfo = MISSING;
        criteria = ImmutableMap.<String, Criterion>builder().build();
        requirements = new String[0][];
    }

    public SkillPage(int index, ResourceLocation id, SkillPageDisplayInfo displayInfo, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
        this.id = id;
        this.displayInfo = displayInfo == null ? MISSING : displayInfo;
        this.index = index;

        this.criteria = ImmutableMap.copyOf(criteriaIn);
        this.requirements = requirementsIn == null ? new String[0][] : requirementsIn;
    }

    public Map<String, Criterion> getCriteria() {
        return criteria;
    }

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
            for (String[] requirements : this.requirements) {
                buf.writeVarInt(requirements.length);
                for (String requirement : requirements) {
                    buf.writeString(requirement);
                }
            }
    }

    @Override
    public void readFrom(PacketBuffer buf) {
        id = buf.readResourceLocation();
        index = buf.readVarInt();
        if(buf.readBoolean())
            displayInfo = SkillPageDisplayInfo.read(buf);

        criteria = Criterion.criteriaFromNetwork(buf);

        String[][] requirements = new String[buf.readVarInt()][];
        for (int i = 0; i < requirements.length; ++i) {
            requirements[i] = new String[buf.readVarInt()];
            for (int j = 0; j < requirements[i].length; ++j) {
                requirements[i][j] = buf.readString();
            }
        }
    }

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

    public void setIndex(int index) {
        this.index = index;
    }

    @OnlyIn(Dist.CLIENT)
    public ITextComponent getPageName() {
        return getDisplayInfo().getPageName();
    }

    //
    @OnlyIn(Dist.CLIENT)
    public SkillPageAlignment getAlignment() {
        return displayInfo.getAlignment();
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getIcon() {
        return displayInfo.getIcon();
    }

    //    @OnlyIn(Dist.CLIENT)
//    public abstract ItemStack createIcon();
//
    @OnlyIn(Dist.CLIENT)
    public ImageAsset getBackgroundImage() {
        return displayInfo.getBackground();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean drawInForegroundOfTab() {
        return displayInfo.drawTitle();
    }

    public int getLabelColor() {
        return 4210752;
    }

    public Builder copy() {
        return new Builder(index, this.displayInfo, criteria, requirements);
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
        children.put(skill.getId(), skill);
    }

    public Collection<Skill> getSkills() {
        return children.values();
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

    public static class Builder implements Comparable<Builder> {
        private int index = -1;
        private SkillPageDisplayInfo display;
        private Map<String, Criterion> criteria = new HashMap<>();
        private String[][] requirements;
        private IRequirementsStrategy requirementsStrategy = IRequirementsStrategy.AND;

        private Builder(@Nullable SkillPageDisplayInfo displayIn, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
            this(-1, displayIn, criteriaIn, requirementsIn);
        }

        private Builder(int index, @Nullable SkillPageDisplayInfo displayIn, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
            this.index = index;
            this.display = displayIn;
            this.criteria = criteriaIn;
            this.requirements = requirementsIn;
        }

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public static Builder deserialize(JsonObject json, ConditionArrayParser conditionParser) {
            // TODO Make conditional skill page class (?)
//            if ((json = net.minecraftforge.common.crafting.ConditionalAdvancement.processConditional(json)) == null) return null;
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
            return new Builder(index, displayInfo, criterion, requirements);
        }

        public static Builder readFrom(PacketBuffer buf) {
            int index = buf.readVarInt();
            SkillPageDisplayInfo displayInfo = null;
            if (buf.readBoolean())
                displayInfo = SkillPageDisplayInfo.read(buf);

            Map<String, Criterion> criteria = Criterion.criteriaFromNetwork(buf);
            String[][] astring = new String[buf.readVarInt()][];

            for (int i = 0; i < astring.length; ++i) {
                astring[i] = new String[buf.readVarInt()];

                for (int j = 0; j < astring[i].length; ++j) {
                    astring[i][j] = buf.readString();
                }
            }

            return new Builder(index, displayInfo, criteria, astring);
        }

        public static int compare(Builder in, Builder to) {
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

        public Builder withCriterion(String key, ICriterionInstance criterionIn) {
            return this.withCriterion(key, new Criterion(criterionIn));
        }

        public Builder withCriterion(String key, Criterion criterionIn) {
            if (this.criteria.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate criterion " + key);
            } else {
                this.criteria.put(key, criterionIn);
                return this;
            }
        }

        public Builder withRequirementsStrategy(IRequirementsStrategy strategy) {
            this.requirementsStrategy = strategy;
            return this;
        }

        public Builder withDisplay(ItemStack icon, ITextComponent title, ITextComponent description, SkillPageAlignment alignment, ImageAsset background) {
            return this.withDisplay(new SkillPageDisplayInfo(icon, title, description, background, alignment));
        }

        public Builder withDisplay(IItemProvider itemIn, ITextComponent title, ITextComponent description, ImageAsset background, SkillPageAlignment alignment) {
            return this.withDisplay(new SkillPageDisplayInfo(new ItemStack(itemIn.asItem()), title, description, background, alignment));
        }

        public Builder withDisplay(ItemStack icon, String name) {
            return withDisplay(icon, name, SkillPageAlignment.VERTICAL);
        }

        public Builder withDisplay(ItemStack icon, String name, SkillPageAlignment alignment) {
            return withDisplay(new SkillPageDisplayInfo(icon,
                    new TranslationTextComponent("skillpage." + name + ".title"),
                    new TranslationTextComponent("skillpage." + name + ".description"),
                    alignment));
        }

        public Builder withDisplay(SkillPageDisplayInfo displayIn) {
            this.display = displayIn;
            return this;
        }

        public Builder atIndex(int index) {
            this.index = index;
            return this;
        }

        public int getIndex() {
            return index;
        }

        public JsonObject serialize() {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("index", index);
            if (this.display != null) {
                jsonobject.add("display", this.display.serialize());
            }

            JsonObject jsonobject1 = new JsonObject();
            for (Map.Entry<String, Criterion> entry : this.criteria.entrySet()) {
                jsonobject1.add(entry.getKey(), entry.getValue().serialize());
            }

            jsonobject.add("criteria", jsonobject1);
            JsonArray jsonarray1 = new JsonArray();
            for (String[] astring : this.requirements) {
                JsonArray jsonarray = new JsonArray();

                for (String s : astring) {
                    jsonarray.add(s);
                }

                jsonarray1.add(jsonarray);
            }
            jsonobject.add("requirements", jsonarray1);

            return jsonobject;
        }

        public void writeTo(PacketBuffer buf) {
            buf.writeVarInt(index);
            if (this.display != null) {
                buf.writeBoolean(true);
                this.display.write(buf);
            } else
                buf.writeBoolean(false);

            Criterion.serializeToNetwork(this.criteria, buf);
            // TODO Check for 0?
            int j = requirements == null ? 0 : requirements.length;
            buf.writeVarInt(j);
            if (j > 0)
                for (String[] astring : this.requirements) {
                    buf.writeVarInt(astring.length);

                    for (String s : astring) {
                        buf.writeString(s);
                    }
                }
        }

        public SkillPage build(ResourceLocation id) {
            return new SkillPage(index, id, display, criteria, requirements);
        }

        public SkillPage register(Consumer<SkillPage> consumer, String id) {
            SkillPage page = this.build(new ResourceLocation(ModMain.MODID, id));
            consumer.accept(page);
            return page;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("Skill Builder{");
            sb.append("index=").append(index);
            sb.append(", display=").append(display);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public int compareTo(Builder other) {
            return Builder.compare(this, other);
        }
    }
}
