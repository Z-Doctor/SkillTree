package zdoctor.zskilltree.config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Predicate;

import static zdoctor.zskilltree.ModMain.SKILLTREEMOD;

public class SkillTreeConfig {
    //    public static final SkillTreeConfig.Client CLIENT;
    public static final SkillTreeConfig.Server SERVER;
    //    public static final ForgeConfigSpec clientSpec;
    public static final ForgeConfigSpec serverSpec;
    private static final Logger LOGGER = LogManager.getLogger();

//    static {
//        final Pair<SkillTreeConfig.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(SkillTreeConfig.Client::new);
//        clientSpec = specPair.getRight();
//        CLIENT = specPair.getLeft();
//    }

    static {
        final Pair<SkillTreeConfig.Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(SkillTreeConfig.Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        SERVER.generateFilter();
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.Reloading configEvent) {
        // TODO? Use
        LOGGER.debug(SKILLTREEMOD, "Skill Tree config just got changed on the file system!");
    }

    public static class Server {
        public final ForgeConfigSpec.IntValue updateTicks;
        public final ForgeConfigSpec.BooleanValue whitelistOnly;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> whitelist;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklist;

        public final Set<EntityType<?>> whiteListedTypes = new HashSet<>();
        public final Set<EntityType<?>> blacklistTypes = new HashSet<>();
        private Predicate<Entity> canAttach = entity -> true;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("If you would like to define the defaults for each created world then just copy your custom config to the defaultconfigs folder");
            builder.comment("Skill Tree configuration settings")
                    .push("server");

            updateTicks = builder
                    .comment("The amount of ticks before syncing checks between the server and client. Ticks are counted if when game is paused.")
                    .translation("skilltree.updateTicks")
                    .defineInRange("updateTicks", 10, 1, Integer.MAX_VALUE);

            whitelistOnly = builder
                    .comment("If enabled then only types found in the white list will have capabilities attached to them")
                    .translation("skilltree.whitelistOnly")
                    .worldRestart()
                    .define("capability.whitelistOnly", false);

            whitelist = builder
                    .comment("Specify specific entities that should have skill tree capabilities attached to them")
                    .translation("skilltree.whitelist")
                    .worldRestart()
                    .defineList("capability.whitelist", new ArrayList<>(), o -> o instanceof String);

            blacklist = builder
                    .comment("Specify specific entities that should not have skill tree capabilities attached to them")
                    .translation("skilltree.blacklist")
                    .worldRestart()
                    .defineList("capability.blacklist", new ArrayList<>(), o -> o instanceof String);

            builder.pop();
        }

        private static Optional<EntityType<?>> validateType(String type) {
            Optional<EntityType<?>> optionalType = EntityType.byKey(type);
            if (EntityType.byKey(type).isPresent())
                LOGGER.debug(SKILLTREEMOD, type);
            else
                LOGGER.error(SKILLTREEMOD, "Could not validate entity type {}", type);

            return optionalType;
        }

        public boolean canAttachTo(Entity entity) {
            return canAttach.test(entity);
        }

        public void generateFilter() {
            whiteListedTypes.clear();
            blacklistTypes.clear();

            if (whitelistOnly.get()) {
                LOGGER.info(SKILLTREEMOD, "Whitelist only mode, found {} entries in whitelist", whitelist.get().size());
                whitelist.get().forEach(type -> validateType(type).map(whiteListedTypes::add));
                canAttach = createFilter(whiteListedTypes, true);
            } else {
                LOGGER.info(SKILLTREEMOD, "Found {} entries in blacklist", blacklist.get().size());
                blacklist.get().forEach(type -> validateType(type).map(blacklistTypes::add));
                canAttach = createFilter(blacklistTypes, false).negate();
            }
        }

        private Predicate<Entity> createFilter(Set<EntityType<?>> filter, boolean defaultValue) {
            switch (filter.size()) {
                case 0:
                    return entity -> defaultValue;
                case 1:
                    EntityType<?> type = filter.stream().findFirst().get();
                    return entity -> type.equals(entity.getType());
                default:
                    return entity -> filter.contains(entity.getType());
            }
        }
    }

//    public static class Client {
//        Client(ForgeConfigSpec.Builder builder) {
////            builder.comment("Client only settings, mostly things related to rendering")
////                    .push("client");
//        }
//    }
}
