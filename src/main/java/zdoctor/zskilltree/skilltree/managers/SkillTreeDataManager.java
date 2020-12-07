package zdoctor.zskilltree.skilltree.managers;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.SkillTreeApi;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.api.interfaces.ITrackerManager;
import zdoctor.zskilltree.config.SkillTreeConfig;
import zdoctor.zskilltree.config.SkillTreeGameRules;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SkillTreeDataManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final HashMap<UUID, ISkillTreeTracker> playerData = new HashMap<>();
    private final Map<ResourceLocation, CriterionTracker> trackers = new HashMap<>();
    private final Object2BooleanMap<ITrackerManager> updateStatus = new Object2BooleanOpenHashMap<>();
    private int ticksSinceUpdate;
    private MinecraftServer server;

    public SkillTreeDataManager() {
    }

    public boolean register(ITrackerManager trackerManager) {
        return updateStatus.put(trackerManager, false);
    }

    public void onReload(ITrackerManager trackerManager) {
        if (!updateStatus.containsKey(trackerManager))
            LOGGER.error(ModMain.SKILLTREEMOD, "Tried to reload unregistered Tracker Manager");
        else {
            LOGGER.debug(ModMain.SKILLTREEMOD, "Reloaded {}", trackerManager.getSimpleName());
            updateStatus.put(trackerManager, true);
            if (updateStatus.values().stream().allMatch(Boolean::booleanValue)) {
                updateStatus.keySet().forEach(key -> updateStatus.put(key, false));
                reload();
            }
        }
    }

    public void clearManagers() {
        updateStatus.clear();
    }

    public void update(ITrackerManager trackerManager) {
        trackerManager.getAllTrackers().putAll(trackers);
    }

    public ImmutableMap<ResourceLocation, CriterionTracker> getAllTrackers() {
        return ImmutableMap.copyOf(trackers);
    }

    public CriterionTracker getTracker(ResourceLocation id) {
        return trackers.get(id);
    }

    private void updateAllTrackers() {
        updateStatus.keySet().forEach(manager -> trackers.putAll(manager.getAllTrackers()));
    }

    public void playerLoggedIn(ServerPlayerEntity player) {
        if (playerData.containsKey(player.getUniqueID()))
            LOGGER.error("Duplicate player UUID {}: {}", player.getDisplayName(), player.getUniqueID());
        else {
            getSkillData(player).ifPresent(tracker -> {
                playerData.put(player.getUniqueID(), tracker);
                tracker.reload();
            });

            if (!playerData.containsKey(player.getUniqueID()))
                LOGGER.error("Player {} did not have Skill Tree Capabilities", player.getDisplayName().getString());
        }
    }

    public void playerLoggedOut(ServerPlayerEntity player) {
        playerData.remove(player.getUniqueID());
    }

    public Optional<ISkillTreeTracker> getSkillData(ServerPlayerEntity player) {
        return player.getCapability(ModMain.getSkillTreeCapability()).resolve();
    }

    public void reload() {
        updateAllTrackers();
        playerData.values().forEach(ISkillTreeTracker::reload);
    }

    public void onPlayerClone(PlayerEntity original, PlayerEntity newPlayer) {
        if (!server.getGameRules().get(SkillTreeGameRules.KEEP_SKILLS_ON_DEATH).get())
            return;

        ISkillTreeTracker old = SkillTreeApi.getTracker(original);
        ISkillTreeTracker $new = SkillTreeApi.getTracker(newPlayer);

        if (old == null || $new == null)
            LOGGER.trace("Unable to clone cap from {} to {}", original.getDisplayName(), newPlayer.getDisplayName());
        else {
            if (!original.getUniqueID().equals(newPlayer.getUniqueID()))
                LOGGER.error("Old Player UUID did not match new Player UUID");
            $new.deserializeNBT(old.serializeNBT());
            playerData.put(original.getUniqueID(), $new);
            // TODO re-examine the use of this as it is more of a work around
            $new.reload();
        }
    }

    public void onServerTick(LogicalSide side, TickEvent.Phase phase) {
        if (side == LogicalSide.SERVER && phase == TickEvent.Phase.START) {
            if (ticksSinceUpdate >= SkillTreeConfig.SERVER.updateTicks.get()) {
                ticksSinceUpdate = 0;
                playerData.values().forEach(ISkillTreeTracker::flushDirty);
            } else
                ticksSinceUpdate++;
        }
    }

    public void onServerStarted(MinecraftServer server) {
        this.server = server;
    }
}
