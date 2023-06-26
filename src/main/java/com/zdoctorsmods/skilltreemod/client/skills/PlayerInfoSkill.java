package com.zdoctorsmods.skilltreemod.client.skills;

import com.zdoctorsmods.skilltreemod.SkillTreeMod;
import com.zdoctorsmods.skilltreemod.skills.DisplayInfo;
import com.zdoctorsmods.skilltreemod.skills.Skill;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// TODO add support for placement of skills either on sides or tops
@OnlyIn(Dist.CLIENT)
public class PlayerInfoSkill extends Skill {
    private static final Component TITLE = Component.translatable(SkillTreeMod.MODID + ".player_info.title");
    private static final Component DESCRIPTION = Component
            .translatable(SkillTreeMod.MODID + ".player_info.description");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(
            "minecraft:textures/gui/advancements/backgrounds/stone.png");
    private static final DisplayInfo DISPLAY = new DisplayInfo(new ItemStack(Items.WRITTEN_BOOK), TITLE, DESCRIPTION,
            BACKGROUND, null, false);

    public PlayerInfoSkill() {
        super(new ResourceLocation(SkillTreeMod.MODID, "player_info"), null, DISPLAY, null, null);
    }
}
