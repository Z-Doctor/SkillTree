package com.zdoctorsmods.skilltreemod.server;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.zdoctorsmods.skilltreemod.data.skilltrees.events.RegisterSkillEvent;
import com.zdoctorsmods.skilltreemod.skills.Skill;
import com.zdoctorsmods.skilltreemod.skills.SkillList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;

public class ServerSkillManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = (new GsonBuilder()).create();
    private SkillList skills = new SkillList();

    public ServerSkillManager(PredicateManager predicateManager) {
        super(GSON, "skills");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager,
            ProfilerFiller pProfiler) {
        Map<ResourceLocation, Skill.Builder> map = Maps.newHashMap();
        LOGGER.debug("Looking for Skills");
        pObject.forEach((location, element) -> {
            try {
                JsonObject jsonobject = GsonHelper.convertToJsonObject(element, "skill");

                LOGGER.debug("Found skill {}", location);
                Skill.Builder builder = Skill.Builder.fromJson(jsonobject);
                if (builder == null) {
                    LOGGER.debug("Skipping loading skill {} as it's conditions were not met", location);
                    return;
                }
                map.put(location, builder);
            } catch (Exception exception) {
                LOGGER.error("Parsing error skill trees {}: {}", location,
                        exception.getMessage());
            }
        });
        SkillList skillList = new SkillList();
        skillList.add(map);
        MinecraftForge.EVENT_BUS.post(new RegisterSkillEvent(LogicalSide.SERVER, skillList));
        this.skills = skillList;
    }

    public Skill getSkill(ResourceLocation skillId) {
        return skills.get(skillId);
    }

    public Collection<Skill> getAllSkills() {
        return skills.getAllSkills();
    }
}
