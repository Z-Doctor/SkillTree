package com.zdoctorsmods.skilltreemod.skills;

public enum SkillEntityType {
    NONE,
    ALL,
    ID, // Will expect of resource ids which the skill can be applied to
    TAG, // I don't know how tags work, but I think they can be used like this
    PLAYER,
    VILLAGER,
    PASSIVE,
    HOSTILE,
}
