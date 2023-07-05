package com.zdoctorsmods.skilltreemod.client.multiplayer;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.zdoctorsmods.skilltreemod.network.packets.ClientboundUpdateSkillsPacket;
import com.zdoctorsmods.skilltreemod.skills.Skill;
import com.zdoctorsmods.skilltreemod.skills.SkillEvent;
import com.zdoctorsmods.skilltreemod.skills.SkillList;
import com.zdoctorsmods.skilltreemod.skills.SkillProgress;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(Dist.CLIENT)
public class ClientSkills {
    private static final Logger LOGGER = LogUtils.getLogger();
    private int skillPoints;
    private final SkillList skills = new SkillList();
    private final Map<Skill, SkillProgress> progress = new HashMap<>();
    private Skill selectedTree;
    private SkillList.Listener listener;

    public ClientSkills() {
    }

    public void update(ClientboundUpdateSkillsPacket packet) {
        skillPoints = packet.getSkillPoints();
        if (packet.shouldReset()) {
            skills.clear();
            progress.clear();
        }

        this.skills.remove(packet.getRemoved());
        this.skills.add(packet.getAdded());

        MinecraftForge.EVENT_BUS.post(new SkillEvent.RegisterSkillEvent(skills));

        for (Map.Entry<ResourceLocation, SkillProgress> entry : packet.getProgress().entrySet()) {
            Skill skill = this.skills.get(entry.getKey());
            if (skill != null) {
                SkillProgress skillProgress = entry.getValue();
                skillProgress.update(skill.getCriteria(), skill.getRequirements());
                this.progress.put(skill, skillProgress);
                if (this.listener != null) {
                    this.listener.onUpdateSkillProgress(skill, skillProgress);
                }

            } else {
                LOGGER.warn("Server informed client about progress for unknown skill {}", entry.getKey());
            }
        }

    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public SkillList getSkills() {
        return skills;
    }

    public void setListener(SkillList.Listener listener) {
        this.listener = listener;
        skills.setListener(listener);

        if (listener != null)
            setSelectedTab(selectedTree, false);
    }

    public void setSelectedTab(Skill skill, boolean tellServer) {
        selectedTree = skill;
        if (listener != null)
            listener.onSelectedTreeChanged(skill);
    }

    public SkillProgress getOrStartProgress(Skill skill) {
        SkillProgress progress = this.progress.get(skill);
        if (progress == null) {
            progress = new SkillProgress();
            progress.update(skill.getCriteria(), skill.getRequirements());
            this.progress.put(skill, progress);
        }
        return progress;
    }

    public boolean hasSkill(Skill skill) {
        SkillProgress progress = getOrStartProgress(skill);
        return progress.isDone();
    }

}
