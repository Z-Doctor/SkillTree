package zdoctor.zskilltree.config;

import net.minecraft.world.GameRules;
import zdoctor.zskilltree.network.play.server.SUpdatePausePacket;

public class SkillTreeGameRules {
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_SKILL_TREE_PAUSE = GameRules.register("doSkillTreePause",
            GameRules.Category.PLAYER, GameRuleHelper.create(true, SUpdatePausePacket::onChanged));

    public static final GameRules.RuleKey<GameRules.BooleanValue> KEEP_SKILLS_ON_DEATH = GameRules.register("keepSkillsOnDeath",
            GameRules.Category.PLAYER, GameRuleHelper.create(true));

    public static void init() {
    }
}
