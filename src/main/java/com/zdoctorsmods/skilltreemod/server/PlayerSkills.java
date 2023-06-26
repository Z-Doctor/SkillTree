package com.zdoctorsmods.skilltreemod.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.zdoctorsmods.skilltreemod.SkillTreeMod;
import com.zdoctorsmods.skilltreemod.network.packets.ClientboundUpdateSkillsPacket;
import com.zdoctorsmods.skilltreemod.skills.Skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSkills {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .setPrettyPrinting()
            .create();
    // .registerTypeAdapter(SkillProgress.class, new SkillProgress.Serializer())
    // .registerTypeAdapter(ResourceLocation.class,
    // new ResourceLocation.Serializer())
    // .setPrettyPrinting().create();
    // private static final TypeToken<Map<ResourceLocation, SkillProgress>>
    // TYPE_TOKEN = new TypeToken<Map<ResourceLocation, SkillProgress>>() {
    // };
    private int skillPoints;
    private final Set<Skill> visibleSkills = Sets.newLinkedHashSet();
    private final Set<Skill> visibilityChanged = Sets.newLinkedHashSet();
    private final File skillFile;
    private ServerPlayer player;
    private Skill lastSelectedTab;
    private boolean isFirstPacket = true;
    private boolean isDirty;

    public PlayerSkills(ServerPlayer player, File skillFile) {
        this.player = player;
        this.skillFile = skillFile;
        this.load();
    }

    public void setPlayer(ServerPlayer pPlayer) {
        this.player = pPlayer;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public boolean addSkillPoints(int amount) {
        if (amount == 0)
            return false;
        skillPoints += amount;
        skillPoints = Math.max(0, skillPoints);
        setDirty();
        return true;
    }

    public boolean setSkillPoints(int amount) {
        if (amount == this.skillPoints)
            return false;
        this.skillPoints = amount;
        setDirty();
        return true;
    }

    public void setDirty() {
        isDirty = true;
    }

    public void stopListening() {

    }

    public void reload() {
        this.stopListening();
        this.visibleSkills.clear();
        this.visibilityChanged.clear();
        this.isFirstPacket = true;
        this.lastSelectedTab = null;
        this.load();
    }

    public void flushDirty() {
        if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || this.isDirty) {
            Set<Skill> addedSkills = Sets.newLinkedHashSet();
            Set<ResourceLocation> removedSkills = Sets.newLinkedHashSet();

            for (Skill skill : this.visibilityChanged) {
                if (this.visibleSkills.contains(skill)) {
                    addedSkills.add(skill);
                } else {
                    removedSkills.add(skill.getId());
                }
            }

            if (this.isFirstPacket || !addedSkills.isEmpty() || !removedSkills.isEmpty()) {
                SkillTreeMod.CHANNEL.sendPacketTo(player,
                        new ClientboundUpdateSkillsPacket(isFirstPacket, addedSkills, removedSkills, skillPoints));
                this.visibilityChanged.clear();
            }
        }

        this.isFirstPacket = false;
    }

    public void save() {
        // Map<ResourceLocation, SkillProgress> map = Maps.newHashMap();

        // for (Map.Entry<Skill, SkillProgress> entry : this.skills.entrySet()) {
        // SkillProgress skillProgress = entry.getValue();
        // if (skillProgress.hasProgress()) {
        // map.put(entry.getKey().getId(), skillProgress);
        // }
        // }

        if (!this.skillFile.getParentFile().exists()) {
            this.skillFile.getParentFile().mkdirs();
        }

        // JsonElement jsonelement = GSON.toJsonTree(visibleSkills);
        try (
                OutputStream outputstream = new FileOutputStream(this.skillFile);
                Writer writer = new OutputStreamWriter(outputstream,
                        Charsets.UTF_8.newEncoder());) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("skill_points", skillPoints);
            writer.write(GSON.toJson(jsonObject));
        } catch (

        IOException ioexception) {
            LOGGER.error("Couldn't save player skills to {}", this.skillFile, ioexception);
        }
    }

    private void load() {
        for (Skill skill : SkillTreeMod.getSkillManager().getAllSkills()) {
            LOGGER.debug("Loading skill {} to player {}", skill.getId(), player.getName());
            visibleSkills.add(skill);
            visibilityChanged.add(skill);
        }
        // TODO Load obtained skills from file
        if (skillFile.isFile()) {
            try {
                JsonObject jsonObject = JsonParser.parseString(Files.asCharSource(skillFile,
                        StandardCharsets.UTF_8).read()).getAsJsonObject();

                if (jsonObject.has("skill_points"))
                    skillPoints = jsonObject.get("skill_points").getAsInt();
                // Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE,
                // Streams.parse(jsonreader));

                // Set<Skill> set = (Set<Skill>)
                // GSON.getAdapter(SET_SKILL_TOKEN2).fromJsonTree(dynamic.getValue());
                // if (set == null) {
                // throw new JsonParseException("Found null for skills");
                // }

                // // Stream<Map.Entry<ResourceLocation, SkillProgress>> stream =
                // // map.entrySet().stream();
                // // .sorted(Comparator.comparing(Map.Entry::getValue));

                // for (Skill skillData : set) {
                // Skill skill = ModMain.getSkillManager().getSkill(skillData.getId());
                // if (skill == null) {
                // ModMain.LOGGER.warn("Ignored skill '{}' in progress file {} - it doesn't
                // exist anymore?",
                // skillData.getId(), skillFile);
                // }
                // }
            } catch (JsonParseException jsonparseexception) {
                LOGGER.error("Couldn't parse player skill in {}", skillFile, jsonparseexception);
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't access player skill in {}", skillFile, ioexception);
            }
        }
    }
}
