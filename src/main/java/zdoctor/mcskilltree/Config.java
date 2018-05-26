package zdoctor.mcskilltree;

import java.util.HashMap;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.init.Blocks;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import zdoctor.mcskilltree.block.SkillWorkbench;
import zdoctor.skilltree.EasyConfig;

public class Config {
	public static final HashMap<ResourceLocation, EasyConfig.BooleanProperty> SKILL_PROPS = new HashMap<>();
	public static final HashMap<ResourceLocation, EasyConfig.IntProperty> SKILL_POINT_PROPS = new HashMap<>();
	public static final HashMap<ResourceLocation, EasyConfig.IntProperty> LEVEL_PROPS = new HashMap<>();

	public static void postInit(FMLPostInitializationEvent e) {
		GameRegistry.findRegistry(IRecipe.class).getEntries().forEach(entry -> {
			EasyConfig.BooleanProperty prop = new EasyConfig.BooleanProperty(ModMain.proxy.config, "recipes",
					"RequiresSkill:" + entry.getKey(), true);
			EasyConfig.IntProperty prop1 = new EasyConfig.IntProperty(ModMain.proxy.config, "skill_point_requirement",
					"SkillPointsRequired:" + entry.getKey(), 0, 1, Integer.MAX_VALUE);
			EasyConfig.IntProperty prop2 = new EasyConfig.IntProperty(ModMain.proxy.config, "level_requirement",
					"LevelsRequired:" + entry.getKey(), 0, 1, Integer.MAX_VALUE);

			SKILL_PROPS.put(entry.getKey(), prop);
			SKILL_POINT_PROPS.put(entry.getKey(), prop1);
			LEVEL_PROPS.put(entry.getKey(), prop2);
		});
	}

	@ObjectHolder(value = ModMain.MODID + ":workbench")
	public static final SkillWorkbench WORKBENCH = null;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void entityJoinedWorld(BlockEvent.PlaceEvent event) {
		debug(event);
	}

	public void debug(BlockEvent.PlaceEvent event) {
		if (event.getPlacedBlock().getBlock() instanceof BlockWorkbench) {
			if (event.getPlacedBlock().getBlock() instanceof SkillWorkbench)
				return;
			// event.setCanceled(true);
			event.getWorld().setBlockState(event.getPos(), WORKBENCH.getDefaultState());
		}
	}

	@SubscribeEvent
	public void playerInteractBlock(PlayerInteractEvent.RightClickBlock event) {
		debug(event);
	}

	public void debug(PlayerInteractEvent.RightClickBlock event) {
		if (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.CRAFTING_TABLE) {
			event.getWorld().setBlockState(event.getPos(), WORKBENCH.getDefaultState());
		}
	}

}
