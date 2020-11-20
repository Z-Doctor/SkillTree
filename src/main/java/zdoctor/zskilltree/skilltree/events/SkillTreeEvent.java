package zdoctor.zskilltree.skilltree.events;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class SkillTreeEvent extends Event {
    public static class PlayerReloadedEvent extends SkillTreeEvent {

        private final ServerPlayerEntity player;

        public PlayerReloadedEvent(ServerPlayerEntity player) {
            this.player = player;
        }

        public ServerPlayerEntity getPlayer() {
            return player;
        }
    }
}
