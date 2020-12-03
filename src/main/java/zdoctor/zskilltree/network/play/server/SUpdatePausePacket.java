package zdoctor.zskilltree.network.play.server;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.config.SkillTreeGameRules;

import java.util.function.Supplier;

public class SUpdatePausePacket {
    private final boolean doPause;

    public SUpdatePausePacket(boolean doPause) {
        this.doPause = doPause;
    }

    public SUpdatePausePacket(PacketBuffer buf) {
        doPause = buf.readBoolean();
    }

    public static void handle(SUpdatePausePacket msg, Supplier<NetworkEvent.Context> ctx) {
        World world = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().world);
        if (world != null && world.isRemote) {
            world.getGameRules().get(SkillTreeGameRules.DO_SKILL_TREE_PAUSE).set(msg.doPause, null);
            ctx.get().setPacketHandled(true);
        }
    }

    public static void onChanged(MinecraftServer minecraftServer, GameRules.BooleanValue newValue) {
        if (minecraftServer.isSinglePlayer()) {
            ModMain.getInstance().getPacketChannel().send(PacketDistributor.ALL.noArg(), new SUpdatePausePacket(newValue.get()));
        }
    }

    public void writeTo(PacketBuffer buf) {
        buf.writeBoolean(doPause);
    }
}
