package zdoctor.zskilltree.api.interfaces;

import net.minecraft.entity.Entity;

import java.util.function.Predicate;

public interface EntityPredicate extends Predicate<Entity> {
    @Override
    boolean test(Entity entity);
}
