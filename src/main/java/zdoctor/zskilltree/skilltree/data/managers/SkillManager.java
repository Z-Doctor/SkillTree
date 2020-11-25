package zdoctor.zskilltree.skilltree.data.managers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.skilltree.data.builders.SkillBuilder;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO Add hard coded way to add more(?) perhaps through an event
public class SkillManager extends JsonReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).create();
    private final HashMap<ResourceLocation, SkillBuilder> toBuild = new HashMap<>();
    private final HashMap<ResourceLocation, Skill> skills = new HashMap<>();
    private final LootPredicateManager lootPredicateManager;

    public SkillManager(LootPredicateManager lootPredicateManager) {
        super(GSON, "skills");
        this.lootPredicateManager = lootPredicateManager;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        objectIn.forEach((location, page) -> {
            LOGGER.info("Opened: " + getPreparedPath(location));
            JsonObject jsonobject = JSONUtils.getJsonObject(page, location.toString());
            // TODO Get parent from location(?)
//            Skill skill = Skill.deserialize(location, jsonobject, new ConditionArrayParser(location, this.lootPredicateManager));
            SkillBuilder builder = Skill.Builder.deserialize(jsonobject, new ConditionArrayParser(location, this.lootPredicateManager));
            toBuild.put(location, builder);
            LOGGER.info("Created Skill {}", location);
        });
    }

    public void build(SkillPageManager skillPageManager) {
        for (Map.Entry<ResourceLocation, SkillBuilder> entry : toBuild.entrySet()) {
            ResourceLocation id = entry.getKey();
            SkillBuilder builder = entry.getValue();
            SkillPage page = skillPageManager.getPage(builder.getPageId());
            if (page == null && (page = resolveParent(id, skillPageManager)) != null)
                builder.onPage(page);
            else {
                LOGGER.info("Skipping: Unable to find parent {} for skill {}", builder.getPageId(), id);
                continue;
            }
            Skill skill = builder.build(id);
            page.addSkill(skill);
            LOGGER.info("Built Skill {} and sent to {}", id, page.getRegistryName());
            skills.put(id, skill);
        }
        toBuild.clear();
    }

    public SkillPage resolveParent(ResourceLocation location, SkillPageManager skillPageManager) {
        SkillPage page = null;
        String path = location.getPath();
        String[] parents = path.substring(0, path.lastIndexOf('/')).split("/");
        for (String parent : parents) {
            page = skillPageManager.getPage(new ResourceLocation(location.getNamespace(), parent));
            if (page != null)
                break;
        }
        return page;
    }

    public Map<ResourceLocation, Skill> getAllEntries() {
        return ImmutableMap.copyOf(skills);
    }

    public Collection<Skill> getAllSkills() {
        return ImmutableSet.copyOf(skills.values());
    }

    public Skill getSkill(ResourceLocation id) {
        return skills.get(id);
    }
}
