package com.zdoctorsmods.skilltreemod.skills;

import net.minecraftforge.eventbus.api.Event;

public class SkillEvent extends Event {
    public SkillEvent() {
    }

    public static class SkillGainedEvent extends SkillEvent {

    }

    public static class SkillProgressedEvent extends SkillEvent {

    }

    public static class SkillActivatedEvent extends SkillEvent {

    }

    public static class SkillToggledEvent extends SkillEvent {

    }

    public static class SkillPassiveTickedEvent extends SkillEvent {

    }

    public static class SkillPurchaseEvent extends SkillEvent {
        private final Type type;

        public SkillPurchaseEvent(Type type) {
            this.type = type;
        }

        public enum Type {
            NONE,
            CHECK,
            PURCHASED,
        }
    }

    public static class RegisterSkillEvent extends SkillEvent {
        private final SkillList skillList;

        public RegisterSkillEvent(SkillList skillList) {
            this.skillList = skillList;
        }

        public void register(Skill skill) {
            skillList.add(skill);
        }
    }

}
