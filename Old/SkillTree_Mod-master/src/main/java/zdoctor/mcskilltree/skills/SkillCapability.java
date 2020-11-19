package zdoctor.mcskilltree.skills;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.api.ClientSkillApi;
import zdoctor.mcskilltree.api.ISkillHandler;
import zdoctor.mcskilltree.api.SkillApi;

@Mod.EventBusSubscriber
public class SkillCapability {
    public static final ResourceLocation SKILL_CAPABILITY_KEY = new ResourceLocation(McSkillTree.MODID, "skill_capability");

    @CapabilityInject(ISkillHandler.class)
    protected static Capability<ISkillHandler> SKILL_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(ISkillHandler.class, new SkillHandler(), SkillHandler::new);
    }

    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            ISkillHandler skillHandler = SkillApi.getSkillHandler(event.getEntityLiving());
            if (skillHandler.isDirty())
                skillHandler.updateSkillData();
        }
    }


    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // TODO Add event
            LazyOptional<ISkillHandler> oldCap = event.getOriginal().getCapability(SKILL_CAPABILITY);
            LazyOptional<ISkillHandler> newCap = event.getPlayer().getCapability(SKILL_CAPABILITY);
            if (oldCap.isPresent() && newCap.isPresent()) {
                ISkillHandler oldSkillHandler = oldCap.orElseThrow(() -> new NullPointerException("Old ISkillHandler is null, but we just checked?"));
                ISkillHandler newSkillHandler = newCap.orElseThrow(() -> new NullPointerException("New ISkillHandler is null, but we just checked?"));
                newSkillHandler.onPlayerRespawn(oldSkillHandler);
            }
        }
    }

    @SubscribeEvent
    public static void onAttachSkillCap(AttachCapabilitiesEvent<Entity> attachEvent) {
        Entity entity = attachEvent.getObject();
        if (entity instanceof PlayerEntity && !entity.getCapability(SKILL_CAPABILITY).isPresent()) {
            ISkillHandler skillHandler = new SkillHandler((PlayerEntity) entity);
            // TODO Attach Event
            attachEvent.addCapability(SKILL_CAPABILITY_KEY, skillHandler);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        SkillApi.updateSkillData(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        SkillApi.updateSkillData(event.getPlayer());
    }

}
