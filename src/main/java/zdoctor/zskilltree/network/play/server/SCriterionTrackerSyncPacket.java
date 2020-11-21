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
import zdoctor.zskilltree.api.annotations.ClassNameMapper;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.criterion.ProgressTracker;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class SCriterionTrackerSyncPacket {
    private static final Logger LOGGER = LogManager.getLogger();
    private Collection<CriterionTracker> toAdd;
    private Map<String, List<CriterionTracker>> trackableTypes;
    private boolean firstSync;
    private Set<ResourceLocation> toRemove;
    private Map<ResourceLocation, ProgressTracker> progressChanged;

    public SCriterionTrackerSyncPacket() {
    }

    public SCriterionTrackerSyncPacket(boolean firstSync, Collection<CriterionTracker> toAdd, Set<ResourceLocation> toRemove, Map<ResourceLocation, ProgressTracker> progressChanged) {
        this.firstSync = firstSync;
        this.trackableTypes = new HashMap<>();

        String key;
        for (CriterionTracker trackable : toAdd) {
            ClassNameMapper mapping = trackable.getClass().getAnnotation(ClassNameMapper.class);
            if (mapping == null)
                key = trackable.getClass().getSimpleName();
            else
                key = mapping.key();
            this.trackableTypes.putIfAbsent(key, new ArrayList<>());
            this.trackableTypes.get(key).add(trackable);
        }

        this.toAdd = toAdd;
        this.toRemove = toRemove;
        this.progressChanged = progressChanged;
    }

    public static SCriterionTrackerSyncPacket readFrom(PacketBuffer buf) {
        SCriterionTrackerSyncPacket infoPacket = new SCriterionTrackerSyncPacket();
        infoPacket.readPacketData(buf);
        return infoPacket;
    }

    public static void handle(SCriterionTrackerSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            LOGGER.error("Client sent Packet to Server");
        } else if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            LOGGER.debug("Server sent packet to client: " + Minecraft.getInstance().player);
            Optional<ISkillTreeTracker> cap = Minecraft.getInstance().player.getCapability(ModMain.SKILL_TREE_CAPABILITY).resolve();
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

        int count, blockEnd, nextBlock;
        for (count = buf.readVarInt(); count > 0; count--) {
            String type = buf.readString();
            blockEnd = buf.readerIndex() + buf.readInt();
            Function<PacketBuffer, CriterionTracker> reader = ModMain.getInstance().getCriterionMappings().get(type);
            if (reader == null) {
                LOGGER.error("Skipping: Unable to find reader for type {}", type);
                buf.readerIndex(blockEnd);
                continue;
            }

            for (int i = buf.readVarInt(); i > 0; i--) {
                nextBlock = buf.readerIndex() + buf.readInt();
                try {
                    toAdd.add(reader.apply(buf));
                } catch (Exception e) {
                    LOGGER.trace("Skipping: Ran into error {} when parsing item of {}", e.getLocalizedMessage()
                            , type);
                    buf.readerIndex(nextBlock);
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

        int blockSize, blockStart;
        for (Map.Entry<String, List<CriterionTracker>> entry : trackableTypes.entrySet()) {
            buf.writeString(entry.getKey());
            blockStart = buf.writerIndex();
            buf.writeInt(0);
            buf.writeVarInt(entry.getValue().size());

            int size, start;
            for (CriterionTracker trackable : entry.getValue()) {
                start = buf.writerIndex();
                buf.writeInt(0);
                trackable.writeTo(buf);
                size = buf.writerIndex() - start;
                buf.markWriterIndex();
                buf.writerIndex(start);
                buf.writeInt(size);
                buf.resetWriterIndex();
            }
            blockSize = buf.writerIndex() - blockStart;
            buf.markWriterIndex();
            buf.writerIndex(blockStart);
            buf.writeInt(blockSize);
            buf.resetWriterIndex();
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
    public Collection<CriterionTracker> getToAdd() {
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
