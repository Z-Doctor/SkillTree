package zdoctor.zskilltree.network.play;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.api.SkillTreeApi;
import zdoctor.zskilltree.skilltree.criterion.Skill;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class SkillInteractionPacket {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ResourceLocation skillId;
    private final Type type;
    // TODO Maybe make it so that skill info like requirements, title, description, etc isn't sent to client
    //  until it's clicked

    @OnlyIn(Dist.CLIENT)
    public SkillInteractionPacket(@Nonnull Skill skill, Type type) {
        // Client Side
        this.skillId = Objects.requireNonNull(skill.getRegistryName());
        this.type = type;
    }

    public SkillInteractionPacket(PacketBuffer buf) {
        // Server
        type = buf.readEnumValue(Type.class);
        skillId = buf.readResourceLocation();
    }

    public static void handle(SkillInteractionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        try {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                switch (msg.type) {
                    case BUY:
                        LOGGER.debug("{} is trying to buy skill {}", Objects.requireNonNull(ctx.get().getSender()).getDisplayName().getString(), msg.skillId);
                        // TODO Add logic if skill is bought
                        SkillTreeApi.grantSkill(ctx.get().getSender(), SkillTreeApi.getSkill(msg.skillId));
                        break;
                    case TOGGLE:
                        // TODO Add logic for toggle
                    default:
                        LOGGER.error("Don't know how to handle request of {} from {}", msg.type, Objects.requireNonNull(ctx.get().getSender()).getDisplayName());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error handling packet from {} info: {} error: {}", ctx.get().getSender(), msg, e.getLocalizedMessage());
        }

        ctx.get().setPacketHandled(true);
    }

    public void writeTo(PacketBuffer buf) {
        buf.writeEnumValue(type);
        buf.writeResourceLocation(skillId);
    }

    @Override
    public String toString() {
        return "SkillInteractionPacket{" +
                "skillId=" + skillId +
                ", type=" + type +
                '}';
    }

    public enum Type {
        BUY,
        TOGGLE,
    }

}
