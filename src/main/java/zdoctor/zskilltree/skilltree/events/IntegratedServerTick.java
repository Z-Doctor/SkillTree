package zdoctor.zskilltree.skilltree.events;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;

public class IntegratedServerTick extends Event {
    public final TickEvent.Phase phase;

    public IntegratedServerTick(TickEvent.Phase phase) {
        this.phase = phase;
    }
}
