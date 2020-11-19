package zdoctor.zskilltree.api.interfaces;

import net.minecraft.advancements.Criterion;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public interface ITrackCriterion {
    ResourceLocation getId();

    Map<String, Criterion> getCriteria();

    String[][] getRequirements();

    void writeTo(PacketBuffer buf);

    void readFrom(PacketBuffer buf);
}
