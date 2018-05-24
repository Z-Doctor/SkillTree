package zdoctor.skilltree.skills;

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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.ISkillHandler;
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
		if (e.getEntity() instanceof EntityLivingBase) {
			SkillTreeApi.getSkillHandler((EntityLivingBase) e.getEntity()).setOwner((EntityLivingBase) e.getEntity());
			if (e.getEntity() instanceof EntityPlayer)
				SkillTreeApi.syncSkills((EntityLivingBase) e.getEntity());
		}
	}

	@SubscribeEvent
	public void reloadCap(PlayerEvent.Clone e) {
		ModMain.proxy.config.open();
		ModMain.proxy.config.sync();
		if (e.isWasDeath() && !ModMain.proxy.keepSkillsOnDeath.getValue()) {
			ModMain.proxy.log.debug("KeepSkillsOnDeath is diabled, not sysncing {} skills",
					e.getEntityPlayer().getDisplayName());
			return;
		}

		ISkillHandler capOrginal = SkillTreeApi.getSkillHandler(e.getOriginal());
		SkillTreeApi.getSkillHandler(e.getEntityPlayer()).deserializeNBT(capOrginal.serializeNBT());
		SkillTreeApi.syncSkills(e.getEntityPlayer());
	}

	@SubscribeEvent
	public void playerTracking(PlayerEvent.StartTracking e) {
		Entity target = e.getTarget();
		if (target instanceof EntityPlayerMP)
			SkillTreeApi.syncSkills(e.getEntityPlayer());
	}

	@SubscribeEvent
	public void tickEvent(TickEvent.WorldTickEvent e) {
		if (e.phase == TickEvent.Phase.END) {
			MinecraftForge.EVENT_BUS.post(new SkillEvent.SkillTick());
		}
	}

}
