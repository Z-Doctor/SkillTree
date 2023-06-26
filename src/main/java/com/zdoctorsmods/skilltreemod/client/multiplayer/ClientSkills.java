package com.zdoctorsmods.skilltreemod.client.multiplayer;

import com.zdoctorsmods.skilltreemod.data.skilltrees.events.RegisterSkillEvent;
import com.zdoctorsmods.skilltreemod.network.packets.ClientboundUpdateSkillsPacket;
import com.zdoctorsmods.skilltreemod.skills.Skill;
import com.zdoctorsmods.skilltreemod.skills.SkillList;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;

@OnlyIn(Dist.CLIENT)
public class ClientSkills {
    private int skillPoints;
    private final SkillList skills = new SkillList();
    private Skill selectedTree;
    private SkillList.Listener listener;

    public ClientSkills() {
    }

    public void update(ClientboundUpdateSkillsPacket packet) {
        skillPoints = packet.getSkillPoints();
        if (packet.shouldReset()) {
            skills.clear();
        }

        this.skills.remove(packet.getRemoved());
        this.skills.add(packet.getAdded());
        MinecraftForge.EVENT_BUS.post(new RegisterSkillEvent(LogicalSide.CLIENT, skills));
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

}
