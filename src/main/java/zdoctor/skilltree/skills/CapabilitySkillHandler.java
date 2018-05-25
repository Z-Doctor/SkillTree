package zdoctor.skilltree.skills;

import java.util.Collections;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.interfaces.ISkillHandler;
import zdoctor.skilltree.events.SkillEvent;

public class CapabilitySkillHandler {

	@CapabilityInject(ISkillHandler.class)
	public static Capability<ISkillHandler> SKILL_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(ISkillHandler.class, new Capability.IStorage<ISkillHandler>() {
			@Override
			public NBTBase writeNBT(Capability<ISkillHandler> capability, ISkillHandler instance, EnumFacing side) {
				return new NBTTagCompound();
			}

			@Override
			public void readNBT(Capability<ISkillHandler> capability, ISkillHandler instance, EnumFacing side,
					NBTBase base) {
			}
		}, SkillHandler::new);

	}

	@SubscribeEvent
	public void initCapabilities(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() == null || !(e.getObject() instanceof EntityLivingBase))
			return;
		e.addCapability(new ResourceLocation(ModMain.MODID, "SkillCapability"), new SkillCapabilityProvider());
	}

	@SubscribeEvent
	public void entityJoinedWorld(EntityJoinWorldEvent e) {
		debug2(e);
	}

	private void debug2(EntityJoinWorldEvent e) {
		if (e.getEntity() != null && e.getEntity() instanceof EntityLivingBase && !e.getEntity().isDead) {
			System.out.println(e.getEntity() + " joined on side " + (e.getWorld().isRemote ? "Client" : "Server"));
			if (!e.getWorld().isRemote) {
				SkillTreeApi.getSkillHandler((EntityLivingBase) e.getEntity()).markDirty();
				System.out.println("Marked Dirty: " + e.getEntity());
			}
			SkillTreeApi.syncSkills((EntityLivingBase) e.getEntity());
		}
	}

	@SubscribeEvent
	public void reloadCap(PlayerEvent.Clone e) {
		debug(e);
	}

	private void debug(Clone e) {
		ModMain.proxy.config.open();
		ModMain.proxy.config.sync();
		e.getEntityPlayer().setEntityId(e.getOriginal().getEntityId());
		if (e.isWasDeath() && !ModMain.proxy.keepSkillsOnDeath.getValue()) {
			ModMain.proxy.log.debug("KeepSkillsOnDeath is diabled, not sysncing {} skills",
					e.getEntityPlayer().getDisplayName());
			return;
		}

		ISkillHandler capOrginal = SkillTreeApi.getSkillHandler(e.getOriginal());
		ISkillHandler capClone = SkillTreeApi.getSkillHandler(e.getEntityPlayer());
		capClone.deserializeNBT(capOrginal.serializeNBT());
		capClone.markDirty();

		System.out.println("Reloading Cap: " + e.getOriginal().isDead);
		System.out.println("Reloading Cap: " + e.getEntityPlayer().world);
		System.out.println("capO: " + capOrginal.serializeNBT());
		System.out.println("capC: " + capClone.serializeNBT());
		// SkillTreeApi.syncSkills(e.getEntityPlayer());
	}

	@SubscribeEvent
	public void playerTracking(PlayerEvent.StartTracking e) {
		debug3(e);
	}

	private void debug3(StartTracking e) {
		Entity target = e.getTarget();
		// System.out.println("Player Tracking: " + e.getEntityPlayer());

		if (target != null && target instanceof EntityLivingBase && e.getEntityPlayer() instanceof EntityPlayerMP) {
			// System.out.println("Sync Track: " + e.getEntityPlayer());
			SkillTreeApi.syncSkills((EntityLivingBase) target, Collections.singletonList(e.getEntityPlayer()));
		}
	}

	@SubscribeEvent
	public void tickEvent(TickEvent.WorldTickEvent e) {
		debug4(e);
	}

	private void debug4(WorldTickEvent e) {
		if (e.phase == TickEvent.Phase.END) {
			MinecraftForge.EVENT_BUS.post(new SkillEvent.SkillTick(e.world));
		}
	}

	// @SubscribeEvent
	// public void tickEvent(TickEvent.ClientTickEvent e) {
	// debug5(e);
	// }
	//
	// private void debug5(ClientTickEvent e) {
	// if (ModMain.proxy.getWorld() != null && e.phase == TickEvent.Phase.END) {
	// World world = ModMain.proxy.getWorld();
	// MinecraftForge.EVENT_BUS.post(new
	// SkillEvent.SkillTick(ModMain.proxy.getWorld()));
	// }
	// }

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent e) {
		debug5(e);
		// EntityPlayer player = e.player;
		// if (e.phase == Phase.END && e.player.world.isRemote) {
		// MinecraftForge.EVENT_BUS.post(new SkillEvent.SkillTick(e.player.world));
		// ISkillHandler skillHandler = SkillTreeApi.getSkillHandler(player);
		// if (skillHandler.isDirty()) {
		// System.out.println("Dirty: " + skillHandler);
		// syncSkills(player, skillHandler);
		// skillHandler.clean();
		// }
		// }
	}

	private void debug5(PlayerTickEvent e) {
		
	}

	// private void syncSkills(EntityPlayer player, ISkillHandler skillHandler) {
	// List<EntityPlayer> receivers = new ArrayList<>(
	// ((WorldServer) player.world).getEntityTracker().getTrackingPlayers(player));
	// receivers.add(player);
	// SkillTreeApi.syncSkills(player, receivers);
	// }

}
