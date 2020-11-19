package zdoctor.skilltree.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.FutureTask;

import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
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
		debug(e);
	}

	public void debug(EntityJoinWorldEvent e) {
		if (e.getEntity() != null && e.getEntity() instanceof EntityLivingBase && !e.getEntity().isDead) {
			// System.out.println(e.getEntity() + " joined on side " +
			// (e.getWorld().isRemote ? "Client" : "Server"));
			if (!e.getWorld().isRemote) {
				ISkillHandler handler = SkillTreeApi.getSkillHandler((EntityLivingBase) e.getEntity());
				handler.reloadHandler();
				handler.markDirty();
			}
		}
	}

	@SubscribeEvent
	public void reloadCap(PlayerEvent.Clone e) {
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
		capClone.reloadHandler();

		// System.out.println("Reloading Cap: " + e.getOriginal().isDead);
		// System.out.println("Reloading Cap: " + e.getEntityPlayer().world);
		// System.out.println("capO: " + capOrginal.serializeNBT());
		// System.out.println("capC: " + capClone.serializeNBT());
	}

	@SubscribeEvent
	public void playerTracking(PlayerEvent.StartTracking e) {
		Entity target = e.getTarget();
		// System.out.println(e.getEntityPlayer().world + " Player Tracking: " +
		// e.getEntityPlayer());

		if (target != null && target instanceof EntityLivingBase) {
			// System.out.println("Sync Track: " + e.getEntityPlayer());
			List<EntityPlayer> recievers = new ArrayList<>();
			recievers.add(e.getEntityPlayer());
			if (target instanceof EntityPlayerMP)
				recievers.add((EntityPlayer) target);
			SkillTreeApi.syncSkills((EntityLivingBase) target, recievers);
		}
	}

	@SubscribeEvent
	public void tickEvent(TickEvent.WorldTickEvent e) {
		if (e.world != null && e.phase == TickEvent.Phase.END && !e.world.isRemote) {
			List<EntityLivingBase> livingList = e.world.getEntities(EntityLivingBase.class,
					entity -> !(entity instanceof EntityPlayer) && !entity.isDead);
			for (EntityLivingBase entity : livingList) {
				ISkillHandler handler = SkillTreeApi.getSkillHandler(entity);
				if (handler != null) {
					// System.out.println(entity + " Entity: " + e.side);
					handler.onTick(entity, entity.world);
				}
			}
		}
	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent e) {
		if (e.player != null && e.phase == TickEvent.Phase.END) {
			ISkillHandler handler = SkillTreeApi.getSkillHandler(e.player);
			if (handler != null) {
				// System.out.println("Player: " + e.side);
				handler.onTick(e.player, e.player.world);
			}

			if (e.side == Side.CLIENT) {
				List<EntityLivingBase> entityList = e.player.world.getEntities(EntityLivingBase.class,
						entity -> !entity.isDead);
				for (EntityLivingBase entity : entityList) {
					ISkillHandler entityHandler = SkillTreeApi.getSkillHandler(entity);
					if (entityHandler != null) {
						entityHandler.onTick(entity, entity.world);
					}
				}
			}
		}
	}

}
