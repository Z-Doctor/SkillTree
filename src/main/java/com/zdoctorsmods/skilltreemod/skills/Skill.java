package com.zdoctorsmods.skilltreemod.skills;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class Skill {
    private Skill parent;
    private ResourceLocation id;
    private DisplayInfo display;
    private SkillEntityType entityType = SkillEntityType.PLAYER;
    private final Map<String, Criterion> criteria;
    private final LootItemCondition[] purchaseConditions;
    private final String[][] requirements;
    private final Set<Skill> children = new LinkedHashSet<>();

    public Skill(ResourceLocation id, Skill parent, DisplayInfo display, Map<String, Criterion> criteria,
            String[][] requirements, LootItemCondition[] purchaseConditions) {
        this.id = id;
        this.display = display;
        this.criteria = ImmutableMap.copyOf(criteria);
        this.parent = parent;
        this.requirements = requirements;
        if (this.parent != null) {
            this.parent.addChild(this);
        }
        this.purchaseConditions = purchaseConditions;
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

    public Map<String, Criterion> getCriteria() {
        return criteria;
    }

    public String[][] getRequirements() {
        return requirements;
    }

    public int getMaxCriteraRequired() {
        return this.requirements.length;
    }

    public LootItemCondition[] getPurchaseConditions() {
        return purchaseConditions;
    }

    public Builder deconstruct() {
        return new Builder(parent == null ? null : parent.getId(), display, criteria, requirements, purchaseConditions);
    }

    public static class Builder implements net.minecraftforge.common.extensions.IForgeAdvancementBuilder {
        private ResourceLocation parentId;
        private Skill parent;
        private Map<String, Criterion> criteria = new LinkedHashMap<>();
        private String[][] requirements = new String[0][];
        private LootItemCondition[] purchaseConditions = new LootItemCondition[0];
        private DisplayInfo display;
        private RequirementsStrategy requirementsStrategy = RequirementsStrategy.AND;

        Builder(ResourceLocation skillParentId, DisplayInfo display, Map<String, Criterion> criteria,
                String[][] requirements, LootItemCondition[] purchaseConditions) {
            this.parentId = skillParentId;
            this.display = display;
            this.criteria = criteria;
            this.requirements = requirements;
            this.purchaseConditions = purchaseConditions;
        }

        private Builder() {
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
            }

            if (criteria == null)
                criteria = new HashMap<>();

            if (this.requirements == null) {
                this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            return new Skill(id, parent, display, criteria, requirements, purchaseConditions);
        }

        public Skill save(Consumer<Skill> pConsumer, String skillId) {
            Skill skill = build(new ResourceLocation(skillId));
            pConsumer.accept(skill);
            return skill;
        }

        public JsonObject serializeToJson() {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            JsonObject jsonobject = new JsonObject();
            if (parent != null) {
                jsonobject.addProperty("parent", parent.getId().toString());
            } else if (parentId != null) {
                jsonobject.addProperty("parent", this.parentId.toString());
            }

            if (display != null)
                jsonobject.add("display", display.serializeToJson());

            JsonObject jsonobject1 = new JsonObject();
            for (Map.Entry<String, Criterion> entry : this.criteria.entrySet()) {
                jsonobject1.add(entry.getKey(), entry.getValue().serializeToJson());
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

        public void serializeToNetwork(FriendlyByteBuf pBuffer) {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            pBuffer.writeNullable(this.parentId, FriendlyByteBuf::writeResourceLocation);
            pBuffer.writeNullable(this.display, (byteBuffer, displayInfo) -> {
                displayInfo.serializeToNetwork(byteBuffer);
            });

            Criterion.serializeToNetwork(this.criteria, pBuffer);
            pBuffer.writeVarInt(this.requirements.length);

            for (String[] requirements : this.requirements) {
                pBuffer.writeVarInt(requirements.length);

                for (String requirement : requirements) {
                    pBuffer.writeUtf(requirement);
                }
            }
        }

        public static Builder fromJson(JsonObject pJson, DeserializationContext context) {
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

            LootItemCondition[] purchaseCondition = new LootItemCondition[0];

            if (pJson.has("purchase_condition")) {
                JsonArray jArray = pJson.getAsJsonArray("purchase_condition");
                purchaseCondition = context.deserializeConditions(jArray, "purchase_condition",
                        LootContextParamSets.ADVANCEMENT_ENTITY);
            }

            Map<String, Criterion> map = Criterion.criteriaFromJson(GsonHelper.getAsJsonObject(pJson, "criteria"),
                    context);
            if (map.isEmpty()) {
                throw new JsonSyntaxException("Skill criteria cannot be empty");
            } else {
                JsonArray jsonarray = GsonHelper.getAsJsonArray(pJson, "requirements", new JsonArray());
                String[][] requirements = new String[jsonarray.size()][];

                for (int i = 0; i < jsonarray.size(); ++i) {
                    JsonArray jsonarray1 = GsonHelper.convertToJsonArray(jsonarray.get(i),
                            "requirements[" + i + "]");
                    requirements[i] = new String[jsonarray1.size()];

                    for (int j = 0; j < jsonarray1.size(); ++j) {
                        requirements[i][j] = GsonHelper.convertToString(jsonarray1.get(j),
                                "requirements[" + i + "][" + j + "]");
                    }
                }

                if (requirements.length == 0) {
                    requirements = new String[map.size()][];
                    int k = 0;

                    for (String s2 : map.keySet()) {
                        requirements[k++] = new String[] { s2 };
                    }
                }

                for (String[] astring1 : requirements) {
                    if (astring1.length == 0 && map.isEmpty()) {
                        throw new JsonSyntaxException("Requirement entry cannot be empty");
                    }

                    for (String s : astring1) {
                        if (!map.containsKey(s)) {
                            throw new JsonSyntaxException("Unknown required criterion '" + s + "'");
                        }
                    }
                }

                for (String s1 : map.keySet()) {
                    boolean flag = false;

                    for (String[] astring2 : requirements) {
                        if (ArrayUtils.contains(astring2, s1)) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        throw new JsonSyntaxException("Criterion '" + s1
                                + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
                    }
                }

                return new Builder(parent, displayInfo, map, requirements, purchaseCondition);
            }
        }

        public static Builder fromNetwork(FriendlyByteBuf pBuffer) {
            ResourceLocation resourcelocation = pBuffer.readNullable(FriendlyByteBuf::readResourceLocation);
            DisplayInfo displayinfo = pBuffer.readNullable(DisplayInfo::fromNetwork);
            Map<String, Criterion> map = Criterion.criteriaFromNetwork(pBuffer);
            String[][] astring = new String[pBuffer.readVarInt()][];

            for (int i = 0; i < astring.length; ++i) {
                astring[i] = new String[pBuffer.readVarInt()];

                for (int j = 0; j < astring[i].length; ++j) {
                    astring[i][j] = pBuffer.readUtf();
                }
            }
            return new Builder(resourcelocation, displayinfo, map, astring, new LootItemCondition[0]);
        }

    }
}