package zdoctor.zskilltree.skilltree.skill;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Criterion;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.ImageAssets;
import zdoctor.zskilltree.api.annotations.ClassNameMapper;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

@ClassNameMapper(key = ModMain.MODID + ":skill")
public class Skill extends ForgeRegistryEntry.UncheckedRegistryEntry<Skill> implements CriterionTracker {
    public static final Skill NONE = new Skill();

    private SkillDisplayInfo displayInfo;

    private Map<String, Criterion> criteria;
    private String[][] requirements;

    // Skills should only have a parent page or a parent skill, not both
    //  The parent page should be inferred from the root skill to save bandwidth
    private SkillPage parentPage;
    private List<Skill> child_skills;

    private Skill() {
        setRegistryName(new ResourceLocation(ModMain.MODID, "skill_none"));
    }

    private Skill(Skill skill) {
        setRegistryName(Objects.requireNonNull(skill.getRegistryName()));
        displayInfo = skill.getDisplayInfo();
        criteria = skill.getCriteria();
        requirements = skill.getRequirements();
    }

    public Skill(PacketBuffer buf) {
        setRegistryName(buf.readResourceLocation());
        if (buf.readBoolean())
            displayInfo = SkillDisplayInfo.read(buf);

        criteria = Criterion.criteriaFromNetwork(buf);

        requirements = new String[buf.readVarInt()][];
        for (int i = 0; i < requirements.length; ++i) {
            requirements[i] = new String[buf.readVarInt()];
            for (int j = 0; j < requirements[i].length; ++j) {
                requirements[i][j] = buf.readString();
            }
        }
    }


    public Skill(ItemStack icon, String name, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
        this(new ResourceLocation(name), new SkillDisplayInfo(icon, new TranslationTextComponent("skill." + name + ".title"),
                new TranslationTextComponent("skill." + name + ".description")), criteriaIn, requirementsIn);
    }

    public Skill(ResourceLocation id, SkillDisplayInfo displayInfo, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
        setRegistryName(id);
        this.displayInfo = displayInfo;
        this.criteria = ImmutableMap.copyOf(criteriaIn);
        this.requirements = requirementsIn;
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
        buf.writeResourceLocation(Objects.requireNonNull(getRegistryName()));
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
    }

    public Skill setPage(SkillPage skillPage) {
        this.parentPage = skillPage;
        return this;
    }

//    public ResourceLocation getRegistryName() {
//        return id;
//    }

    public SkillDisplayInfo getDisplayInfo() {
        return displayInfo;
    }

    public SkillPage getParentPage() {
        return parentPage;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + getRegistryName() +
                ", displayInfo=" + displayInfo +
                '}';
    }

    public Builder copy() {
        return new Builder(getRegistryName(), getDisplayInfo(), getCriteria(), getRequirements()).onPage(getParentPage());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (!(obj instanceof Skill))
            return false;
        else {
            return Objects.equals(getRegistryName(), ((Skill) obj).getRegistryName());
        }
    }

    @Override
    public int hashCode() {
        return Objects.requireNonNull(getRegistryName()).hashCode();
    }

    @OnlyIn(Dist.CLIENT)
    public ITextComponent getSkillName() {
        return getDisplayInfo().getSkillName();
    }

    @OnlyIn(Dist.CLIENT)
    public List<? extends ITextProperties> getTooltip(ClientPlayerEntity player, ITooltipFlag.TooltipFlags tooltipFlags) {
        List<ITextComponent> list = new ArrayList<>();
        if (displayInfo == null)
            return Collections.singletonList(new TranslationTextComponent("skillpage." + getRegistryName().getPath() + ".title"));

        IFormattableTextComponent title = (new StringTextComponent("")).append(getSkillName()).mergeStyle(Rarity.EPIC.color);
//        title.mergeStyle(TextFormatting.ITALIC);
        list.add(title);

        if (tooltipFlags.isAdvanced())
            list.add(new StringTextComponent(getRegistryName().toString()));
        else
            list.add(getDisplayInfo().getDescription());

        return list;
    }

    public static class Builder {
        ResourceLocation pageId = SkillPage.NONE.getRegistryName();
        SkillDisplayInfo display;
        private Map<String, Criterion> criteria = new HashMap<>();
        private String[][] requirements;

        // TODO Add Criterion and requirements to be able to see or be able to buy
        // TODO Add rewards
        // TODO make having parent skills Criterion

        private Builder() {
        }

        private Builder(ResourceLocation pageId, @Nullable SkillDisplayInfo displayIn, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
            this.pageId = pageId;
            this.display = displayIn;
            this.criteria = criteriaIn;
            this.requirements = requirementsIn;
        }


//        public static Builder readFrom(PacketBuffer buf) {
//            ResourceLocation skillPage = buf.readResourceLocation();
//            SkillDisplayInfo displayInfo = null;
//            if (buf.readBoolean())
//                displayInfo = SkillDisplayInfo.read(buf);
//            return new Builder(skillPage, displayInfo);
//        }

        public static Builder builder() {
            return new Builder();
        }

        public static Builder deserialize(JsonObject json) {
            Builder builder = builder();

            if (json.has("page"))
                builder.pageId = new ResourceLocation(JSONUtils.getString(json, "page"));
            if (json.has("display"))
                builder.display = SkillDisplayInfo.deserialize(JSONUtils.getJsonObject(json, "display"));

            return builder;
        }

        public void writeTo(PacketBuffer buf) {
            buf.writeResourceLocation(pageId);
            if (this.display != null) {
                buf.writeBoolean(true);
                this.display.write(buf);
            } else
                buf.writeBoolean(false);
        }

        public ResourceLocation getPageId() {
            return pageId;
        }

        public Builder withDisplay(ItemStack icon, String name) {
            return withDisplay(icon, name, ImageAssets.COMMON_FRAME_UNOWNED);
        }

        public Builder withDisplay(ItemStack icon, String name, ImageAsset frame) {
            // TODO Add owned and unowned frames
            return this.withDisplay(new SkillDisplayInfo(icon, new TranslationTextComponent("skill." + name + ".title"),
                    new TranslationTextComponent("skill." + name + ".description"), frame));
        }

        public Builder withDisplay(ItemStack icon, ITextComponent skillName, ITextComponent description) {
            // TODO Add owned and unowned frames
            return this.withDisplay(new SkillDisplayInfo(icon, skillName, description, ImageAssets.COMMON_FRAME_UNOWNED));
        }

        public Builder withDisplay(ItemStack icon, ITextComponent skillName, ITextComponent description, ImageAsset frame) {
            return this.withDisplay(new SkillDisplayInfo(icon, skillName, description, frame));
        }

        public Builder withDisplay(IItemProvider itemIn, ITextComponent skillName, ITextComponent description, ImageAsset frame) {
            return this.withDisplay(new SkillDisplayInfo(new ItemStack(itemIn.asItem()), skillName, description, frame));
        }

        public Builder withDisplay(SkillDisplayInfo displayIn) {
            this.display = displayIn;
            return this;
        }

        public Builder onPage(SkillPage skillPage) {
            return onPage(skillPage.getRegistryName());
        }

        public Builder onPage(ResourceLocation pageId) {
            this.pageId = pageId;
            return this;
        }

        public Skill register(Consumer<Skill> consumer, String id) {
            Skill skill = this.build(new ResourceLocation(ModMain.MODID, id));
            consumer.accept(skill);
            return skill;
        }

        public Skill build(ResourceLocation id) {
            return new Skill(id, display, criteria, requirements);
        }

        public JsonElement serialize() {
            JsonObject jsonobject = new JsonObject();
            if (pageId != null)
                jsonobject.addProperty("page", pageId.toString());
            if (this.display != null)
                jsonobject.add("display", this.display.serialize());
            return jsonobject;
        }


    }
}
