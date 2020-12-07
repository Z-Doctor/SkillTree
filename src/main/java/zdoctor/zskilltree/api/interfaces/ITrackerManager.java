package zdoctor.zskilltree.api.interfaces;

import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.util.ResourceLocation;
import zdoctor.zskilltree.ModMain;

import java.util.Map;

public interface ITrackerManager extends IFutureReloadListener {
    Map<ResourceLocation, CriterionTracker> getAllTrackers();

    default void onReloaded() {
        ModMain.getInstance().getSkillTreeDataManager().onReload(this);
    }

    default void registerManager() {
        ModMain.getInstance().getSkillTreeDataManager().register(this);
    }
}
