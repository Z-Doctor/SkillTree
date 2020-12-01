package zdoctor.zskilltree.skilltree.managers;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.SkillTreeApi;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SkillTreeDataManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final HashMap<UUID, ISkillTreeTracker> playerData = new HashMap<>();
    private ImmutableMap<ResourceLocation, CriterionTracker> trackers;

    public SkillTreeDataManager() {
    }

    public ImmutableMap<ResourceLocation, CriterionTracker> getAllTrackers() {
        return trackers;
    }

    public CriterionTracker getTracker(ResourceLocation id) {
        return trackers.get(id);
    }

    private void updateAllTrackers() {
        Map<ResourceLocation, CriterionTracker> temp = new HashMap<>();
        temp.putAll(ModMain.getInstance().getSkillPageManager().getAllEntries());
        temp.putAll(ModMain.getInstance().getSkillManager().getAllEntries());
        trackers = ImmutableMap.copyOf(temp);
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

    public void onPlayerTick(ServerPlayerEntity player) {
        playerData.computeIfPresent(player.getUniqueID(), ((uuid, playerData) -> {
            playerData.flushDirty();
            return playerData;
        }));
    }

    public Optional<ISkillTreeTracker> getSkillData(ServerPlayerEntity player) {
        return player.getCapability(ModMain.SKILL_TREE_CAPABILITY).resolve();
    }

    public void reload() {
        updateAllTrackers();
        playerData.values().forEach(ISkillTreeTracker::reload);
    }

    public void onPlayerClone(PlayerEntity original, PlayerEntity newPlayer) {
        ISkillTreeTracker old = SkillTreeApi.getTracker(original);
        ISkillTreeTracker $new = SkillTreeApi.getTracker(newPlayer);

        // TODO Add config for keep on death(Default: true)
        if (old == null || $new == null)
            LOGGER.trace("Unable to clone cap from {} to {}", original.getDisplayName(), newPlayer.getDisplayName());
        else {
            if(!original.getUniqueID().equals(newPlayer.getUniqueID()))
                LOGGER.error("Old Player UUID did not match new Player UUID");
            $new.deserializeNBT(old.serializeNBT());
            playerData.put(original.getUniqueID(), $new);
            // TODO re-examine the use of this as it is more of a work around
            $new.reload();
        }
    }
}
