package zdoctor.zskilltree.api.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public interface CriterionTracker extends HasCriteria, PacketSerializer {
    ResourceLocation getRegistryName();

    default boolean isConditionallyVisible() {
        return false;
    }

    boolean isVisibleTo(Entity entity);
}
