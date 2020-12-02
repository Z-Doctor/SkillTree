package zdoctor.zskilltree.network.play.server;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.SkillTreeApi;
import zdoctor.zskilltree.api.annotations.ClassNameMapper;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.skilltree.trackers.ProgressTracker;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class SCriterionTrackerSyncPacket {
    private static final Logger LOGGER = LogManager.getLogger();
    public final boolean fromServer;
    private final Collection<CriterionTracker> toAdd;
    private final Map<String, List<CriterionTracker>> trackableTypes;
    private final boolean firstSync;
    private final Set<ResourceLocation> toRemove;
    private final Map<ResourceLocation, ProgressTracker> progressChanged;

    public SCriterionTrackerSyncPacket(PacketBuffer buf) {
        fromServer = false;

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
            progressChanged.put(id, new ProgressTracker(buf));
        }
    }

    public SCriterionTrackerSyncPacket(boolean firstSync, Collection<CriterionTracker> toAdd,
                                       Set<ResourceLocation> toRemove, Map<ResourceLocation, ProgressTracker> progressChanged) {
        fromServer = true;
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

    public static void handle(SCriterionTrackerSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
            LOGGER.trace("Invalid Direction for this packet");
            return;
        }

        Entity player = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
        LOGGER.debug("Server sent packet to client: " + player.getDisplayName().getString());
        boolean flag = SkillTreeApi.perform(player, tracker -> {
            tracker.read(msg);
            return true;
        });
        if (!flag)
            LOGGER.fatal("Handler for player {} not found", Minecraft.getInstance().player);

        ctx.get().setPacketHandled(flag);
    }

    public void writeTo(PacketBuffer buf) {
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
            entry.getValue().writeTo(buf);
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
    public Map<ResourceLocation, ProgressTracker> getProgressChanged() {
        return progressChanged;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFirstSync() {
        return firstSync;
    }

}
