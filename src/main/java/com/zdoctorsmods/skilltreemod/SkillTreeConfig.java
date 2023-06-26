package com.zdoctorsmods.skilltreemod;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class SkillTreeConfig {
    private static SkillTreeConfig instance;

    private ForgeConfigSpec forgeConfig;
    public final ConfigValue<Boolean> syncLocalizations;

    SkillTreeConfig(ForgeConfigSpec.Builder builder) {
        syncLocalizations = builder.comment(Constants.SYNC_LOCAL_COMMENT)
                .translation(Constants.SYNC_LOCAL_TRANSLATION)
                .define(Constants.SYNC_LOCAL_KEY, true);

    }

    static {
        Pair<SkillTreeConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(SkillTreeConfig::new);
        instance = pair.getLeft();
        instance.forgeConfig = pair.getRight();
    }

    public static SkillTreeConfig get() {
        return instance;
    }

    public ForgeConfigSpec getForgeConfig() {
        return forgeConfig;
    }

    public static class Constants {
        public static final String SYNC_LOCAL_COMMENT = "Send the player localizations found in data packs and sync changes when reloaded";
        public static final String SYNC_LOCAL_TRANSLATION = "config.server.sync_localizations";
        public static final String SYNC_LOCAL_KEY = "server_sync_localizations";
    }
}
