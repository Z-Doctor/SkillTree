package com.zdoctorsmods.skilltreemod.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import com.zdoctorsmods.skilltreemod.SkillTree;
import com.zdoctorsmods.skilltreemod.network.packets.ClientboundUpdateSkillsPacket;
import com.zdoctorsmods.skilltreemod.network.packets.ServerboundClientSkillPacket;
import com.zdoctorsmods.skilltreemod.skills.Skill;
import com.zdoctorsmods.skilltreemod.skills.SkillAction;
import com.zdoctorsmods.skilltreemod.skills.SkillProgress;
import com.zdoctorsmods.skilltreemod.skills.loot.critereon.SkillCriterionTriggerListener;
import com.zdoctorsmods.skilltreemod.skills.loot.critereon.SkillTriggers;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraftforge.common.util.FakePlayer;

public class PlayerSkills {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(SkillProgress.class, new SkillProgress.Serializer())
            .setPrettyPrinting()
            .create();
    // .registerTypeAdapter(SkillProgress.class, new SkillProgress.Serializer())
    // .registerTypeAdapter(ResourceLocation.class,
    // new ResourceLocation.Serializer())
    // .setPrettyPrinting().create();
    private static final TypeToken<Map<ResourceLocation, SkillProgress>> TYPE_TOKEN = new TypeToken<>() {
    };
    private int skillPoints;
    private final Map<Skill, SkillProgress> skills = new LinkedHashMap<>();
    private final Set<Skill> visibleSkills = new LinkedHashSet<>();
    private final Set<Skill> visibilityChanged = new LinkedHashSet<>();
    private final Set<Skill> progressChanged = new LinkedHashSet<>();
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

    private void startProgress(Skill skill, SkillProgress pProgress) {
        pProgress.update(skill.getCriteria(), skill.getRequirements());
        this.skills.put(skill, pProgress);
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

    public boolean award(Skill skill, String pCriterionKey) {
        if (this.player instanceof FakePlayer)
            return false;

        boolean awarded = false;
        SkillProgress skillProgress = this.getOrStartProgress(skill);
        boolean alreadyAwarded = skillProgress.isDone();
        if (skillProgress.grantProgress(pCriterionKey)) {
            this.unregisterListeners(skill);
            this.progressChanged.add(skill);
            awarded = true;
            // net.minecraftforge.event.ForgeEventFactory.onAdvancementProgressedEvent(this.player,
            // pAdvancement,
            // skillProgress, pCriterionKey,
            // net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent.ProgressType.GRANT);
            if (!alreadyAwarded && skillProgress.isDone()) {
                // pAdvancement.getRewards().grant(this.player);
            }
            // net.minecraftforge.common.ForgeHooks.onAdvancement(this.player,
            // pAdvancement);
            // net.minecraftforge.event.ForgeEventFactory.onAdvancementEarnedEvent(this.player,
            // pAdvancement);
        }
        return awarded;
    }

    public boolean revoke(Skill skill, String pCriterionKey) {
        boolean flag = false;
        SkillProgress skillProgress = this.getOrStartProgress(skill);
        if (skillProgress.revokeProgress(pCriterionKey)) {
            this.registerListeners(skill);
            this.progressChanged.add(skill);
            flag = true;
            // net.minecraftforge.event.ForgeEventFactory.onAdvancementProgressedEvent(this.player,
            // skill,
            // skillProgress, pCriterionKey,
            // net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent.ProgressType.REVOKE);
        }

        // if (!skillProgress.hasProgress()) {
        // this.ensureVisibility(skill);
        // }

        return flag;
    }

    public void setDirty() {
        isDirty = true;
    }

    public void reload() {
        save();
        this.stopListening();
        this.skills.clear();
        this.visibleSkills.clear();
        this.visibilityChanged.clear();
        this.progressChanged.clear();
        this.isFirstPacket = true;
        this.lastSelectedTab = null;
        this.load();
    }

    public void flushDirty() {
        if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()
                || this.isDirty) {
            Set<Skill> addedSkills = new LinkedHashSet<>();
            Set<ResourceLocation> removedSkills = new LinkedHashSet<>();
            Map<ResourceLocation, SkillProgress> changedSkills = new HashMap<>();

            for (Skill skill : this.visibilityChanged) {
                if (this.visibleSkills.contains(skill)) {
                    addedSkills.add(skill);
                } else {
                    removedSkills.add(skill.getId());
                }
            }

            for (Skill skill : this.progressChanged) {
                if (this.visibleSkills.contains(skill)) {
                    changedSkills.put(skill.getId(), skills.get(skill));
                }
            }

            SkillTree.CHANNEL.sendPacketTo(player,
                    new ClientboundUpdateSkillsPacket(isFirstPacket, addedSkills, removedSkills, changedSkills,
                            skillPoints));
            this.visibilityChanged.clear();
            this.progressChanged.clear();

            isDirty = false;
        }

        this.isFirstPacket = false;
    }

    public void save() {
        Map<ResourceLocation, SkillProgress> map = new HashMap<>();

        for (Map.Entry<Skill, SkillProgress> entry : this.skills.entrySet()) {
            SkillProgress skillProgress = entry.getValue();
            if (skillProgress.hasProgress()) {
                map.put(entry.getKey().getId(), skillProgress);
            }
        }

        if (!this.skillFile.getParentFile().exists()) {
            this.skillFile.getParentFile().mkdirs();
        }
        JsonElement jsonelement = GSON.toJsonTree(map);
        try (
                OutputStream outputstream = new FileOutputStream(this.skillFile);
                Writer writer = new OutputStreamWriter(outputstream,
                        Charsets.UTF_8.newEncoder());) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("skill_points", skillPoints);
            jsonObject.add("progress", jsonelement);
            writer.write(GSON.toJson(jsonObject));
        } catch (

        IOException ioexception) {
            LOGGER.error("Couldn't save player skills to {}", this.skillFile, ioexception);
        }
    }

    private void load() {
        // for (Skill skill : SkillTree.getSkillManager().getAllSkills()) {
        // LOGGER.debug("Loading skill {} to player {}", skill.getId(),
        // player.getName());
        // visibleSkills.add(skill);
        // visibilityChanged.add(skill);
        // }
        // // TODO Load obtained skills from file
        if (skillFile.isFile()) {
            try {
                JsonObject jsonObject = JsonParser.parseString(Files.asCharSource(skillFile,
                        StandardCharsets.UTF_8).read()).getAsJsonObject();

                if (jsonObject.has("skill_points"))
                    skillPoints = jsonObject.get("skill_points").getAsInt();

                if (jsonObject.has("progress")) {
                    JsonElement progressJson = jsonObject.get("progress");
                    Map<ResourceLocation, SkillProgress> progress = GSON.getAdapter(TYPE_TOKEN)
                            .fromJsonTree(progressJson);

                    for (Map.Entry<ResourceLocation, SkillProgress> entry : progress.entrySet()) {
                        Skill skill = SkillTree.getSkillManager().getSkill(entry.getKey());
                        if (skill == null) {
                            LOGGER.warn("Ignored skill '{}' in progress file {} - it doesn't exist anymore?",
                                    entry.getKey(), this.skillFile);
                        } else {
                            this.startProgress(skill, entry.getValue());
                        }
                    }
                }

            } catch (JsonParseException jsonparseexception) {
                LOGGER.error("Couldn't parse player skill in {}", skillFile, jsonparseexception);
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't access player skill in {}", skillFile, ioexception);
            }
        }
        this.ensureAllVisible();
        this.registerListeners();
    }

    private void registerListeners() {
        for (Skill skill : SkillTree.getSkillManager().getAllSkills()) {
            this.registerListeners(skill);
        }

    }

    private void registerListeners(Skill skill) {
        SkillProgress skillProgress = this.getOrStartProgress(skill);
        if (!skillProgress.isDone()) {
            for (Map.Entry<String, Criterion> entry : skill.getCriteria().entrySet()) {
                CriterionProgress criterionprogress = skillProgress.getCriterion(entry.getKey());
                if (criterionprogress != null && !criterionprogress.isDone()) {
                    CriterionTriggerInstance criteriontriggerinstance = entry.getValue().getTrigger();
                    if (criteriontriggerinstance != null) {
                        CriterionTrigger<CriterionTriggerInstance> criteriontrigger = CriteriaTriggers
                                .getCriterion(criteriontriggerinstance.getCriterion());
                        if (criteriontrigger != null) {
                            criteriontrigger.addPlayerListener(player.getAdvancements(),
                                    new SkillCriterionTriggerListener<>(this, criteriontriggerinstance, skill,
                                            entry.getKey()));
                        }
                    }
                }
            }

        }
    }

    private void unregisterListeners(Skill skill) {
        SkillProgress skillProgress = this.getOrStartProgress(skill);

        for (Map.Entry<String, Criterion> entry : skill.getCriteria().entrySet()) {
            CriterionProgress criterionprogress = skillProgress.getCriterion(entry.getKey());
            if (criterionprogress != null && (criterionprogress.isDone() || skillProgress.isDone())) {
                CriterionTriggerInstance criteriontriggerinstance = entry.getValue().getTrigger();
                if (criteriontriggerinstance != null) {
                    CriterionTrigger<CriterionTriggerInstance> criteriontrigger = CriteriaTriggers
                            .getCriterion(criteriontriggerinstance.getCriterion());
                    if (criteriontrigger != null) {
                        criteriontrigger.removePlayerListener(player.getAdvancements(),
                                new SkillCriterionTriggerListener<>(this, criteriontriggerinstance, skill,
                                        entry.getKey()));
                    }
                }
            }
        }

    }

    // Might not be necessary because advancements may be doing this for me, may
    // also be causing problems but need to be tested
    public void stopListening() {
        for (CriterionTrigger<?> criteriontrigger : CriteriaTriggers.all()) {
            criteriontrigger.removePlayerListeners(player.getAdvancements());
        }

    }

    private void ensureAllVisible() {
        // for (Map.Entry<Skill, SkillProgress> entry : this.skills.entrySet()) {
        // if (entry.getValue().isDone()) {
        // this.progressChanged.add(entry.getKey());
        // }
        // }

        for (Skill skill : SkillTree.getSkillManager().getAllSkills()) {
            this.ensureVisibility(skill);
        }

    }

    private void ensureVisibility(Skill skill) {
        boolean shouldBeVisible = this.shouldBeVisible(skill);
        boolean isVisible = this.visibleSkills.contains(skill);
        if (shouldBeVisible && !isVisible) {
            this.visibleSkills.add(skill);
            this.visibilityChanged.add(skill);
            if (this.skills.containsKey(skill)) {
                this.progressChanged.add(skill);
            }
        } else if (!shouldBeVisible && isVisible) {
            this.visibleSkills.remove(skill);
            this.visibilityChanged.add(skill);
        }

        // if (shouldBeVisible != isVisible && skill.getParent() != null) {
        // this.ensureVisibility(skill.getParent());
        // }

        // for (Skill child : skill.getChildren()) {
        // this.ensureVisibility(child);
        // }
    }

    private boolean shouldBeVisible(Skill skill) {
        if (skill.getDisplay() == null) {
            return false;
        }

        SkillProgress skillProgress = this.getOrStartProgress(skill);
        if (skillProgress.isDone()) {
            return true;
        }

        if (skill.getDisplay().isHidden()) {
            return false;
        }

        return true;
    }

    private boolean hasCompletedChildrenOrSelf(Skill skill) {
        SkillProgress skillProgress = this.getOrStartProgress(skill);
        if (skillProgress.isDone()) {
            return true;
        } else {
            for (Skill child : skill.getChildren()) {
                if (this.hasCompletedChildrenOrSelf(child)) {
                    return true;
                }
            }

            return false;
        }
    }

    public void process(ServerboundClientSkillPacket packet) {
        if (packet.getAction() == SkillAction.BUY) {
            Skill skill = SkillTree.getSkillManager().getSkill(packet.getId());
            if (skill == null) {
                LOGGER.debug("{} sent a buy packet for an unknown skill {}", packet.getSender().getDisplayName(),
                        packet.getId());
                return;
            }
            SkillProgress progress = getOrStartProgress(skill);
            if (progress.isDone()) {
                LOGGER.debug("{} tried to buy skill {} which they already have", packet.getSender().getDisplayName(),
                        packet.getId());
                return;
            }
            Predicate<LootContext> canBuy = LootItemConditions.orConditions(skill.getPurchaseConditions());
            LootContext.Builder contextBuilder = new LootContext.Builder(player.getLevel())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withParameter(LootContextParams.ORIGIN, player.position());
            LootContext context = contextBuilder.create(LootContextParamSets.ADVANCEMENT_ENTITY);
            LOGGER.debug("{} is{}able to purchase skill.", player.getName(),
                    canBuy.test(context) ? " " : " not ");
            if (canBuy.test(context)) {
                SkillTriggers.SKILL_PURCHASED.trigger(player, skill);
            }
        }
    }

    public SkillProgress getOrStartProgress(Skill skill) {
        SkillProgress skillProgress = this.skills.get(skill);
        if (skillProgress == null) {
            skillProgress = new SkillProgress();
            this.startProgress(skill, skillProgress);
        }

        return skillProgress;
    }

    public boolean hasSkill(Skill skill) {
        if (skill == null)
            return false;
        SkillProgress progress = getOrStartProgress(skill);
        return progress.isDone();
    }
}
