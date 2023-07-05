package com.zdoctorsmods.skilltreemod;

import com.mojang.logging.LogUtils;
import com.zdoctorsmods.skilltreemod.server.PlayerSkills;
import com.zdoctorsmods.skilltreemod.server.ServerLanguageManager;
import com.zdoctorsmods.skilltreemod.network.SkillTreePacketHandler;
import com.zdoctorsmods.skilltreemod.network.packets.ServerboundClientSkillPacket;
import com.zdoctorsmods.skilltreemod.server.PlayerSkillList;
import com.zdoctorsmods.skilltreemod.server.ServerSkillManager;
import com.zdoctorsmods.skilltreemod.server.SkillCommands;
import com.zdoctorsmods.skilltreemod.skills.loot.critereon.SkillTriggers;
import com.zdoctorsmods.skilltreemod.skills.loot.providers.numbers.NumberProviders;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import java.util.List;

import org.slf4j.Logger;

// TODO Add Skill_Boought trigger and skill point earned trigger
// TODO Way to make skill automatically obtained
// TODO add villager to learn skill points from
// TODO Make skills able to be applied to all entities, but only players by default.
// TODO Add or make sure having an advancement can be used as a criteron
// TODO Maker sure generators work
// TODO optimize packet data to send as little as possible
// TODO Decide whether to add support for negative position and how handle scrolling
// TODO Make hot keys for active abilities to use them or toggle them
// TODO add greater visibility control of skills. By default all skills will be visible and you can read them,
// but you can obfuscate them either until its parent is bought or the skill is bought
// TODO Implement a way so that values can be defined in a settings file and automatically tied in the skill file i.e. $skill.cost
// TODO Implement potion effects that are infinite for passives that won't show a time on the client
// TODO add support for conditional loading of skills
// TODO Cleanup how skills are drawn and give useful info
// TODO Localize all commands
@Mod(SkillTree.MODID)
public class SkillTree {
    public static final String MODID = "skilltree";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final LevelResource PLAYER_SKILLS_DIR = new LevelResource("skills");

    private static MinecraftServer server;
    private static PlayerSkillList playerSkillList;
    private static ServerSkillManager skillManager;
    private static ServerLanguageManager languageManager;

    public static final SkillTreePacketHandler CHANNEL = new SkillTreePacketHandler();
    public static final SkillTreeConfig CONFIG = SkillTreeConfig.get();

    public SkillTree() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG.getForgeConfig());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigChanged);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerEvent);
        SkillTriggers.init();
    }

    public void onConfigChanged(ModConfigEvent.Reloading event) {
        LOGGER.debug("Config Reloaded");
        if (event.getConfig().getSpec() == CONFIG.getForgeConfig()) {
            LOGGER.debug("Reloading config {}", CONFIG.syncLocalizations.get());
            if (languageManager != null && CONFIG.syncLocalizations.get())
                reloadLocalizations();
        }
    }

    public void registerEvent(RegisterEvent event) {
        NumberProviders.init(event);
    }

    // @SubscribeEvent
    // @SuppressWarnings("deprecation")
    // public void onAdvancementProgress(
    // net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent
    // event) {
    // if (event.getEntity() instanceof ServerPlayer player) {
    // PlayerSkills playerSkills = getPlayerSkills(player);
    // if(playerSkills != null)
    // playerSkills.award(null, MODID);
    // }
    // }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        SkillCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarted(ServerAboutToStartEvent event) {
        server = event.getServer();
        playerSkillList = new PlayerSkillList(server);
    }

    @SubscribeEvent
    public void addSkillTreeLoader(AddReloadListenerEvent event) {
        event.addListener(skillManager = new ServerSkillManager(event.getServerResources().getPredicateManager()));
        event.addListener(languageManager = new ServerLanguageManager());
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            playerSkillList.addPlayer((ServerPlayer) event.getEntity());
            sendLocalizations((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onReload(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            playerSkillList.reload();
            reloadLocalizations();
        }
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer)
            playerSkillList.remove((ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == Phase.END)
            if (event.player instanceof ServerPlayer)
                onPlayerTick((ServerPlayer) event.player);
    }

    private void onPlayerTick(ServerPlayer player) {
        getPlayerSkills(player).flushDirty();
    }

    public static Iterable<PlayerSkills> getAllPlayerSkills() {
        List<ServerPlayer> players = List.copyOf(server.getPlayerList().getPlayers());
        List<PlayerSkills> playerSkills = List.of();
        players.forEach(player -> {
            PlayerSkills skills = getPlayerSkills(player);
            if (skills != null)
                playerSkills.add(skills);
        });
        return playerSkills;
    }

    public static ServerLanguageManager getLanguageManager() {
        return languageManager;
    }

    public static PlayerSkillList getPlayerSkillList() {
        return playerSkillList;
    }

    public static ServerSkillManager getSkillManager() {
        return skillManager;
    }

    public static PlayerSkills getPlayerSkills(ServerPlayer player) {
        return playerSkillList.get(player);
    }

    public static int getSkillPointBalance(ServerPlayer player) {
        PlayerSkills playerSkills = getPlayerSkills(player);
        if (playerSkills == null)
            return -1;
        return getPlayerSkills(player).getSkillPoints();
    }

    private void reloadLocalizations() {
        if (CONFIG.syncLocalizations.get() && languageManager.isDirty()) {
            CHANNEL.sendToAll(languageManager.getUpdatePacket(true));
            languageManager.flush();
        }
    }

    private static void sendLocalizations(ServerPlayer player) {
        if (CONFIG.syncLocalizations.get())
            CHANNEL.sendPacketTo(player, languageManager.getUpdatePacket(true));
    }

    public static boolean addPoints(ServerPlayer player, int amount) {
        PlayerSkills pSkills = getPlayerSkills(player);
        if (pSkills == null)
            return false;
        return pSkills.addSkillPoints(amount);
    }

    public static boolean setPoints(ServerPlayer player, int amount) {
        PlayerSkills pSkills = getPlayerSkills(player);
        if (pSkills == null)
            return false;
        return pSkills.setSkillPoints(amount);
    }

    public static void processSkillPacket(ServerPlayer sender, ServerboundClientSkillPacket packet) {
        PlayerSkills skills = getPlayerSkills(sender);
        if (skills != null)
            skills.process(packet);
    }

}