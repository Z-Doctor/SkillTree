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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.ImageAssets;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.client.ClientCapabilityProvider;
import zdoctor.zskilltree.client.KeyBindings;
import zdoctor.zskilltree.skilltree.data.criterion.ExtendedCriteriaTriggers;
import zdoctor.zskilltree.network.NetworkSerializationRegistry;
import zdoctor.zskilltree.network.SkillTreePacketHandler;
import zdoctor.zskilltree.skilltree.commands.SkillTreeCommand;
import zdoctor.zskilltree.skilltree.commands.SkillTreeEntityOptions;
import zdoctor.zskilltree.skilltree.data.trackers.SkillTreeTracker;
import zdoctor.zskilltree.skilltree.data.managers.SkillManager;
import zdoctor.zskilltree.skilltree.data.managers.SkillPageManager;
import zdoctor.zskilltree.skilltree.data.providers.CapabilitySkillTreeProvider;
import zdoctor.zskilltree.skilltree.data.providers.SkillPageProvider;
import zdoctor.zskilltree.skilltree.data.providers.SkillProvider;
import zdoctor.zskilltree.skilltree.loot.conditions.AdditionalConditions;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skill.SkillTreeDataManager;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;
import zdoctor.zskilltree.skilltree.skillpages.SkillPageDisplayInfo;

import java.util.Map;
import java.util.function.Function;

@Mod(ModMain.MODID)
public final class ModMain {
    public static final String MODID = "zskilltree";
    public static final ResourceLocation SKILL_TREE_CAPABILITY_ID = new ResourceLocation(ModMain.MODID, "skill_capability");
    private static final Logger LOGGER = LogManager.getLogger();
    @CapabilityInject(ISkillTreeTracker.class)
    public static final Capability<ISkillTreeTracker> SKILL_TREE_CAPABILITY = null;
    private static ModMain INSTANCE = null;

    private CapabilitySkillTreeProvider capabilityProvider;

    private SkillTreeDataManager skillTreeDataManager;

    private SkillPageManager skillPageManager;

    private SkillManager skillManager;
    private Map<String, Function<PacketBuffer, CriterionTracker>> criterionMappings;

    private SimpleChannel packetChannel;

    public ModMain() {
        if (INSTANCE != null)
            return;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doServerStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::createProviders);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::createRegistries);

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(DataSerializerEntry.class, this::registerDataSerializers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(GlobalLootModifierSerializer.class, this::registerGlobalLootModifierSerializers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(SkillPage.class, this::createSkillPages);

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerClone);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::attachCapability);

        initBootstrap();
        INSTANCE = this;
    }

    public static ModMain getInstance() {
        return INSTANCE;
    }

    private static void initBootstrap() {
        SkillTreeEntityOptions.register();
        AdditionalConditions.init();
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

    private void doServerStuff(FMLDedicatedServerSetupEvent event) {
        capabilityProvider = new CapabilitySkillTreeProvider();
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

        packetChannel = SkillTreePacketHandler.createChannel();

        ExtendedCriteriaTriggers.init();
        criterionMappings = NetworkSerializationRegistry.registerMapping(CriterionTracker.class);
        NetworkSerializationRegistry.register(SkillPage.class, SkillPage::new, CriterionTracker.class);
        NetworkSerializationRegistry.register(ModMain.MODID + ":skill", Skill::new, CriterionTracker.class);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        capabilityProvider = new ClientCapabilityProvider();
        KeyBindings.initBindings();
        event.getMinecraftSupplier().get().getModelManager().getBlockModelShapes().reloadModels();
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
        event.addListener(skillManager = new SkillManager(lootPredicateManager));
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
        LazyOptional<ISkillTreeTracker> oldCap = event.getPlayer().getCapability(SKILL_TREE_CAPABILITY);
        if (!oldCap.isPresent())
            return;
        LazyOptional<ISkillTreeTracker> newCap = event.getPlayer().getCapability(SKILL_TREE_CAPABILITY);
        if (!newCap.isPresent())
            return;
        // TODO Add config for keep on death(Default: true)

        oldCap.ifPresent(oldHandler -> newCap.ifPresent(newHandler ->
                newHandler.deserializeNBT(oldHandler.serializeNBT())));
    }
}
