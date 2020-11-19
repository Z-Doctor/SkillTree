// TODO Add support for obtaining skills to trigger advancements
//package zdoctor.zskilltree.extra;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonNull;
//import com.google.gson.JsonObject;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.util.JSONUtils;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.world.storage.loot.LootContext;
//import net.minecraft.world.storage.loot.LootParameter;
//import zdoctor.mcskilltree.McSkillTree;
//import zdoctor.mcskilltree.api.ISkillHandler;
//import zdoctor.mcskilltree.api.ISkillProperty;
//import zdoctor.mcskilltree.api.SkillApi;
//import zdoctor.mcskilltree.registries.SkillTreeRegistries;
//import zdoctor.mcskilltree.skills.Skill;
//import zdoctor.mcskilltree.skills.properties.SkillTierProperty;
//
//import java.util.*;
//
//public class SkillPredicate {
//    private static final Map<ResourceLocation, ISkillProperty> custom_properties = new HashMap<>();
////    private static final Map<ResourceLocation, Object> unmod_elements = Collections.unmodifiableMap(custom_elements);
//
//    private final ISkillProperty[] properties;
//
//    public static final SkillPredicate ANY = new SkillPredicate();
//
//    static {
//        SkillPredicate.register(new ResourceLocation("tier"), SkillTierProperty.UNBOUNDED);
//    }
//
//    private final Skill skill;
//    private final LootContext.EntityTarget source;
//
//    public SkillPredicate() {
//        skill = null;
//        source = null;
//        properties = null;
//    }
//
//    public SkillPredicate(Skill skill, LootContext.EntityTarget source, ISkillProperty[] properties) {
//        this.skill = skill;
//        this.source = source;
//        this.properties = properties;
//    }
//
//    public static void register(ResourceLocation name, ISkillProperty property) {
//        custom_properties.put(name, property);
//    }
//
//    public boolean test(LivingEntity entity) {
//        ISkillHandler handler = SkillApi.getSkillHandler(entity);
//        if (handler == ISkillHandler.EMPTY)
//            return false;
//        else if (this == ANY)
//            return true;
//        else if (!handler.hasSkill(skill))
//            return false;
//        else if (properties == null)
//            return true;
//
//        return Arrays.stream(properties).allMatch(property -> property.test(skill, handler));
//    }
//
//    public LootParameter<? extends Entity> getSource() {
//        return source != null ? source.getParameter() : null;
//    }
//
//    public static SkillPredicate deserialize(JsonElement element) {
//        if (element != null && !element.isJsonNull()) {
//            JsonObject jsonObject = JSONUtils.getJsonObject(element, "skill");
//
//            ResourceLocation skillLocation = new ResourceLocation(jsonObject.get("skill").getAsString());
//            Skill skill = SkillTreeRegistries.SKILLS.getValue(skillLocation);
//
//            if (skill == null)
//                return ANY;
//
//            LootContext.EntityTarget source = LootContext.EntityTarget.fromString(jsonObject.get("entity").getAsString());
//
//            if (!jsonObject.has("properties"))
//                return new SkillPredicate(skill, source, null);
//
//            List<ISkillProperty> properties = new ArrayList<>();
//
//            JsonArray jsonArray = JSONUtils.getJsonArray(jsonObject.get("properties"), "properties_array");
//            if (!jsonArray.isJsonNull()) {
//                for (JsonElement jsonElement : jsonArray) {
//                    JsonObject propObject = JSONUtils.getJsonObject(jsonElement, "properties_element");
//                    for (ResourceLocation location : custom_properties.keySet()) {
//                        String key;
//                        if (location.getNamespace().equalsIgnoreCase("minecraft") ||
//                                location.getNamespace().equalsIgnoreCase(McSkillTree.MODID))
//                            key = location.getPath();
//                        else
//                            key = location.toString();
//                        if (!propObject.has(key))
//                            continue;
//                        properties.add(custom_properties.get(location).deserialize(propObject));
//                    }
//                }
//            }
//
//            return new SkillPredicate(skill, source, properties.toArray(new ISkillProperty[0]));
//        } else {
//            return ANY;
//        }
//    }
//
//    public JsonElement serialize() {
//        if (this == ANY) {
//            return JsonNull.INSTANCE;
//        } else {
//            JsonObject jsonobject = new JsonObject();
//            if (skill != null) {
//                jsonobject.addProperty("skill", Objects.requireNonNull(skill.getRegistryName()).toString());
//            }
//
//            if (source != null) {
//                jsonobject.addProperty("entity", source.toString());
//            }
//
//            if (custom_properties.isEmpty() || properties == null)
//                return jsonobject;
//
//            JsonArray jsonArray = new JsonArray();
//
//            for (ISkillProperty property : properties) {
//                jsonArray.add(property.serialize());
//            }
//            if (jsonArray.size() > 0)
//                jsonobject.add("properties", jsonArray);
//            return jsonobject;
//        }
//    }
//
//}
