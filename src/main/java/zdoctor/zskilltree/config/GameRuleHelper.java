package zdoctor.zskilltree.config;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import java.util.function.BiConsumer;

public class GameRuleHelper {
    public static GameRules.RuleType<GameRules.BooleanValue> create(boolean defaultValue) {
        return BooleanValue.create(defaultValue);
    }

    public static GameRules.RuleType<GameRules.BooleanValue> create(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changeListener) {
        return BooleanValue.create(defaultValue, changeListener);
    }

    public static GameRules.RuleType<GameRules.IntegerValue> create(int defaultValue) {
        return IntegerValue.create(defaultValue);
    }

    public static GameRules.RuleType<GameRules.IntegerValue> create(int min, int max) {
        return IntegerValue.create(min, max);
    }

    public static GameRules.RuleType<GameRules.IntegerValue> create(int min, int max, int defaultValue) {
        return IntegerValue.create(min, max, defaultValue);
    }

    public static GameRules.RuleType<GameRules.IntegerValue> create(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changeListener) {
        return IntegerValue.create(defaultValue, changeListener);
    }

    public static class BooleanValue {
        public static GameRules.RuleType<GameRules.BooleanValue> create(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changeListener) {
            return new GameRules.RuleType<>(BoolArgumentType::bool, (type) -> new GameRules.BooleanValue(type, defaultValue),
                    changeListener, GameRules.IRuleEntryVisitor::changeBoolean);
        }

        public static GameRules.RuleType<GameRules.BooleanValue> create(boolean defaultValue) {
            return create(defaultValue, (server, value) -> {
            });
        }
    }

    public static class IntegerValue {
        public static GameRules.RuleType<GameRules.IntegerValue> create(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changeListener) {
            return new GameRules.RuleType<>(IntegerArgumentType::integer, (type) -> new GameRules.IntegerValue(type, defaultValue),
                    changeListener, GameRules.IRuleEntryVisitor::changeInteger);
        }

        public static GameRules.RuleType<GameRules.IntegerValue> create(int min, int max, BiConsumer<MinecraftServer, GameRules.IntegerValue> changeListener) {
            return new GameRules.RuleType<>(() -> IntegerArgumentType.integer(min, max), (type) -> new GameRules.IntegerValue(type, min),
                    changeListener, GameRules.IRuleEntryVisitor::changeInteger);
        }

        public static GameRules.RuleType<GameRules.IntegerValue> create(int min, int max, int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changeListener) {
            return new GameRules.RuleType<>(() -> IntegerArgumentType.integer(min, max), (type) -> new GameRules.IntegerValue(type, defaultValue),
                    changeListener, GameRules.IRuleEntryVisitor::changeInteger);
        }

        public static GameRules.RuleType<GameRules.IntegerValue> create(int defaultValue) {
            return create(defaultValue, (server, value) -> {
            });
        }

        public static GameRules.RuleType<GameRules.IntegerValue> create(int min, int max) {
            return create(min, max, (server, value) -> {
            });
        }

        public static GameRules.RuleType<GameRules.IntegerValue> create(int min, int max, int defaultValue) {
            return create(min, max, defaultValue, (server, value) -> {
            });
        }
    }
}
