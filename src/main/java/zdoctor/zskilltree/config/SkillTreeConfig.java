package zdoctor.zskilltree.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SkillTreeConfig {

    public static final SkillTreeConfig.Client CLIENT;
    public static final SkillTreeConfig.Server SERVER;
    public static final ForgeConfigSpec clientSpec;
    public static final ForgeConfigSpec serverSpec;

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

    public static class Server {
//        public final ForgeConfigSpec.BooleanValue

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                    .push("server");
        }
    }

    public static class Client {
        Client(ForgeConfigSpec.Builder builder) {
//            builder.comment("Client only settings, mostly things related to rendering")
//                    .push("client");
        }
    }
}
