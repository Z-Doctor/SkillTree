package zdoctor.zskilltree.api;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.skilltree.skill.Skill;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class SkillTreeApi {
    public static Skill skillFrom(ResourceLocation skillId) {
        // TODO A way for client side testing and converting
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
        if(entity == null)
            return null;
        return () -> {
            if(entity.get() == null)
                return null;
            Optional<ISkillTreeTracker> optional = entity.get().getCapability(ModMain.SKILL_TREE_CAPABILITY).resolve();
            return optional.map(function).orElse(null);
        };
    }

    public static <R> Supplier<R> of(Entity entity, Function<ISkillTreeTracker, R> function) {
        if(entity == null)
            return null;
        Optional<ISkillTreeTracker> optional = entity.getCapability(ModMain.SKILL_TREE_CAPABILITY).resolve();
        return optional.<Supplier<R>>map(tracker -> () -> function.apply(tracker)).orElse(null);
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
            return perform(Minecraft.getInstance().player, tracker -> tracker.getProgress(skill).isDone());
        }
    }
}
