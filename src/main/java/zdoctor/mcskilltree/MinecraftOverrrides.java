package zdoctor.mcskilltree;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import zdoctor.mcskilltree.block.SkillWorkbench;
import zdoctor.skilltree.api.SkillTreeApi;

public class MinecraftOverrrides {
	
	@ObjectHolder(value = ModMain.MODID + ":workbench")
	public static final SkillWorkbench WORKBENCH = null;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void entityJoinedWorld(BlockEvent.PlaceEvent event) {
		if (event.getPlacedBlock().getBlock() instanceof BlockWorkbench) {
			if (event.getPlacedBlock().getBlock() instanceof SkillWorkbench)
				return;
			event.getWorld().setBlockState(event.getPos(), WORKBENCH.getDefaultState());
		}
	}

	@SubscribeEvent
	public void playerInteractBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.CRAFTING_TABLE) {
			event.getWorld().setBlockState(event.getPos(), WORKBENCH.getDefaultState());
		}
	}

	@SubscribeEvent
	public void playerAdvancementEarned(AdvancementEvent event) {
		SkillTreeApi.addSkillPoints(event.getEntityPlayer(), 1);
	}

}
