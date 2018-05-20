package zdoctor.mcskilltree.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import zdoctor.mcskilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.ItemSkillPoint;

public class ItemSkillPointGem extends ItemSkillPoint {
	public ItemSkillPointGem() {
		setUnlocalizedName("itemSkillPointGem");
		setRegistryName(ModMain.MODID + ":itemSkillPointGem");
		setCreativeTab(CreativeTabs.MISC);
	}

}
