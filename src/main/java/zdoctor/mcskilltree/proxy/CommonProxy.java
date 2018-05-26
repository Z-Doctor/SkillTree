package zdoctor.mcskilltree.proxy;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import zdoctor.mcskilltree.Config;
import zdoctor.mcskilltree.ModMain;
import zdoctor.mcskilltree.block.SkillWorkbench;
import zdoctor.mcskilltree.item.ItemSkillPointGem;
import zdoctor.mcskilltree.skills.tabs.MCSkillTreeTabs;
import zdoctor.skilltree.EasyConfig;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.events.SkillHandlerEvent;

public abstract class CommonProxy {

	@ObjectHolder(value = ModMain.MODID + ":itemSkillPointGem")
	public static final Item ITEM_SKILL_POINT_GEM = null;
	public EasyConfig config;

	public void preInit(FMLPreInitializationEvent e) {
		MCSkillTreeTabs.init();
		MinecraftForge.EVENT_BUS.register(this);
		this.config = new EasyConfig(e);
		MinecraftForge.EVENT_BUS.register(new Config());
	}

	public void init(FMLInitializationEvent e) {
	}

	public void postInit(FMLPostInitializationEvent e) {
		Config.postInit(e);
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> e) {
		e.getRegistry().register(new ItemSkillPointGem());
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> e) {
		e.getRegistry().register(new SkillWorkbench());
	}

	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> e) {
		Ingredient dIngredient = Ingredient.fromItem(Item.getItemFromBlock(Blocks.DIAMOND_BLOCK));
		Ingredient eIngredient = Ingredient.fromItem(Item.getItemFromBlock(Blocks.EMERALD_BLOCK));
		ShapedRecipes recipe = new ShapedRecipes(
				"", 3, 3, NonNullList.from(Ingredient.EMPTY, dIngredient, eIngredient, dIngredient, eIngredient,
						dIngredient, eIngredient, dIngredient, eIngredient, dIngredient),
				new ItemStack(ITEM_SKILL_POINT_GEM));
		recipe.setRegistryName(ITEM_SKILL_POINT_GEM.getRegistryName());
		e.getRegistry().register(recipe);
	}

	@SubscribeEvent
	public void firstLoad(SkillHandlerEvent.FirstLoadEvent e) {
		SkillTreeApi.addSkillPoints(e.owner, 5);
	}

}
