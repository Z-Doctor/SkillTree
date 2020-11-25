package zdoctor.zskilltree.skilltree.skill;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Criterion;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.lwjgl.system.NonnullDefault;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.annotations.ClassNameMapper;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.skilltree.data.builders.SkillBuilder;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import javax.annotation.Nonnull;
import java.util.*;

@ClassNameMapper(key = ModMain.MODID + ":skill")
public class Skill extends ForgeRegistryEntry.UncheckedRegistryEntry<Skill> implements CriterionTracker {
    public static final Skill NONE = new Skill();

    private SkillDisplayInfo displayInfo;

    private Map<String, Criterion> criteria;
    private String[][] requirements;

    // Skills should only have a parent page or a parent skill, not both
    //  The parent page should be inferred from the root skill to save bandwidth
    private ResourceLocation parentPage;
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

    public Skill(@Nonnull PacketBuffer buf) {
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

    @NonnullDefault
    public Skill(ItemStack icon, String name, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
        this(new ResourceLocation(name), new SkillDisplayInfo(icon, new TranslationTextComponent("skill." + name + ".title"),
                new TranslationTextComponent("skill." + name + ".description")), criteriaIn, requirementsIn);
    }

    @NonnullDefault
    public Skill(ResourceLocation id, SkillDisplayInfo displayInfo, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
        setRegistryName(id);
        this.displayInfo = displayInfo;
        this.criteria = ImmutableMap.copyOf(criteriaIn);
        this.requirements = requirementsIn;
    }

    public static Skill deserialize(ResourceLocation id, JsonObject json, ConditionArrayParser conditionParser) {
        SkillBuilder builder = Builder.deserialize(json, conditionParser);
        return builder.build(id);
    }

    @Override
    public boolean shouldClientTrack() {
        return true;
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

    public Skill setPage(ResourceLocation skillPage) {
        this.parentPage = skillPage;
        return this;
    }

    public SkillDisplayInfo getDisplayInfo() {
        return displayInfo;
    }

    public ResourceLocation getParentPage() {
        return parentPage;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + getRegistryName() +
                ", displayInfo=" + displayInfo +
                '}';
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

    public JsonObject serialize() {
        JsonObject jsonobject = new JsonObject();

        if (getParentPage() != null)
            jsonobject.addProperty("parent-page", getParentPage().toString());

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

    public static class Builder extends SkillBuilder {
        public static Builder builder() {
            return new Skill.Builder();
        }
    }
}
