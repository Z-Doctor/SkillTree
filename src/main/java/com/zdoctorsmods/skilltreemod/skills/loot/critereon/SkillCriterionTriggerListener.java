package com.zdoctorsmods.skilltreemod.skills.loot.critereon;

import java.util.HashMap;

import com.zdoctorsmods.skilltreemod.SkillTree;
import com.zdoctorsmods.skilltreemod.server.PlayerSkills;
import com.zdoctorsmods.skilltreemod.skills.Skill;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;

public class SkillCriterionTriggerListener<T extends CriterionTriggerInstance> extends CriterionTrigger.Listener<T> {
    private final static Advancement EMPTY_ADVANCEMENT = new Advancement(new ResourceLocation(SkillTree.MODID, "empty"),
            null, null, null, new HashMap<>(1),
            null);
    private final Skill skill;
    private final String criterion;
    private final PlayerSkills playerSkills;

    public SkillCriterionTriggerListener(T pTrigger, Advancement advancement, String criterion) {
        super(pTrigger, advancement, criterion);
        this.criterion = criterion;
        skill = null;
        playerSkills = null;
    }

    public SkillCriterionTriggerListener(PlayerSkills playerSkills, T pTrigger, Skill skill, String criterion) {
        super(pTrigger, EMPTY_ADVANCEMENT, criterion);
        this.playerSkills = playerSkills;
        this.skill = skill;
        this.criterion = criterion;
    }

    @Override
    public void run(PlayerAdvancements pPlayerAdvancements) {
        playerSkills.award(skill, criterion);
    }

    public void run(PlayerSkills playerSkills) {
        playerSkills.award(skill, criterion);
    }

}
