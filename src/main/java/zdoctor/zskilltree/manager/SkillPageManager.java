package zdoctor.zskilltree.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.skill.SkillTreeDataManager;
import zdoctor.zskilltree.skillpages.SkillPage;

import java.util.*;

// TODO Add hard coded way to add more(?) perhaps through an event
// TODO Make it so new pages don't override the last page for values not defined by default
// TODO Make a registry for skill pages for hard coded ones
//  as well as support for the perhaps also make it so new ones will be registered sing deferred
public class SkillPageManager extends JsonReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).create();
    private final SkillManager skillManager;
    private final LootPredicateManager lootPredicateManager;
    private HashMap<ResourceLocation, SkillPage> pages = new HashMap<>();
    private SkillTreeDataManager skillTreeDataManager;

    public SkillPageManager(SkillManager skillManager, LootPredicateManager lootPredicateManager) {
        super(GSON, "pages");
        this.skillManager = skillManager;
        this.lootPredicateManager = lootPredicateManager;
        reset();
    }

    public SkillPage getPage(ResourceLocation id) {
        return pages.get(id);
    }

    public void setPlayerSkillDataManager(SkillTreeDataManager skillTreeDataManager) {
        this.skillTreeDataManager = skillTreeDataManager;
    }

    public void reset() {
        pages.clear();
        pages.put(SkillPage.NONE.getId(), SkillPage.NONE);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        reset();
        HashMap<ResourceLocation, SkillPage> newPages = new HashMap<>();
        objectIn.forEach((location, page) -> {
            try {
                LOGGER.info("Opened: {}", getPreparedPath(location));
                JsonObject jsonobject = JSONUtils.getJsonObject(page, "skill page");

                SkillPage.Builder builder = SkillPage.Builder.deserialize(jsonobject, new ConditionArrayParser(location, this.lootPredicateManager));
                newPages.put(location, builder.build(location));
                LOGGER.info("Added page {}", location);
            } catch (IllegalArgumentException | JsonParseException exception) {
                LOGGER.error("Parsing error loading custom skill page {}: {}", location, exception.getMessage());
            }
        });

        pages = newPages;
        skillManager.build(this);
    }

    public Collection<SkillPage> getAllSkillPages() {
        return ImmutableSet.copyOf(pages.values());
    }

    public Map<ResourceLocation, SkillPage> getAllEntries() {
        return ImmutableMap.copyOf(pages);
    }




}
