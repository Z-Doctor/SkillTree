package zdoctor.skilltree.api.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import zdoctor.skilltree.api.skills.interfaces.ISkill;
import zdoctor.skilltree.api.skills.interfaces.ISkillPage;
import zdoctor.skilltree.api.skills.interfaces.ISkillSlot;

public class SkillEvent extends Event {

	public static class ReloadPages extends SkillEvent {
		public final ISkillPage page;

		public ReloadPages(ISkillPage page) {
			super(EventType.RELOAD);
			this.page = page;
		}

		@Cancelable
		public static class Pre extends ReloadPages {
			public Pre(ISkillPage page) {
				super(page);
			}

		}

		public static class Post extends ReloadPages {

			public Post(ISkillPage page) {
				super(page);
			}

		}

	}

	@Cancelable
	public static class RecenterPage extends SkillEvent {

		public ISkillPage page;

		public RecenterPage(ISkillPage page) {
			super(EventType.RECENTER);
			this.page = page;
		}

	}

	public EventType type;

	public SkillEvent(EventType type) {
		this.type = type;
	}

	public static class SkillTick extends SkillEvent {

		private World world;

		public SkillTick(World world) {
			super(EventType.TICK);
			this.world = world;
		}

		public World getWorld() {
			return world;
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
		public ISkillSlot skillSlot;
		public ISkill skill;

		public ActiveTick(EntityLivingBase owner, ISkillSlot skillSlot, ISkill skill) {
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
