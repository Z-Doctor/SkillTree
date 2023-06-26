package com.zdoctorsmods.skilltreemod.skills;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.advancements.CriterionProgress;

public class SkillProgress {
    final Map<String, CriterionProgress> criteria;
    private String[][] requirements = new String[0][];

    public SkillProgress() {
        this.criteria = Maps.newHashMap();
    }

    public SkillProgress(Map<String, CriterionProgress> criteria) {
        this.criteria = criteria;
    }

    public CriterionProgress getCriterion(String criterionName) {
        return this.criteria.get(criterionName);
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
}
