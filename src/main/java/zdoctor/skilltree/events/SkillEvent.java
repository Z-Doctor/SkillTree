package zdoctor.skilltree.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.client.gui.GuiSkillPage;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.SkillSlot;

public class SkillEvent extends Event {

	public static class ReloadPages extends SkillEvent {
		public ReloadPages() {
			super(EventType.RELOAD);
		}

	}

	@Cancelable
	@SideOnly(Side.CLIENT)
	public static class RecenterPage extends SkillEvent {

		public GuiSkillPage page;

		public RecenterPage(GuiSkillPage page) {
			super(EventType.RECENTER);
			this.page = page;
		}

	}

	public EventType type;

	public SkillEvent(EventType type) {
		this.type = type;
	}

	public static class SkillTick extends SkillEvent {

		public SkillTick() {
			super(EventType.TICK);
		}

	}

	/**
	 * Fired before an obtained tickable skill is ticked. Will not tick if canceled
	 * or will tick (even if not active) if the result is set to allow
	 *
	 */
	@Cancelable
	@HasResult
	public static class ActiveTick extends SkillEvent {

		public EntityLivingBase owner;
		public SkillSlot skillSlot;
		public SkillBase skill;

		public ActiveTick(EntityLivingBase owner, SkillSlot skillSlot, SkillBase skill) {
			super(EventType.ACTIVE_TICK);
			this.owner = owner;
			this.skillSlot = skillSlot;
			this.skill = skill;
		}

	}

	public static enum EventType {
		TICK,
		ACTIVE_TICK,
		RELOAD,
		RECENTER
	}
}
