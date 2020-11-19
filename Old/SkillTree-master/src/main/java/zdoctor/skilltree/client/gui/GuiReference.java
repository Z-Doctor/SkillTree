package zdoctor.skilltree.client.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.ModMain;

@SideOnly(Side.CLIENT)
public class GuiReference {
	/** The location of the skill tree tabs texture */
	public static final ResourceLocation SKILL_TREE_TABS = new ResourceLocation(
			ModMain.MODID + ":textures/gui/skilltree/tabs.png");

	public static final ResourceLocation SKILL_TREE_BACKGROUND = new ResourceLocation(
			ModMain.MODID + ":textures/gui/skilltree/skill_tree.png");
}
