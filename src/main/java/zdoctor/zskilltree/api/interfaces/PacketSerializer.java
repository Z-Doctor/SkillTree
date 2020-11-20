package zdoctor.zskilltree.api.interfaces;

import net.minecraft.network.PacketBuffer;

public interface PacketSerializer {
    void writeTo(PacketBuffer buf);
}
