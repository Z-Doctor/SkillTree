package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zdoctor.mcskilltree.event.CraftingEvent;
import zdoctor.mcskilltree.skills.CraftSkill;
import zdoctor.skilltree.api.SkillTreeApi;

public class ItemCrafterSkill extends CraftSkill {

	private Class<? extends Item> itemClass;
	private Block block;

	public ItemCrafterSkill(Block block, String name, Item icon) {
		this(Item.getItemFromBlock(block).getClass(), name, icon);
		this.block = block;
	}

	public ItemCrafterSkill(Block block, String name, ItemStack icon) {
		this(Item.getItemFromBlock(block).getClass(), name, icon);
		this.block = block;
	}

	public ItemCrafterSkill(Class<? extends Item> itemClass, String name, Item icon) {
		this(itemClass, name, new ItemStack(icon));
	}

	public ItemCrafterSkill(Class<? extends Item> itemClass, String name, ItemStack icon) {
		super(name, icon);
		this.itemClass = itemClass;
	}

	public Class<? extends Item> getItemClass() {
		return itemClass;
	}

	@SubscribeEvent
	public void debug(CraftingEvent event) {
		craftEvent(event);
	}

	public void craftEvent(CraftingEvent event) {
		if (getItemClass() == ItemBlock.class
				&& ItemBlock.class.isAssignableFrom(event.getRecipeResult().getItem().getClass())) {
			Block block1 = Block.getBlockFromItem(event.getRecipeResult().getItem());
			if (block == block1 && !SkillTreeApi.hasSkill(event.getPlayer(), this))
				event.setResult(Result.DENY);
		} else if (getItemClass().isAssignableFrom(event.getRecipeResult().getItem().getClass())) {
			System.out.println("Matching class: " + getRegistryName());
			// System.out.println("Item: " + getItemClass().getSimpleName() + " RecipeClass:
			// "
			// + event.getRecipeResult().getItem().getClass().getSimpleName() + " Match: "
			// +
			// getItemClass().isAssignableFrom(event.getRecipeResult().getItem().getClass()));
			if (!SkillTreeApi.hasSkill(event.getPlayer(), this))
				event.setResult(Result.DENY);
		}
	}

}
