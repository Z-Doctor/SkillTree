package com.zdoctorsmods.skilltreemod.server;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.zdoctorsmods.skilltreemod.SkillTree;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;

public class PlayerSkillList {

    private final MinecraftServer server;
    private final Map<UUID, PlayerSkills> skillMap = Maps.newHashMap();

    public PlayerSkillList(MinecraftServer server) {
        this.server = server;
    }

    public void addPlayer(ServerPlayer player) {
        skillMap.put(player.getUUID(), get(player));
    }

    public PlayerSkills get(ServerPlayer player) {
        if (player == null || player instanceof FakePlayer)
            return null;
        UUID uuid = player.getUUID();
        PlayerSkills playerSkills = skillMap.get(uuid);
        if (playerSkills == null) {
            File skillDir = this.server.getWorldPath(SkillTree.PLAYER_SKILLS_DIR).toFile();
            File skillFile = new File(skillDir, uuid + ".json");
            playerSkills = new PlayerSkills(player, skillFile);
            this.skillMap.put(uuid, playerSkills);
        }
        return playerSkills;
    }

    public void remove(ServerPlayer player) {
        ServerPlayer removing = server.getPlayerList().getPlayer(player.getUUID());
        if (removing == player) {
            save(player);
            skillMap.remove(player.getUUID());
        }
    }

    private void save(ServerPlayer player) {
        PlayerSkills playerSkills = skillMap.get(player.getUUID());
        if (playerSkills != null)
            playerSkills.save();
    }

    public void reload() {
        for (PlayerSkills skills : skillMap.values()) {
            skills.reload();
        }
    }
}