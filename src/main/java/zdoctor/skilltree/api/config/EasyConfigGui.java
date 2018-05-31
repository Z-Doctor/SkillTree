package zdoctor.skilltree.api.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * When you extend this class, make sure to add an empty constructor Do not add
 * guiFacotry to your mod, this class will handle registering itself. Treat like
 * a {@link EasyConfig}
 * 
 */
@SideOnly(Side.CLIENT)
public class EasyConfigGui implements IModGuiFactory {
	private EasyConfig config;

	public EasyConfig getEasyConfig() {
		return config;
	};

	public EasyConfigGui(EasyConfig config) {
		this.config = config;
		GuiConfigHandler.register(this);
	}

	@Override
	public void initialize(Minecraft minecraftInstance) {
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		List<IConfigElement> elements = new ArrayList<>();
		getEasyConfig().getConfig().getCategoryNames().forEach(cat -> {
			elements.addAll(new ConfigElement(getEasyConfig().getConfig().getCategory(cat)).getChildElements());
		});
		return new GuiConfig(parentScreen, elements, getEasyConfig().getModid(), false, false, getTitle());
	}

	public String getTitle() {
		return getEasyConfig().getModid();
	}
}
