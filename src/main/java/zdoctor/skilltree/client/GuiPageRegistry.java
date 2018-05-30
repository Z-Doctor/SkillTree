package zdoctor.skilltree.client;

import java.util.HashMap;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.api.skills.interfaces.ISkillPage;
import zdoctor.skilltree.api.skills.page.SkillPageBase;
import zdoctor.skilltree.client.gui.GuiSkillPage;

/**
 * Use this class to render custom gui pages to use insread of the default
 *
 */
@SideOnly(Side.CLIENT)
public class GuiPageRegistry {
	protected static final HashMap<Class<? extends ISkillPage>, Class<? extends GuiSkillPage>> GUI_REGISTRY = new HashMap<>();

	public static void registerGui(Class<? extends ISkillPage> page, Class<? extends GuiSkillPage> pageGui) {
		if (!GUI_REGISTRY.containsKey(page))
			GUI_REGISTRY.put(page, pageGui);
		else
			FMLLog.bigWarning("Attempt to override gui '{}' if that was the intention use overrideGui",
					GUI_REGISTRY.get(page));
	}

	public static void overrideGui(Class<? extends ISkillPage> page, Class<? extends GuiSkillPage> pageGui) {
		if (GUI_REGISTRY.containsKey(page))
			GUI_REGISTRY.put(page, pageGui);
		else {
			FMLLog.bigWarning("Attempt to override gui '{}' when gui did not exist! Adding...", GUI_REGISTRY.get(page));
			GUI_REGISTRY.put(page, pageGui);
		}
	}

	public static Class<? extends GuiSkillPage> getGui(Class<? extends ISkillPage> page) {
		if (GUI_REGISTRY.containsKey(page))
			return GUI_REGISTRY.get(page);
		else
			return GuiSkillPage.class;
	}
}
