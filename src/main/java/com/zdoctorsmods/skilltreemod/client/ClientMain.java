package com.zdoctorsmods.skilltreemod.client;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.zdoctorsmods.skilltreemod.SkillTree;
import com.zdoctorsmods.skilltreemod.client.gui.screens.skills.SkillScreen;
import com.zdoctorsmods.skilltreemod.client.multiplayer.ClientSkills;
import com.zdoctorsmods.skilltreemod.network.packets.ClientboundUpdateLocalizationPacket;
import com.zdoctorsmods.skilltreemod.network.packets.ClientboundUpdateSkillsPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SkillTree.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ClientMain {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Player PLAYER = Minecraft.getInstance().player;
    public static final ClientSkills SKILLS = new ClientSkills();
    private static final ClientLanguageManager LANGUAGE_MANAGER = new ClientLanguageManager();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == Phase.END)
            handleKeybinds();
    }

    private static void handleKeybinds() {
        while (Options.OPEN_SKILL_TREE.get().consumeClick()) {
            Minecraft.getInstance().setScreen(new SkillScreen());
        }
    }

    public static void updateSkills(ClientboundUpdateSkillsPacket packet) {
        LOGGER.debug("Received skill update from server: {} added, {} removed. {} progress changed. {} skill points.",
                packet.getAdded().size(), packet.getRemoved().size(), packet.getProgress().size(),
                packet.getSkillPoints());
        ClientMain.SKILLS.update(packet);
    }

    public static void updateLocalizations(ClientboundUpdateLocalizationPacket packet) {
        LOGGER.debug("Recieved localization update from server");
        LANGUAGE_MANAGER.update(packet);
    }

    @Mod.EventBusSubscriber(modid = SkillTree.MODID, bus = Bus.MOD, value = Dist.CLIENT)
    public class ModEvents {
        @SubscribeEvent
        public static void onResourceManagerReload(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(LANGUAGE_MANAGER);
        }

    }
}
