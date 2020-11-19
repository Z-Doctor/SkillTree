package zdoctor.zskilltree.api.interfaces;

import net.minecraft.util.ResourceLocation;

public interface CriterionTracker extends HasCriteria, PacketSerializer {
    ResourceLocation getId();
}
