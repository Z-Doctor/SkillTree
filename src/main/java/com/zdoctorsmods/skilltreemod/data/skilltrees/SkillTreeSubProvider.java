package com.zdoctorsmods.skilltreemod.data.skilltrees;

import java.util.function.Consumer;

import com.zdoctorsmods.skilltreemod.skills.Skill;

import net.minecraft.core.HolderLookup;

public interface SkillTreeSubProvider {
    void generate(HolderLookup.Provider pRegistries, Consumer<Skill> pWriter);
}
