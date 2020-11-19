package zdoctor.zskilltree.network.play.server;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.api.interfaces.ITrackCriterion;
import zdoctor.zskilltree.criterion.ProgressTracker;

import java.util.*;
import java.util.function.Supplier;

public class SSkillPageInfoPacket {
    private static final Logger LOGGER = LogManager.getLogger();
    private Collection<ITrackCriterion> toAdd;
    private Map<String, List<ITrackCriterion>> trackableTypes;
    private boolean firstSync;
    private Set<ResourceLocation> toRemove;
    private Map<ResourceLocation, ProgressTracker> progressChanged;


    public SSkillPageInfoPacket() {
    }

    public SSkillPageInfoPacket(boolean firstSync, Collection<ITrackCriterion> toAdd, Set<ResourceLocation> toRemove, Map<ResourceLocation, ProgressTracker> progressChanged) {
        this.firstSync = firstSync;
        this.trackableTypes = new HashMap<>();

        String className;
        for (ITrackCriterion trackable : toAdd) {
            className = trackable.getClass().getName();
            this.trackableTypes.putIfAbsent(className, new ArrayList<>());
            this.trackableTypes.get(className).add(trackable);
        }

        this.toRemove = toRemove;
        this.progressChanged = progressChanged;
    }

    public static SSkillPageInfoPacket readFrom(PacketBuffer buf) {
        SSkillPageInfoPacket infoPacket = new SSkillPageInfoPacket();
        infoPacket.readPacketData(buf);
        return infoPacket;
    }

    public static void handle(SSkillPageInfoPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            LOGGER.error("Client sent Packet to Server");
        } else if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            LOGGER.debug("Server sent packet to client: " + Minecraft.getInstance().player);
            Optional<ISkillTreeTracker> cap = Minecraft.getInstance().player.getCapability(ModMain.SKILLTREE_CAPABILITY).resolve();
            if (cap.isPresent())
                cap.get().read(msg);
            else
                LOGGER.fatal("Handler for player {} not found", Minecraft.getInstance().player);
        }

        ctx.get().setPacketHandled(true);
    }

    public void readPacketData(PacketBuffer buf) {
        this.firstSync = buf.readBoolean();
        this.toAdd = new HashSet<>();
        this.trackableTypes = new HashMap<>();
        this.toRemove = new HashSet<>();
        this.progressChanged = new HashMap<>();

        int count;
        for (count = buf.readVarInt(); count > 0; count--) {
            String className = buf.readString();

            int start, size;
            for (int i = buf.readVarInt(); i > 0; i--) {
                start = buf.readerIndex();
                size = buf.readInt();
                try {
                    // TODO Check for static read from
                    ITrackCriterion trackable = (ITrackCriterion) Class.forName(className).newInstance();
//                    ResourceLocation id = buf.readResourceLocation();
                    trackable.readFrom(buf);
                    toAdd.add(trackable);
//                    trackableTypes.put(id, trackable);
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    LOGGER.error("Ran into error {}: skipping", e.getLocalizedMessage());
                    buf.readerIndex(start + size);
                }
            }

        }

        for (count = buf.readVarInt(); count > 0; count--) {
            ResourceLocation id = buf.readResourceLocation();
            toRemove.add(id);
        }

        for (count = buf.readVarInt(); count > 0; count--) {
            ResourceLocation id = buf.readResourceLocation();
            progressChanged.put(id, ProgressTracker.fromNetwork(buf));
        }
    }

    public void writePacketData(PacketBuffer buf) {
        buf.writeBoolean(firstSync);
        buf.writeVarInt(trackableTypes.size());

        int blockSize, start;
        for (Map.Entry<String, List<ITrackCriterion>> entry : trackableTypes.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeVarInt(entry.getValue().size());
            for (ITrackCriterion trackable : entry.getValue()) {
                start = buf.writerIndex();
                buf.writeInt(0);
//                buf.writeResourceLocation(trackable.getId());
                trackable.writeTo(buf);
                blockSize = buf.writerIndex() - start;
                buf.markWriterIndex();
                buf.writerIndex(start);
                buf.writeInt(blockSize);
                buf.resetWriterIndex();
            }
        }

        buf.writeVarInt(toRemove.size());
        for (ResourceLocation id : toRemove)
            buf.writeResourceLocation(id);

        buf.writeVarInt(progressChanged.size());
        for (Map.Entry<ResourceLocation, ProgressTracker> entry : progressChanged.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            entry.getValue().serializeToNetwork(buf);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Collection<ITrackCriterion> getToAdd() {
        return toAdd;
    }

    @OnlyIn(Dist.CLIENT)
    public Set<ResourceLocation> getToRemove() {
        return toRemove;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFirstSync() {
        return firstSync;
    }

}
