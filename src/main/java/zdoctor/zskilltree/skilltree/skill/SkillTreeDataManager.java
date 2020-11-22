package zdoctor.zskilltree.skilltree.skill;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.skilltree.data.managers.SkillPageManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SkillTreeDataManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static ImmutableMap<ResourceLocation, CriterionTracker> trackers;
    private final HashMap<UUID, ISkillTreeTracker> playerData = new HashMap<>();

    public static ImmutableMap<ResourceLocation, CriterionTracker> getAllTrackers() {
        return trackers;
    }

    public SkillTreeDataManager() {
        reload();
    }

    private static void updateAllTrackers() {
        Map<ResourceLocation, CriterionTracker> temp = new HashMap<>();
        temp.putAll(ModMain.getInstance().getSkillPageManager().getAllEntries());
        temp.putAll(ModMain.getInstance().getSkillManager().getAllEntries());
        trackers = ImmutableMap.copyOf(temp);
    }

    public void playerLoggedIn(ServerPlayerEntity player, SkillPageManager manager) {
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

}
