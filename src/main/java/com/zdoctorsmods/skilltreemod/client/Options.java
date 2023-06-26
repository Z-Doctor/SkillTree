package com.zdoctorsmods.skilltreemod.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.zdoctorsmods.skilltreemod.SkillTreeMod;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class Options {
    public static final Lazy<KeyMapping> OPEN_SKILL_TREE = Lazy
            .of(() -> new KeyMapping("key.skilltree.toggleSkillTree", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K,
                    "key.categories.misc"));
    public static final Lazy<KeyMapping> RECENTER_SKILL_TREE = Lazy
            .of(() -> new KeyMapping("key.skilltree.recenterSkillTree", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R,
                    "key.categories.misc"));

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SKILL_TREE.get());
        event.register(RECENTER_SKILL_TREE.get());
    }

}
