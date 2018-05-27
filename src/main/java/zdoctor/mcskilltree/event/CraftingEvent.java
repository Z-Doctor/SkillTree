package zdoctor.mcskilltree.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

@HasResult
public class CraftingEvent extends Event {

	private EntityPlayer player;
	private IRecipe recipe;
	private ItemStack result;

	public CraftingEvent(EntityPlayer player, IRecipe recipe, ItemStack result) {
		this.player = player;
		this.recipe = recipe;
		this.result = result;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public IRecipe getRecipe() {
		return recipe;
	}

	public ItemStack getRecipeResult() {
		return result == null ? ItemStack.EMPTY : result;
	}

	public void setRecipeResult(ItemStack result) {
		this.result = result;
	}

}
