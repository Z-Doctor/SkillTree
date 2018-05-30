package zdoctor.skilltree.api.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class GuiConfigHandler {
	protected static final Map<ModContainer, EasyConfigGui> GUIMAP = new HashMap<>();

	public static void fmlPostInit() {
		Field guiFactories;
		try {
			guiFactories = FMLClientHandler.class.getDeclaredField("guiFactories");
			guiFactories.setAccessible(true);
			BiMap<ModContainer, IModGuiFactory> guiMap = (BiMap<ModContainer, IModGuiFactory>) guiFactories
					.get(FMLClientHandler.instance());
			GUIMAP.entrySet().forEach(set -> {
				guiMap.forcePut(set.getKey(), set.getValue());
			});
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void register(EasyConfigGui easyConfigGui) {
		GUIMAP.put(getActiveMod(), easyConfigGui);
	}

	public static ModContainer getActiveMod() {
		return Loader.instance().activeModContainer();
	}
}
