package zdoctor.mcskilltree.skills.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import zdoctor.mcskilltree.ModMain;
import zdoctor.mcskilltree.event.CraftingEvent;
import zdoctor.mcskilltree.skills.CraftSkill;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.Skill;

public class ItemCrafterSkill extends CraftSkill {

	private Class<? extends Item> itemClass;

	public ItemCrafterSkill(Class<? extends Item> weaponClass, String name, Item icon) {
		this(weaponClass, name, new ItemStack(icon));
	}

	public ItemCrafterSkill(Class<? extends Item> weaponClass, String name, ItemStack icon) {
		super(name, icon);
		this.itemClass = weaponClass;
	}

	public Class<? extends Item> getItemClass() {
		return itemClass;
	}

	@SubscribeEvent
	public void craftEvent(CraftingEvent event) {
		if (event.getRecipeResult().getItem().getClass().isAssignableFrom(getItemClass())) {
			if (!SkillTreeApi.hasSkill(event.getPlayer(), this))
				event.setResult(Result.DENY);
		}
	}

}
