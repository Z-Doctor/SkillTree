package com.zdoctorsmods.skilltreemod.skills;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.NotImplementedException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class Skill {
    private Skill parent;
    private ResourceLocation id;
    private DisplayInfo display;
    private SkillEntityType entityType = SkillEntityType.PLAYER;
    private final Map<String, Criterion> criteria;
    private final String[][] requirements;
    private final Set<Skill> children = Sets.newLinkedHashSet();

    public Skill(ResourceLocation id, Skill parent, DisplayInfo display, Map<String, Criterion> criteria,
            String[][] requirements) {
        this.id = id;
        this.display = display;
        if (criteria == null)
            this.criteria = Maps.newHashMap();
        else
            this.criteria = ImmutableMap.copyOf(criteria);
        this.parent = parent;
        this.requirements = requirements;
        if (this.parent != null) {
            this.parent.addChild(this);
        }
    }

    public void addChild(Skill skill) {
        children.add(skill);
    }

    public Skill getParent() {
        return parent;
    }

    public Set<Skill> getChildren() {
        return ImmutableSet.copyOf(children);
    }

    public ResourceLocation getId() {
        return id;
    }

    public DisplayInfo getDisplay() {
        return display;
    }

    public SkillEntityType getEntityType() {
        return this.entityType;
    }

    public Builder deconstruct() {
        return new Builder(parent == null ? null : parent.getId(), display, criteria, requirements);
    }

    public static class Builder implements net.minecraftforge.common.extensions.IForgeAdvancementBuilder {
        private ResourceLocation parentId;
        private Skill parent;
        private Map<String, Criterion> criteria = Maps.newHashMap();
        private String[][] requirements = new String[0][];
        private DisplayInfo display;
        private RequirementsStrategy requirementsStrategy = RequirementsStrategy.AND;

        Builder(ResourceLocation skillParentId, DisplayInfo display, Map<String, Criterion> criteria,
                String[][] requirements) {
            this.parentId = skillParentId;
            this.display = display;
            this.criteria = criteria;
            this.requirements = requirements;
        }

        private Builder() {
        }

        {
            setSPCost(1);
        }

        public static Builder skill() {
            return new Builder();
        }

        public Builder parent(Skill parent) {
            this.parent = parent;
            return this;
        }

        public Builder parent(ResourceLocation parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder display(ItemStack icon, Component title, Component description,
                ResourceLocation background, FrameType frame, boolean hidden) {
            return this.display(new DisplayInfo(icon, title, description, background, frame, hidden));
        }

        public Builder display(ItemLike icon, Component title, Component description,
                ResourceLocation background, FrameType frame, boolean hidden) {
            return this.display(
                    new DisplayInfo(new ItemStack(icon.asItem()), title, description, background, frame, hidden));
        }

        public Builder display(DisplayInfo display) {
            this.display = display;
            return this;
        }

        public Builder addCriterion(String key, CriterionTriggerInstance criterion) {
            return this.addCriterion(key, new Criterion(criterion));
        }

        public Builder addCriterion(String key, Criterion criterion) {
            if (this.criteria.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate criterion " + key);
            } else {
                this.criteria.put(key, criterion);
                return this;
            }
        }

        public Builder requirements(RequirementsStrategy requirementsStrategy) {
            this.requirementsStrategy = requirementsStrategy;
            return this;
        }

        public Builder requirements(String[][] requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder setSPCost(int cost) {
            if (cost < 0)
                throw new IllegalArgumentException("Tried setting cost of %s to less than 0 value");
            criteria.put("sp_cost", new Criterion());
            return this;
        }

        public boolean canBuild(Function<ResourceLocation, Skill> skillTreeLookup) {
            if (parentId == null)
                return true;
            else if (parent == null)
                parent = skillTreeLookup.apply(this.parentId);
            return this.parent != null;
        }

        public Skill build(ResourceLocation id) {
            if (!this.canBuild((p_138407_) -> {
                return null;
            })) {
                throw new IllegalStateException("Tried to build incomplete skill!");
            } else if (this.requirements == null) {
                this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            return new Skill(id, parent, display, criteria, requirements);
        }

        public Skill save(Consumer<Skill> pConsumer, String skillId) {
            Skill skill = build(new ResourceLocation(skillId));
            pConsumer.accept(skill);
            return skill;
        }

        public JsonObject serializeToJson() {
            JsonObject jsonobject = new JsonObject();
            if (parent != null) {
                jsonobject.addProperty("parent", parent.getId().toString());
            } else if (parentId != null) {
                jsonobject.addProperty("parent", this.parentId.toString());
            }

            if (display != null)
                jsonobject.add("display", display.serializeToJson());

            return jsonobject;
        }

        public void serializeToNetwork(FriendlyByteBuf pBuffer) {
            pBuffer.writeNullable(this.parentId, FriendlyByteBuf::writeResourceLocation);
            pBuffer.writeNullable(this.display, (byteBuffer, displayInfo) -> {
                displayInfo.serializeToNetwork(byteBuffer);
            });
        }

        public static Builder fromJson(JsonObject pJson) {
            ResourceLocation parent = pJson.has("parent")
                    ? new ResourceLocation(GsonHelper.getAsString(pJson, "parent"))
                    : null;
            DisplayInfo displayInfo = DisplayInfo.fromJson(GsonHelper.getAsJsonObject(pJson, "display"));

            if (parent == null && displayInfo.getPosition() != null) {
                throw new JsonSyntaxException("Skill tree with defined position detetected");
            } else if (parent != null && displayInfo.getPosition() == null) {
                throw new JsonSyntaxException("Skill with no position data detected");
            } else if (parent != null) {
                if (displayInfo.getPosition().x < 0) {
                    throw new JsonSyntaxException("Skill x position cannot be less than 0");
                } else if (displayInfo.getPosition().y < 0) {
                    throw new JsonSyntaxException("Skill y position cannot be less than 0");
                }
            }

            return new Builder().parent(parent).display(displayInfo);
        }

        public static Builder fromNetwork(FriendlyByteBuf pBuffer) {
            ResourceLocation resourcelocation = pBuffer.readNullable(FriendlyByteBuf::readResourceLocation);
            DisplayInfo displayinfo = pBuffer.readNullable(DisplayInfo::fromNetwork);
            return skill().parent(resourcelocation).display(displayinfo);
        }

    }

    public Skill getTree() {
        throw new NotImplementedException();
    }
}