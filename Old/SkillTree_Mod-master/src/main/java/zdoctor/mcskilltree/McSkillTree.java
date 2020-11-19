package zdoctor.mcskilltree;

import com.mojang.datafixers.DataFixUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.mcskilltree.blocks.SkillCraftingTable;
import zdoctor.mcskilltree.blocks.SkillFurnace;
import zdoctor.mcskilltree.events.SkillFurnaceEvent;
import zdoctor.mcskilltree.events.SkillWorkBenchEvent;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skills.SkillCapability;
import zdoctor.mcskilltree.skills.variants.CraftSkill;
import zdoctor.mcskilltree.skilltree.SkillTree;
import zdoctor.mcskilltree.skilltree.tabs.PlayerInfoTab;
import zdoctor.mcskilltree.skilltree.tabs.TestSkillTree;
import zdoctor.mcskilltree.world.storage.loot.conditions.HasSkill;

import java.util.Objects;

@Mod("mcskilltree")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class McSkillTree {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mcskilltree";
    // TODO Make config for starting points

    static {
        LootConditionManager.registerCondition(new HasSkill.Serializer());
    }

    @SubscribeEvent
    public static void setupCommon(FMLCommonSetupEvent event) {
        CraftSkill.init();
        SkillCapability.register();
    }

    @SubscribeEvent
    public static void createSkillTreeRegistry(RegistryEvent.NewRegistry event) {
        ResourceLocation SKILL_TREES_RES = new ResourceLocation("skill_trees");
        ResourceLocation SKILLS_RES = new ResourceLocation("skills");
        new RegistryBuilder<SkillTree>().setName(SKILL_TREES_RES).setType(SkillTree.class).create();
        new RegistryBuilder<Skill>().setName(SKILLS_RES).setType(Skill.class).setDefaultKey(new ResourceLocation("none")).create();
    }

    @SubscribeEvent
    public static void registerSkills(RegistryEvent.Register<Skill> event) {
        event.getRegistry().register(new Skill("none", Items.AIR));
    }

    @SubscribeEvent
    public static void registerSkillTree(RegistryEvent.Register<SkillTree> event) {
        event.getRegistry().register(new PlayerInfoTab());
        event.getRegistry().register(new TestSkillTree());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        if (!MinecraftForge.EVENT_BUS.post(new SkillWorkBenchEvent.OverrideVanillaEvent()))
            event.getRegistry().register(new SkillCraftingTable());
        if(!MinecraftForge.EVENT_BUS.post(new SkillFurnaceEvent.OverrideVanillaEvent()))
            event.getRegistry().register(new SkillFurnace());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        if (!MinecraftForge.EVENT_BUS.post(new SkillWorkBenchEvent.OverrideVanillaEvent())) {
            Item item = new BlockItem(Blocks.CRAFTING_TABLE, new Item.Properties().group(ItemGroup.DECORATIONS));
            item.setRegistryName(Objects.requireNonNull(Items.CRAFTING_TABLE.getRegistryName()));
            event.getRegistry().register(item);
        }

        if (!MinecraftForge.EVENT_BUS.post(new SkillFurnaceEvent.OverrideVanillaEvent())) {
            Item item = new BlockItem(Blocks.FURNACE, new Item.Properties().group(ItemGroup.DECORATIONS));
            item.setRegistryName(Objects.requireNonNull(Items.FURNACE.getRegistryName()));
            event.getRegistry().register(item);
        }
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        if (!MinecraftForge.EVENT_BUS.post(new SkillFurnaceEvent.OverrideVanillaEvent())) {
            TileEntityType<?> type = TileEntityType.Builder.create(SkillFurnace.SkillFurnaceTileEntity::new, Blocks.FURNACE).build(null);
            type.setRegistryName(TileEntityType.FURNACE.getRegistryName());
            event.getRegistry().register(type);
        }
    }


}
