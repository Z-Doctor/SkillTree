package zdoctor.zskilltree;

import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.advancements.ExtendedCriteriaTriggers;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.client.KeyBindings;
import zdoctor.zskilltree.commands.SkillTreeCommand;
import zdoctor.zskilltree.data.SkillPageProvider;
import zdoctor.zskilltree.data.SkillProvider;
import zdoctor.zskilltree.handlers.CapabilitySkillHandler;
import zdoctor.zskilltree.manager.SkillManager;
import zdoctor.zskilltree.manager.SkillPageManager;
import zdoctor.zskilltree.network.ZSkillTreePacketHandler;
import zdoctor.zskilltree.skill.SkillTreeDataManager;

@Mod(ModMain.MODID)
public final class ModMain {
    public static final String MODID = "zskilltree";
    private static final Logger LOGGER = LogManager.getLogger();
    @CapabilityInject(ISkillTreeTracker.class)
    public static Capability<ISkillTreeTracker> SKILLTREE_CAPABILITY = null;
    private static ModMain INSTANCE = null;

    private SkillTreeDataManager skillTreeDataManager;
    private SkillPageManager skillPageManager;
    private SkillManager skillManager;

    public ModMain() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::createProviders);

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(DataSerializerEntry.class, this::registerDataSerializers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(GlobalLootModifierSerializer.class, this::registerGlobalLootModifierSerializers);

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedOut);

        if (INSTANCE == null)
            INSTANCE = this;
    }

    public static ModMain getInstance() {
        return INSTANCE;
    }

    public SkillPageManager getSkillPageManager() {
        return skillPageManager;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public SkillTreeDataManager getPlayerSkillDataManager() {
        return skillTreeDataManager;
    }

    private void createProviders(GatherDataEvent event) {
        event.getGenerator().addProvider(new SkillPageProvider(event.getGenerator()));
        event.getGenerator().addProvider(new SkillProvider(event.getGenerator()));
    }

    private void setup(final FMLCommonSetupEvent event) {
        ZSkillTreePacketHandler.registerPackets();
        CapabilitySkillHandler.register();
        ExtendedCriteriaTriggers.init();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        KeyBindings.initBindings();
    }

    private void registerDataSerializers(RegistryEvent.Register<DataSerializerEntry> event) {
    }

    private void registerGlobalLootModifierSerializers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
    }

    private void onServerStarting(FMLServerStartingEvent event) {
        skillTreeDataManager = new SkillTreeDataManager();
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        LootPredicateManager lootPredicateManager = event.getDataPackRegistries().getLootPredicateManager();
        event.addListener(skillManager = new SkillManager());
        event.addListener(skillPageManager = new SkillPageManager(skillManager, lootPredicateManager));
        event.addListener(new ReloadListener<Void>() {
            @Override
            protected Void prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
                return null;
            }

            @Override
            protected void apply(Void objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
                if (skillTreeDataManager != null)
                    skillTreeDataManager.reload();
            }
        });
        SkillTreeCommand.register(event.getDataPackRegistries().getCommandManager().getDispatcher());

    }

    private void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayerEntity && INSTANCE != null) {
            skillTreeDataManager.onPlayerTick((ServerPlayerEntity) event.player);
        }
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity && INSTANCE != null)
            skillTreeDataManager.playerLoggedIn((ServerPlayerEntity) event.getPlayer(), skillPageManager);
    }

    private void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity && INSTANCE != null)
            skillTreeDataManager.playerLoggedOut((ServerPlayerEntity) event.getPlayer());
    }

}
