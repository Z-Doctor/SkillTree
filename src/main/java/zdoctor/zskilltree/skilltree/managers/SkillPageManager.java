package zdoctor.zskilltree.skilltree.managers;

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
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.skilltree.criterion.SkillPage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SkillPageManager extends JsonReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).create();
    private final SkillManager skillManager;
    private final LootPredicateManager lootPredicateManager;
    private HashMap<ResourceLocation, SkillPage> pages = new HashMap<>();

    public SkillPageManager(SkillManager skillManager, LootPredicateManager lootPredicateManager) {
        super(GSON, "pages");
        this.skillManager = skillManager;
        this.lootPredicateManager = lootPredicateManager;
        reset();
    }

    public SkillPage getPage(ResourceLocation id) {
        return pages.get(id);
    }

    public void reset() {
        pages.clear();
        GameRegistry.findRegistry(SkillPage.class).getValues().forEach(page -> pages.put(page.getRegistryName(), page));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        reset();
        HashMap<ResourceLocation, SkillPage> newPages = new HashMap<>();

        for (SkillPage page : GameRegistry.findRegistry(SkillPage.class).getValues())
            newPages.put(page.getRegistryName(), page);


        objectIn.forEach((location, page) -> {
            try {
                LOGGER.info("Opened: {}", getPreparedPath(location));
                JsonObject jsonobject = JSONUtils.getJsonObject(page, "skill page");

                SkillPage skillPage = SkillPage.deserialize(location, jsonobject, new ConditionArrayParser(location, this.lootPredicateManager));
                newPages.put(location, skillPage);
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
