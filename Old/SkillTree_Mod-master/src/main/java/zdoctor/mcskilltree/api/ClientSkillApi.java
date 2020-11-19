package zdoctor.mcskilltree.api;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.mcskilltree.skills.Skill;

@OnlyIn(Dist.CLIENT)
public class ClientSkillApi {
    protected static ISkillHandler playerHandler;

    public static ISkillHandler getPlayerHandler() {
        return playerHandler;
    }

    public static LivingEntity getPlayer() {
        return playerHandler.getOwner();
    }

    public static void update(ISkillHandler newSkillHandler) {
        playerHandler = newSkillHandler;
    }

    public static boolean hasSkill(Skill skill) {
        return playerHandler.hasSkill(skill);
    }

    public static int getTier(Skill skill) {
        return playerHandler.getTier(skill);
    }

    public static int getSkillPoints() {
        return playerHandler.getSkillPoints();
    }

    public static boolean hasRequirements(Skill skill) {
        return playerHandler.hasRequirements(skill);
    }
}
