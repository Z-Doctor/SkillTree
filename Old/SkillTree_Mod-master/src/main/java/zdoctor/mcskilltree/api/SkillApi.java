package zdoctor.mcskilltree.api;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.skills.Skill;

public class SkillApi {
    @CapabilityInject(ISkillHandler.class)
    protected static Capability<ISkillHandler> SKILL_CAPABILITY = null;

    public static ISkillHandler getSkillHandler(LivingEntity entity) {
        LazyOptional<ISkillHandler> cap = entity.getCapability(SKILL_CAPABILITY);
        if (!cap.isPresent()) {
            // TODO Config to disable this message
            McSkillTree.LOGGER.debug("{} does not have skill capability or you tried to access capability before it got attached to entity.", entity);
        }
        ISkillHandler skillHandler = cap.orElse(ISkillHandler.EMPTY);
        if (skillHandler.getOwner() == null)
            skillHandler.setOwner(entity);

        return skillHandler;
    }

    public static boolean hasRequirements(LivingEntity entity, Skill skill) {
        ISkillHandler skillHandler = getSkillHandler(entity);
        return skillHandler.hasRequirements(skill);
    }

    public static boolean hasSkill(LivingEntity entity, Skill skill) {
        ISkillHandler skillHandler = getSkillHandler(entity);
        return skillHandler.hasSkill(skill);
    }

    public static boolean buySkill(LivingEntity entity, Skill skill) {
        ISkillHandler skillHandler = getSkillHandler(entity);
        return skillHandler.buySkill(skill);
    }

    public static void updateSkillData(LivingEntity entity) {
        ISkillHandler skillHandler = getSkillHandler(entity);
        skillHandler.updateSkillData();
    }

    public static int getTier(LivingEntity entity, Skill skill) {
        ISkillHandler skillHandler = getSkillHandler(entity);
        return skillHandler.getTier(skill);
    }

    public static boolean addSkillPoints(LivingEntity entity, int amount) {
        ISkillHandler skillHandler = getSkillHandler(entity);
        return skillHandler.addSkillPoints(amount);
    }

    public static boolean deductSkillPoints(LivingEntity entity, int amount, boolean force) {
        ISkillHandler skillHandler = getSkillHandler(entity);
        return skillHandler.deductSkillPoints(amount, force);
    }

    public static int getSkillPoints(LivingEntity entity) {
        ISkillHandler skillHandler = getSkillHandler(entity);
        return skillHandler.getSkillPoints();
    }
}
