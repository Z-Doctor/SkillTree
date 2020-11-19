package zdoctor.mcskilltree.skilltree.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.api.ClientSkillApi;
import zdoctor.mcskilltree.api.SkillApi;
import zdoctor.mcskilltree.api.ISkillHandler;
import zdoctor.mcskilltree.skills.Skill;

import java.util.Objects;
import java.util.function.Supplier;

public class SkillTreePacket {
    public final Type type;
    public final Object data;

    public SkillTreePacket(Type type) {
        this(type, null);
    }

    public SkillTreePacket(Type type, Object data) {
        this.type = type;
        this.data = data;

        if (data == null)
            if (type == Type.UPDATE)
                McSkillTree.LOGGER.error("Sent {} without any data.", type);
    }

    public static void encode(SkillTreePacket msg, PacketBuffer buf) {
        buf.writeEnumValue(msg.type);
        switch (msg.type) {
            case BUY:
                buf.writeResourceLocation(Objects.requireNonNull(((Skill) msg.data).getRegistryName()));
                break;
            case UPDATE:
                ISkillHandler skillHandler = SkillApi.getSkillHandler((LivingEntity) msg.data);
                buf.writeCompoundTag(skillHandler.serializeNBT());
                break;
        }

    }

    public static SkillTreePacket decode(PacketBuffer buf) {
        Type type = buf.readEnumValue(Type.class);
        SkillTreePacket packet;
        switch (type) {
            case BUY:
                ResourceLocation resourceLocation = buf.readResourceLocation();
                packet = new SkillTreePacket(type, GameRegistry.findRegistry(Skill.class).getValue(resourceLocation));
                break;
            case UPDATE:
                packet = new SkillTreePacket(type, buf.readCompoundTag());
                break;
            default:
                packet = null;
        }

        return packet;
    }

    public static class SkillTreePacketHandler {
        public static void handle(SkillTreePacket msg, Supplier<NetworkEvent.Context> ctx) {
            // Client to Server
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                ctx.get().enqueueWork(() -> {
                    ServerPlayerEntity sender = ctx.get().getSender();
                    if (sender != null) {
                        McSkillTree.LOGGER.debug(String.format("SkillPacket of type '%s' sent by '%s'", msg.type, sender.getDisplayName().getString()));
                        switch (msg.type) {
                            case BUY:
                                SkillApi.buySkill(sender, (Skill) msg.data);
                                break;
                            case SELL:
                            case TOGGLE:
                            case REFUND:
                                break;
                            case QUERY:
                                SkillApi.updateSkillData(sender);
                                break;
                            default:
                                McSkillTree.LOGGER.error("Unknown type sent " + msg.type);
                                break;
                            // TODO implement other cases
                        }
                    }
                });
            } else { // Server to Client
                ctx.get().enqueueWork(() -> {
                    if (msg.type == Type.UPDATE) {
                        ISkillHandler skillHandler = SkillApi.getSkillHandler(Objects.requireNonNull(Minecraft.getInstance().player));
                        skillHandler.deserializeNBT((CompoundNBT) msg.data);
                        skillHandler.markClean();
                        ClientSkillApi.update(skillHandler);
                    } else {
                        McSkillTree.LOGGER.error("Server to Client not implemented for type: " + msg.type);
                    }
                });
            }

            ctx.get().setPacketHandled(true);
        }
    }

    public enum Type {
        EMPTY,
        BUY,
        SELL,
        REFUND,
        TOGGLE,
        QUERY,
        UPDATE
    }

}
