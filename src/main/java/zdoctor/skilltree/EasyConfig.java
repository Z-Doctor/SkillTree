package zdoctor.skilltree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Call in your preInit
 */
public class EasyConfig {
	private static final Map<String, EasyConfig> CONFIG_REGISTRY = new HashMap<>();
	protected final ArrayList<Property> PROPERTIES = new ArrayList<>();

	protected String modid;
	protected Configuration config;
	protected String defaultCatergory;
	protected final Logger log;

	public EasyConfig(FMLPreInitializationEvent e) {
		this(e, "Default");
	}

	public EasyConfig(FMLPreInitializationEvent e, String defaultCatergory) {
		log = e.getModLog();
		log.debug("Created Config");
		
		this.defaultCatergory = defaultCatergory;
		modid = Loader.instance().activeModContainer().getModId();
		config = new Configuration(e.getSuggestedConfigurationFile());
		open();
		CONFIG_REGISTRY.put(modid, this);
	}

	public Configuration getConfig() {
		return config;
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (CONFIG_REGISTRY.get(event.getModID()) != null) {
			CONFIG_REGISTRY.get(event.getModID()).sync();
		}
	}

	public void open() {
		config.load();
		PROPERTIES.forEach(Property::save);
		log.debug("Config Opened");
	}

	/**
	 * Called when changes are made and should be called after you are done
	 * registering you config options
	 */
	public void close() {
		config.save();
		log.debug("Config Closed");
	}

	public void sync() {
		config.save();
		PROPERTIES.forEach(Property::save);
		log.debug("Config Sync");
	}

	public String getModid() {
		return modid;
	}

	public abstract static class Property<T> {

		protected EasyConfig config;
		protected String category;
		protected String name;
		protected String comment = "";
		protected T defaultValue;
		protected T value;

		public Property(EasyConfig config, String category, String name, T value) {
			config.log.debug("Created Config: {}", name);
			this.config = config;
			this.category = category;
			this.name = name;
			this.defaultValue = value;
			this.value = value;
			config.PROPERTIES.add(this);
			save();
			config.close();
		}

		public T getValue() {
			return value;
		}

		public String getName() {
			return name;
		}

		public String getCategory() {
			return category;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public abstract void save();

	}

	public static class IntProperty extends Property<Integer> {

		protected int minValue;
		protected int maxValue;

		public IntProperty(EasyConfig config, String category, String name, int value, int min, int max) {
			super(config, category, name, value);
			this.minValue = min;
			this.maxValue = max;
		}

		@Override
		public void setValue(Integer value) {
			if (value < minValue)
				value = minValue;
			if (value > maxValue)
				value = maxValue;
			setValue(value);
		}

		@Override
		public void save() {
			setValue(config.getConfig().getInt(name, category, defaultValue, minValue, maxValue, comment, name));
			config.log.debug("Saved Config - Name: {} Value: {}", name, getValue());
		}

	}

	public static class BooleanProperty extends Property<Boolean> {

		public BooleanProperty(EasyConfig config, String category, String name, Boolean value) {
			super(config, category, name, value);
		}

		@Override
		public void save() {
			setValue(config.getConfig().getBoolean(name, category, defaultValue, comment, name));
			config.log.debug("Saved Config - Name: {} Value: {}", name, getValue());
		}

	}

	public static class StringProperty extends Property<String> {

		public StringProperty(EasyConfig config, String category, String name, String value) {
			super(config, category, name, value);
		}

		@Override
		public void save() {
			setValue(config.getConfig().getString(name, category, defaultValue, comment, name));
			config.log.debug("Saved Config - Name: {} Value: {}", name, getValue());
		}

	}
}