package com.zdoctorsmods.skilltreemod.data.skilltrees.events;

import com.zdoctorsmods.skilltreemod.skills.Skill;
import com.zdoctorsmods.skilltreemod.skills.SkillList;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

public class RegisterSkillEvent extends Event {
    private final SkillList skillList;
    private final LogicalSide side;

    public RegisterSkillEvent(LogicalSide side, SkillList skillList) {
        this.side = side;
        this.skillList = skillList;
    }

    public LogicalSide getSide() {
        return side;
    }

    public void register(Skill skill) {
        skillList.add(skill);
    }
}
