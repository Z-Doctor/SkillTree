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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * When you extend this class, make sure to add an empty constructor Do not add
 * guiFacotry to your mod, this class will handle registering itself. Treat like
 * a {@link EasyConfig}
 * 
 */
public class EasyConfigGui extends EasyConfig implements IModGuiFactory {
	public EasyConfig getEasyConfig() {
		return this;
	};

	public EasyConfigGui() {
	}

	public EasyConfigGui(FMLPreInitializationEvent e) {
		this(e, "Default");
	}

	public EasyConfigGui(FMLPreInitializationEvent e, String defaultCatergory) {
		super(e, defaultCatergory);
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
		getConfig().getCategoryNames().forEach(cat -> {
			elements.addAll(new ConfigElement(getConfig().getCategory(cat)).getChildElements());
		});
		return new GuiConfig(parentScreen, elements, getModid(), false, false, getTitle());
	}

	public String getTitle() {
		return getModid();
	}
}
