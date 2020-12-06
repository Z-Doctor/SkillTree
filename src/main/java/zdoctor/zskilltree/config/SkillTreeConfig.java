package zdoctor.zskilltree.config;

import net.minecraft.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static zdoctor.zskilltree.ModMain.SKILLTREEMOD;

public class SkillTreeConfig {
    public static final SkillTreeConfig.Client CLIENT;
    public static final SkillTreeConfig.Server SERVER;
    public static final ForgeConfigSpec clientSpec;
    public static final ForgeConfigSpec serverSpec;
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        final Pair<SkillTreeConfig.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(SkillTreeConfig.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    static {
        final Pair<SkillTreeConfig.Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(SkillTreeConfig.Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        if (SERVER.whitelistOnly.get()) {
            LOGGER.info(SKILLTREEMOD, "Whitelist only mode, found {} entries in whitelist", SERVER.whitelist.get().size());
            SERVER.whitelist.get().forEach(SkillTreeConfig::validateType);
        } else {
            LOGGER.info(SKILLTREEMOD, "Found {} entries in blacklist", SERVER.blacklist.get().size());
            SERVER.blacklist.get().forEach(SkillTreeConfig::validateType);
        }
    }

    private static void validateType(String type) {
        if (EntityType.byKey(type).isPresent())
            LOGGER.debug(SKILLTREEMOD, type);
        else
            LOGGER.error(SKILLTREEMOD, "Could not validate entity type {}", type);
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

        Server(ForgeConfigSpec.Builder builder) {
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
                    .worldRestart() // TODO? Might be able to make the changes instant in the cap getter
                    .defineList("capability.whitelist", new ArrayList<>(), o -> o instanceof String);

            blacklist = builder
                    .comment("Specify specific entities that should not have skill tree capabilities attached to them")
                    .translation("skilltree.blacklist")
                    .worldRestart() // TODO? Might be able to make the changes instant in the cap getter
                    .defineList("capability.blacklist", new ArrayList<>(), o -> o instanceof String);

            builder.pop();
        }
    }

    public static class Client {
        Client(ForgeConfigSpec.Builder builder) {
//            builder.comment("Client only settings, mostly things related to rendering")
//                    .push("client");
        }
    }
}
