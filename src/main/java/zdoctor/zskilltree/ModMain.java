package zdoctor.zskilltree;

import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.ImageAssets;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.client.KeyBindings;
import zdoctor.zskilltree.client.SinglePlayerCapabilityProvider;
import zdoctor.zskilltree.config.SkillTreeConfig;
import zdoctor.zskilltree.config.SkillTreeGameRules;
import zdoctor.zskilltree.network.NetworkSerializationRegistry;
import zdoctor.zskilltree.network.SkillTreePacketHandler;
import zdoctor.zskilltree.skilltree.commands.SkillTreeCommand;
import zdoctor.zskilltree.skilltree.commands.SkillTreeEntityOptions;
import zdoctor.zskilltree.skilltree.criterion.ExtendedCriteriaTriggers;
import zdoctor.zskilltree.skilltree.criterion.Skill;
import zdoctor.zskilltree.skilltree.criterion.SkillPage;
import zdoctor.zskilltree.skilltree.displays.SkillPageDisplayInfo;
import zdoctor.zskilltree.skilltree.events.IntegratedServerTick;
import zdoctor.zskilltree.skilltree.loot.conditions.AdditionalConditions;
import zdoctor.zskilltree.skilltree.managers.SkillManager;
import zdoctor.zskilltree.skilltree.managers.SkillPageManager;
import zdoctor.zskilltree.skilltree.managers.SkillTreeDataManager;
import zdoctor.zskilltree.skilltree.providers.MultiplayerSkillTreeProvider;
import zdoctor.zskilltree.skilltree.providers.SkillPageProvider;
import zdoctor.zskilltree.skilltree.providers.SkillProvider;
import zdoctor.zskilltree.skilltree.trackers.SkillTreeTracker;

import java.util.Map;
import java.util.function.Function;

@Mod(ModMain.MODID)
public final class ModMain {
    public static final String MODID = "zskilltree";
    public static final ResourceLocation SKILL_TREE_CAPABILITY_ID = new ResourceLocation(ModMain.MODID, "skill_capability");
    @CapabilityInject(ISkillTreeTracker.class)
    public static final Capability<ISkillTreeTracker> SKILL_TREE_CAPABILITY = null;
    private static final Logger LOGGER = LogManager.getLogger();
    private static ModMain INSTANCE = null;

    private Map<String, Function<PacketBuffer, CriterionTracker>> criterionMappings;
    private MultiplayerSkillTreeProvider capabilityProvider;

    private SkillTreeDataManager skillTreeDataManager;

    private SkillPageManager skillPageManager;

    private SkillManager skillManager;

    private SimpleChannel packetChannel;

    public ModMain() {
        if (INSTANCE != null)
            return;
        INSTANCE = this;
        initBootstrap();


        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::createProviders);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::createRegistries);

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(DataSerializerEntry.class, this::registerDataSerializers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(GlobalLootModifierSerializer.class, this::registerGlobalLootModifierSerializers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(SkillPage.class, this::createSkillPages);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::setupIntegratedServer);
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> this::setupServer);
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerClone);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::attachCapability);

    }

    public static ModMain getInstance() {
        return INSTANCE;
    }

    private static void initBootstrap() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SkillTreeConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SkillTreeConfig.serverSpec);


        SkillTreeGameRules.init();
        SkillTreeEntityOptions.register();
        AdditionalConditions.init();
    }


    private void setupIntegratedServer() {
        Mixins.addConfiguration("META-INF/mixin_config.json");
        MinecraftForge.EVENT_BUS.addListener(this::onIntegratedServerTick);
    }

    private void setupServer() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerTick);
    }

    public SkillTreeDataManager getSkillTreeDataManager() {
        return skillTreeDataManager;
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

    public Map<String, Function<PacketBuffer, CriterionTracker>> getCriterionMappings() {
        return criterionMappings;
    }

    public SimpleChannel getPacketChannel() {
        return packetChannel;
    }

    // Mod Events
    private void createRegistries(RegistryEvent.NewRegistry event) {
        LOGGER.info("Creating Registries");
        new RegistryBuilder<ImageAsset>().setType(ImageAsset.class).setName(new ResourceLocation(MODID, "image_assets"))
                .setDefaultKey(ImageAssets.MISSING.getRegistryName()).create();

        new RegistryBuilder<SkillPage>().setType(SkillPage.class).setName(new ResourceLocation(MODID, "skill_page"))
                .setDefaultKey(SkillPage.NONE.getRegistryName()).create();

        new RegistryBuilder<Skill>().setType(Skill.class).setName(new ResourceLocation(MODID, "skill"))
                .setDefaultKey(Skill.NONE.getRegistryName()).create();

    }

    private void createSkillPages(RegistryEvent.Register<SkillPage> event) {
        LOGGER.info("Registering Skill Pages");
        event.getRegistry().register(SkillPage.builder().atIndex(0)
                .withDisplay(new SkillPageDisplayInfo(Items.WRITABLE_BOOK.getDefaultInstance(),
                        new TranslationTextComponent("skillpage.player_info.title"),
                        new TranslationTextComponent("skillpage.player_info.description"),
                        SkillPageAlignment.HORIZONTAL))
                .build(new ResourceLocation(MODID, "player_info")));

    }

    private void createProviders(GatherDataEvent event) {
        event.getGenerator().addProvider(new SkillPageProvider(event.getGenerator()));
        event.getGenerator().addProvider(new SkillProvider(event.getGenerator()));
    }

    private void setup(final FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ISkillTreeTracker.class, new Capability.IStorage<ISkillTreeTracker>() {
            @Override
            public INBT writeNBT(Capability<ISkillTreeTracker> capability, ISkillTreeTracker instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<ISkillTreeTracker> capability, ISkillTreeTracker instance, Direction side, INBT nbt) {
                if (nbt instanceof CompoundNBT)
                    instance.deserializeNBT((CompoundNBT) nbt);
            }
        }, SkillTreeTracker::new);

        capabilityProvider = DistExecutor.safeRunForDist(() -> SinglePlayerCapabilityProvider::new, () -> MultiplayerSkillTreeProvider::new);
        skillTreeDataManager = new SkillTreeDataManager();

        packetChannel = SkillTreePacketHandler.createChannel();

        ExtendedCriteriaTriggers.init();
        criterionMappings = NetworkSerializationRegistry.registerMapping(CriterionTracker.class);
        NetworkSerializationRegistry.register(SkillPage.class, SkillPage::new, CriterionTracker.class);
        NetworkSerializationRegistry.register(Skill.class, Skill::new, CriterionTracker.class);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        KeyBindings.initBindings();
        event.getMinecraftSupplier().get().getModelManager().getBlockModelShapes().reloadModels();
    }

    private void registerDataSerializers(RegistryEvent.Register<DataSerializerEntry> event) {
    }

    private void registerGlobalLootModifierSerializers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        LootPredicateManager lootPredicateManager = event.getDataPackRegistries().getLootPredicateManager();
        event.addListener(skillManager = new SkillManager(lootPredicateManager));
        event.addListener(skillPageManager = new SkillPageManager(skillManager, lootPredicateManager));
        event.addListener(new ReloadListener<Void>() {
            @Override
            protected Void prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
                return null;
            }

            @Override
            protected void apply(Void objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
                skillTreeDataManager.reload();
            }
        });
        SkillTreeCommand.register(event.getDataPackRegistries().getCommandManager().getDispatcher());
        skillTreeDataManager.reload();
    }

    private void onServerStarted(FMLServerStartedEvent event) {
        skillTreeDataManager.onServerStarted(event.getServer());
    }

    private void onServerTick(TickEvent.ServerTickEvent event) {
        skillTreeDataManager.onServerTick(event.side, event.phase);
    }

    private void onIntegratedServerTick(IntegratedServerTick event) {
        skillTreeDataManager.onServerTick(LogicalSide.SERVER, event.phase);
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity && INSTANCE != null)
            skillTreeDataManager.playerLoggedIn((ServerPlayerEntity) event.getPlayer());
    }

    private void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity && INSTANCE != null)
            skillTreeDataManager.playerLoggedOut((ServerPlayerEntity) event.getPlayer());
    }

    private void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        // TODO Add a config filter for this and who and what skill trees are attached to
        ICapabilityProvider provider = capabilityProvider.createProvider(event.getObject());
        if (provider != null)
            event.addCapability(SKILL_TREE_CAPABILITY_ID, provider);
    }

    private void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath())
            return;
        skillTreeDataManager.onPlayerClone(event.getOriginal(), event.getPlayer());
    }
}
