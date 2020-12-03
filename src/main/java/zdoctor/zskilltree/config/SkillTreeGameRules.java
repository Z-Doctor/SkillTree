package zdoctor.zskilltree.config;

import net.minecraft.world.GameRules;
import zdoctor.zskilltree.network.play.server.SUpdatePausePacket;
import zdoctor.zskilltree.skilltree.managers.SkillTreeDataManager;

public class SkillTreeGameRules {
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_SKILL_TREE_PAUSE = GameRules.register("doSkillTreePause",
            GameRules.Category.PLAYER, GameRuleHelper.create(true, SUpdatePausePacket::onChanged));

    public static final GameRules.RuleKey<GameRules.IntegerValue> SKILL_TREE_UPDATE_TICKS = GameRules.register("skillTreeUpdateTicks",
            GameRules.Category.PLAYER, GameRuleHelper.IntegerValue.create(1, Integer.MAX_VALUE, 10, SkillTreeDataManager::onChanged));

    public static final GameRules.RuleKey<GameRules.BooleanValue> KEEP_SKILLS_ON_DEATH = GameRules.register("keepSkillsOnDeath",
            GameRules.Category.PLAYER, GameRuleHelper.create(true));

    public static void init() {
    }
}
