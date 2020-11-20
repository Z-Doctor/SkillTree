package zdoctor.zskilltree.skilltree.events;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import zdoctor.zskilltree.api.enums.SkillTreeAction;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

/**
 * Only called called the first time a skill page is granted or revoked
 */
public class SkillPageEvent extends Event {

    private final LivingEntity owner;
    private final SkillPage skillPage;
    private final SkillTreeAction action;

    public SkillPageEvent(LivingEntity owner, SkillPage skillPage, SkillTreeAction action) {
        this.owner = owner;
        this.skillPage = skillPage;
        this.action = action;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public SkillPage getSkillPage() {
        return skillPage;
    }

    public SkillTreeAction getAction() {
        return action;
    }

    public static class SkillPageGrantedEvent extends SkillPageEvent {

        public SkillPageGrantedEvent(LivingEntity owner, SkillPage skillPage) {
            super(owner, skillPage, SkillTreeAction.GRANT);
        }
    }

    public static class SkillPageRevokedEvent extends SkillPageEvent {
        public SkillPageRevokedEvent(LivingEntity owner, SkillPage skillPage) {
            super(owner, skillPage, SkillTreeAction.REVOKE);
        }
    }
}
