package zdoctor.skilltree.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SkillHandlerEvent extends Event {
	public final boolean hasLoaded;
	public final EntityLivingBase owner;
	public final World world;

	public SkillHandlerEvent(World world, EntityLivingBase owner, boolean hasLoaded) {
		this.world = world;
		this.owner = owner;
		this.hasLoaded = hasLoaded;
	}

	@Cancelable
	@HasResult
	/**
	 * Fired before the handler is reloaded. Setting result to deny will prevent the
	 * FirstLoadEvent from firing if hasLoaded is false. Setting the result to allow
	 * will always fire the FirstLoadEvent
	 *
	 */
	public static class ReloadedEvent extends SkillHandlerEvent {

		/**
		 * @param hasLoaded
		 *            - Whether the handler has been used before. False if handler is
		 *            new (i.e. player joins the world for the first time). Only false
		 *            when entity first joins the world or player is reset
		 */
		public ReloadedEvent(World world, EntityLivingBase owner, boolean hasLoaded) {
			super(world, owner, hasLoaded);
		}
	}

	/**
	 * Fired when the handler is first created and has not loaded before. If you
	 * would like to give a player or entity Skill Points or something else when
	 * there skills are reset or created, do it here. Denying this event will
	 */
	public static class FirstLoadEvent extends SkillHandlerEvent {

		/**
		 * @param hasLoaded
		 *            - Whether the handler has been used before. False if handler is
		 *            new (i.e. player joins the world for the first time)
		 */
		public FirstLoadEvent(World world, EntityLivingBase owner, boolean hasLoaded) {
			super(world, owner, hasLoaded);
		}
	}
}
