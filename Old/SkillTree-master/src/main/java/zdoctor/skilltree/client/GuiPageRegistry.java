package zdoctor.skilltree.client;

import java.util.HashMap;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.client.gui.GuiSkillPage;
import zdoctor.skilltree.skills.pages.SkillPageBase;

/**
 * Use this class to render custom gui pages to use insread of the default
 *
 */
@SideOnly(Side.CLIENT)
public class GuiPageRegistry {
	protected static final HashMap<Class<? extends SkillPageBase>, Class<? extends GuiSkillPage>> GUI_REGISTRY = new HashMap<>();

	public static void registerGui(Class<? extends SkillPageBase> page, Class<? extends GuiSkillPage> pageGui) {
		if (!GUI_REGISTRY.containsKey(page))
			GUI_REGISTRY.put(page, pageGui);
		else
			FMLLog.bigWarning("Attempt to override gui '{}' if that was the intention use overrideGui",
					GUI_REGISTRY.get(page));
	}

	public static void overrideGui(Class<? extends SkillPageBase> page, Class<? extends GuiSkillPage> pageGui) {
		if (GUI_REGISTRY.containsKey(page))
			GUI_REGISTRY.put(page, pageGui);
		else {
			FMLLog.bigWarning("Attempt to override gui '{}' when gui did not exist! Adding...", GUI_REGISTRY.get(page));
			GUI_REGISTRY.put(page, pageGui);
		}
	}

	public static Class<? extends GuiSkillPage> getGui(Class<? extends SkillPageBase> page) {
		if (GUI_REGISTRY.containsKey(page))
			return GUI_REGISTRY.get(page);
		else
			return GuiSkillPage.class;
	}
}
