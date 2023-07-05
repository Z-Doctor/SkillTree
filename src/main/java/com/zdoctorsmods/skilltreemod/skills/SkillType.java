package com.zdoctorsmods.skilltreemod.skills;

public class SkillType {
    public static final SkillType NONE = new SkillType(0);
    public static final SkillType PASSIVE = new SkillType(1);
    public static final SkillType ACTIVE = new SkillType(1 << 2);
    public static final SkillType TOGGLE = new SkillType(1 << 3);

    int flag;

    private SkillType(int flag) {
        this.flag = flag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SkillType type) {
            return (this.flag & type.flag) != 0;
        }
        return false;
    }

    public static SkillType of(SkillType... skillTypes) {
        int flag = 0;
        for (SkillType type : skillTypes) {
            flag |= type.flag;
            flag <<= 1;
        }
        return new SkillType(flag);
    }
}
