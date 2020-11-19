package zdoctor.zskilltree.skill;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Criterion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.client.gui.ImageAssets;
import zdoctor.zskilltree.extra.ImageAsset;
import zdoctor.zskilltree.skillpages.SkillPage;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Skill {
    private ResourceLocation id;
    private SkillDisplayInfo displayInfo;

    private ImmutableMap<String, Criterion> criteria;
    private String[][] requirements;

    private ResourceLocation pageId = SkillPage.NONE.getId();

    public Skill() {

    }

    public Skill(ItemStack icon, String name, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
        this(new ResourceLocation(name), new SkillDisplayInfo(icon, new TranslationTextComponent("skill." + name + ".title"),
                new TranslationTextComponent("skill." + name + ".description")), criteriaIn, requirementsIn);
    }

    public Skill(ResourceLocation id, SkillDisplayInfo displayInfo, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
        this.id = id;
        this.displayInfo = displayInfo;
        this.criteria = ImmutableMap.copyOf(criteriaIn);
        this.requirements = requirementsIn;
    }

//    @Override
    public ImmutableMap<String, Criterion> getCriteria() {
        return criteria;
    }

//    @Override
    public String[][] getRequirements() {
        return requirements;
    }

//    @Override
    public void writeTo(PacketBuffer buf) {

    }

//    @Override
    public void readFrom(PacketBuffer buf) {

    }

    public Skill setPage(SkillPage skillPage) {
        this.pageId = skillPage.getId();
        return this;
    }

    public ResourceLocation getId() {
        return id;
    }

    public SkillDisplayInfo getDisplayInfo() {
        return displayInfo;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", pageId" + pageId +
                ", displayInfo=" + displayInfo +
                '}';
    }

    public Builder copy() {
        return new Builder(pageId, displayInfo, getCriteria(), getRequirements()).onPage(pageId);
    }

    public static class Builder {
        ResourceLocation pageId = SkillPage.NONE.getId();
        SkillDisplayInfo display;
        private Map<String, Criterion> criteria = new HashMap<>();;
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
            return onPage(skillPage.getId());
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
