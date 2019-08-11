package zdoctor.skilltree.api.skills;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import zdoctor.skilltree.api.SkillTreeApi;

public class ItemSkillPoint extends Item {

	protected int skillPointReward;

	public ItemSkillPoint() {
		this(1);
	}

	public ItemSkillPoint(int skillPointsGiven) {
		skillPointReward = skillPointsGiven;
	}

	@Override
	public ActionResult<net.minecraft.item.ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn,
			EnumHand handIn) {
		ItemStack itemStack = playerIn.getHeldItem(handIn);
		if (!(itemStack.getItem() instanceof ItemSkillPoint) || itemStack.isEmpty()) // Just use the api
			return super.onItemRightClick(worldIn, playerIn, handIn);
		if (!playerIn.capabilities.isCreativeMode)
			itemStack.shrink(1);
		SkillTreeApi.addSkillPoints(playerIn, skillPointReward); // <- This
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}
