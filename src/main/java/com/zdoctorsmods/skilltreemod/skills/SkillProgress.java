package com.zdoctorsmods.skilltreemod.skills;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class SkillProgress {
    final Map<String, CriterionProgress> criteria;
    private String[][] requirements = new String[0][];

    public SkillProgress() {
        this.criteria = Maps.newHashMap();
    }

    public SkillProgress(Map<String, CriterionProgress> criteria) {
        this.criteria = criteria;
    }

    public void update(Map<String, Criterion> pCriteria, String[][] pRequirements) {
        Set<String> criterions = pCriteria.keySet();
        this.criteria.entrySet().removeIf(criteria -> {
            return !criterions.contains(criteria.getKey());
        });

        for (String requirement : criterions) {
            if (!this.criteria.containsKey(requirement)) {
                this.criteria.put(requirement, new CriterionProgress());
            }
        }

        this.requirements = pRequirements;
    }

    public CriterionProgress getCriterion(String criterionName) {
        return this.criteria.get(criterionName);
    }

    public void serializeToNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeMap(this.criteria, FriendlyByteBuf::writeUtf, (buffer, progress) -> {
            progress.serializeToNetwork(buffer);
        });
    }

    public static SkillProgress fromNetwork(FriendlyByteBuf pBuffer) {
        Map<String, CriterionProgress> map = pBuffer.readMap(FriendlyByteBuf::readUtf, CriterionProgress::fromNetwork);
        return new SkillProgress(map);
    }

    public Iterable<String> getRemainingCriteria() {
        List<String> list = new ArrayList<>();

        for (Map.Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
            if (!entry.getValue().isDone()) {
                list.add(entry.getKey());
            }
        }

        return list;
    }

    public String getProgressText() {
        if (this.criteria.isEmpty()) {
            return null;
        } else {
            int i = this.requirements.length;
            if (i <= 1) {
                return null;
            } else {
                int j = this.countCompletedRequirements();
                return j + "/" + i;
            }
        }
    }

    public Iterable<String> getCompletedCriteria() {
        List<String> list = new ArrayList<>();

        for (Map.Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
            if (entry.getValue().isDone()) {
                list.add(entry.getKey());
            }
        }

        return list;
    }

    public boolean isDone() {
        if (this.requirements.length == 0) {
            return false;
        } else {
            for (String[] requirements : this.requirements) {
                boolean isDone = false;

                for (String requirement : requirements) {
                    CriterionProgress criterionprogress = this.getCriterion(requirement);
                    if (criterionprogress != null && criterionprogress.isDone()) {
                        isDone = true;
                        break;
                    }
                }

                if (!isDone) {
                    return false;
                }
            }

            return true;
        }
    }

    public float getPercent() {
        if (this.criteria.isEmpty()) {
            return 0.0F;
        } else {
            float f = (float) this.requirements.length;
            float f1 = (float) this.countCompletedRequirements();
            return f1 / f;
        }
    }

    private int countCompletedRequirements() {
        int i = 0;

        for (String[] requirements : this.requirements) {
            boolean anyDone = false;

            for (String requirement : requirements) {
                CriterionProgress criterionprogress = this.getCriterion(requirement);
                if (criterionprogress != null && criterionprogress.isDone()) {
                    anyDone = true;
                    break;
                }
            }

            if (anyDone) {
                ++i;
            }
        }
        // int count = (int) Stream.of(this.requirements).filter(requirements ->
        // Stream.of(requirements)
        // .map(this.criteria::get).anyMatch(criteria -> criteria != null &&
        // criteria.isDone())).count();
        return i;
    }

    public boolean hasProgress() {
        for (CriterionProgress criterionprogress : this.criteria.values()) {
            if (criterionprogress.isDone()) {
                return true;
            }
        }

        return false;
    }

    public boolean grantProgress(String pCriterionName) {
        CriterionProgress criterionprogress = this.criteria.get(pCriterionName);
        if (criterionprogress != null && !criterionprogress.isDone()) {
            criterionprogress.grant();
            return true;
        } else {
            return false;
        }
    }

    public boolean revokeProgress(String pCriterionName) {
        CriterionProgress criterionprogress = this.criteria.get(pCriterionName);
        if (criterionprogress != null && criterionprogress.isDone()) {
            criterionprogress.revoke();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "SkillProgress{criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements)
                + "}";
    }

    public static class Serializer implements JsonDeserializer<SkillProgress>, JsonSerializer<SkillProgress> {

        @Override
        public JsonElement serialize(SkillProgress pSrc, Type pTypeOfSrc, JsonSerializationContext pContext) {
            JsonObject jsonobject = new JsonObject();
            JsonObject jsonobject1 = new JsonObject();

            for (Map.Entry<String, CriterionProgress> entry : pSrc.criteria.entrySet()) {
                CriterionProgress criterionprogress = entry.getValue();
                if (criterionprogress.isDone()) {
                    jsonobject1.add(entry.getKey(), criterionprogress.serializeToJson());
                }
            }

            if (!jsonobject1.entrySet().isEmpty()) {
                jsonobject.add("criteria", jsonobject1);
            }

            jsonobject.addProperty("done", pSrc.isDone());
            return jsonobject;
        }

        @Override
        public SkillProgress deserialize(JsonElement pJson, Type pTypeOfT, JsonDeserializationContext pContext)
                throws JsonParseException {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(pJson, "skill");
            JsonObject jsonobject1 = GsonHelper.getAsJsonObject(jsonobject, "criteria", new JsonObject());
            SkillProgress skillProgress = new SkillProgress();

            for (Map.Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
                String s = entry.getKey();
                skillProgress.criteria.put(s,
                        CriterionProgress.fromJson(GsonHelper.convertToString(entry.getValue(), s)));
            }

            return skillProgress;
        }
    }
}
