package zdoctor.zskilltree.skilltree.events;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import zdoctor.zskilltree.api.enums.ProgressAction;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;

/**
 * Only called called the first time a skill page is granted or revoked
 */
public class CriterionTrackerEvent extends Event {

    private final LivingEntity owner;
    private final CriterionTracker trackable;
    private final ProgressAction action;

    public CriterionTrackerEvent(LivingEntity owner, CriterionTracker trackable, ProgressAction action) {
        this.owner = owner;
        this.trackable = trackable;
        this.action = action;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public CriterionTracker getTrackable() {
        return trackable;
    }

    public ProgressAction getAction() {
        return action;
    }

    public static class ProgressGrantedEvent extends CriterionTrackerEvent {

        public ProgressGrantedEvent(LivingEntity owner, CriterionTracker trackable) {
            super(owner, trackable, ProgressAction.GRANT);
        }
    }

    public static class ProgressRevokedEvent extends CriterionTrackerEvent {
        public ProgressRevokedEvent(LivingEntity owner, CriterionTracker trackable) {
            super(owner, trackable, ProgressAction.REVOKE);
        }
    }
}
