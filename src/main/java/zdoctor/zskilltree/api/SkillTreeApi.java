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
import zdoctor.zskilltree.skilltree.data.criterion.ProgressTracker;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class SkillTreeApi {

    public static Skill skillFrom(ResourceLocation skillId) {
        return ModMain.getInstance().getSkillManager().getSkill(skillId);
    }

    public static boolean grantSkill(Entity entity, Skill skill) {
        return perform(entity, tracker -> tracker.grant(skill));
    }

    public static boolean revokeSkill(Entity entity, Skill skill) {
        return perform(entity, tracker -> tracker.revoke(skill));
    }

    public static boolean perform(LazyOptional<Entity> lazyOptional, SkillTreeOperation operation) {
        if (!lazyOptional.isPresent())
            return false;
        Optional<Entity> optional = lazyOptional.resolve();
        return optional.isPresent() && perform(optional.get(), operation);
    }

    public static boolean perform(Entity entity, SkillTreeOperation operation) {
        if (entity == null)
            return false;
        return operation.apply(entity.getCapability(ModMain.SKILL_TREE_CAPABILITY));
    }

    public static <R> Supplier<R> of(Supplier<Entity> entity, Function<ISkillTreeTracker, R> function) {
        if (entity == null)
            return null;
        return () -> {
            if (entity.get() == null)
                return null;
            Optional<ISkillTreeTracker> optional = entity.get().getCapability(ModMain.SKILL_TREE_CAPABILITY).resolve();
            return optional.map(function).orElse(null);
        };
    }

    public static <R> Supplier<R> of(Entity entity, Function<ISkillTreeTracker, R> function) {
        if (entity == null)
            return null;
        Optional<ISkillTreeTracker> optional = entity.getCapability(ModMain.SKILL_TREE_CAPABILITY).resolve();
        return optional.<Supplier<R>>map(tracker -> () -> function.apply(tracker)).orElse(null);
    }

    public static SkillPage getPage(ResourceLocation skillPageId) {
        return ModMain.getInstance().getSkillPageManager().getPage(skillPageId);
    }

    public static boolean hasPage(Entity entity, SkillPage page) {
        return perform(entity, tracker -> {
            ProgressTracker progress = tracker.getProgress(page);
            return progress != null && progress.isDone();
        });
    }

    public static boolean hasPage(Entity entity, ResourceLocation pageId) {
        return perform(entity, tracker -> {
            CriterionTracker trackable = tracker.getTracker(pageId);
            if (!(trackable instanceof SkillPage))
                return false;
            ProgressTracker progress = tracker.getProgress(trackable);
            return progress != null && progress.isDone();
        });
    }

    public static ISkillTreeTracker getTracker(Entity entity) {
        return of(entity, skillTreeTracker -> skillTreeTracker).get();
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

    public interface SkillTreeOperation {
        default boolean apply(LazyOptional<ISkillTreeTracker> lazyOptional) {
            Optional<ISkillTreeTracker> optional = lazyOptional.resolve();
            return optional.isPresent() && apply(optional.get());
        }

        boolean apply(ISkillTreeTracker tracker);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Client extends SkillTreeApi {
        public static boolean lazyHasSkill(Skill skill) {
            return perform(Minecraft.getInstance().player, tracker -> tracker.getProgress(skill).lazyIsDone());
        }

        public static boolean hasSkill(Skill skill) {
            return perform(Minecraft.getInstance().player, tracker -> tracker.isDone(skill));
        }
    }
}
