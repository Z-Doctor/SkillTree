package zdoctor.zskilltree.api;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.skilltree.trackers.ProgressTracker;
import zdoctor.zskilltree.skilltree.criterion.Skill;
import zdoctor.zskilltree.skilltree.criterion.SkillPage;

import java.util.Optional;
import java.util.function.Function;

public class SkillTreeApi {

    public static ISkillTreeTracker getTracker(Entity entity) {
        return SkillTreeOperation.get(entity);
    }

    public static <R> R perform(Entity entity, SkillTreeOperation<R> operation) {
        return SkillTreeOperation.perform(entity, operation);
    }

    public static CriterionTracker getTracker(Entity entity, ResourceLocation trackerId) {
        return perform(entity, tracker -> tracker.getTracker(trackerId));
    }

    public static ProgressTracker getProgress(Entity entity, ResourceLocation trackerId) {
        return perform(entity, tracker -> {
            CriterionTracker trackable = tracker.getTracker(trackerId);
            return trackable != null ? tracker.getProgress(trackable) : null;
        });
    }

    public static Skill getSkill(ResourceLocation skillId) {
        return ModMain.getInstance().getSkillManager().getSkill(skillId);
    }

    public static SkillPage getPage(ResourceLocation skillPageId) {
        return ModMain.getInstance().getSkillPageManager().getPage(skillPageId);
    }

    public static boolean grantSkill(Entity entity, Skill skill) {
        return perform(entity, tracker -> tracker.grant(skill));
    }

    public static boolean revokeSkill(Entity entity, Skill skill) {
        return perform(entity, tracker -> tracker.revoke(skill));
    }

    public static boolean obtained(Entity entity, CriterionTracker tracker) {
        return obtained(entity, tracker.getRegistryName());
    }

    public static boolean obtained(Entity entity, ResourceLocation id) {
        ProgressTracker progress = getProgress(entity, id);
        return progress != null && progress.isDoneFast();
    }

    public static LootContext getLootContext(Entity entity) {
        LootContext lootContext = null;
        if (entity.getEntityWorld() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) entity.getEntityWorld();
            lootContext = new LootContext.Builder(world).withParameter(LootParameters.THIS_ENTITY, entity)
                    .withParameter(LootParameters.field_237457_g_, entity.getPositionVec())
                    .withRandom(world.getRandom()).build(LootParameterSets.field_237454_j_);
        }
        return lootContext;
    }

    public boolean canSee(Entity entity, CriterionTracker tracker) {
        return tracker.isVisibleTo(entity);
    }

    public interface SkillTreeOperation<R> extends Function<ISkillTreeTracker, R> {
        static ISkillTreeTracker get(Entity entity) {
            return SkillTreeOperation.perform(entity, tracker -> tracker);
        }

        static <R> R perform(Entity entity, SkillTreeOperation<R> operation) {
            return operation.apply(entity.getCapability(ModMain.SKILL_TREE_CAPABILITY));
        }

        default R apply(LazyOptional<ISkillTreeTracker> lazyOptional) {
            Optional<ISkillTreeTracker> optional = lazyOptional.resolve();
            return optional.map(this).orElse(null);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class Client extends SkillTreeApi {
        public static boolean lazyHasSkill(Skill skill) {
            return perform(Minecraft.getInstance().player, tracker -> tracker.getProgress(skill).isDoneFast());
        }

        public static boolean hasSkill(Skill skill) {
            return perform(Minecraft.getInstance().player, tracker -> tracker.getProgress(skill).isDoneFast());
        }
    }
}
