package zdoctor.skilltree.proxy;

import org.apache.logging.log4j.Logger;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import zdoctor.skilltree.EasyConfig;
import zdoctor.skilltree.EasyConfig.BooleanProperty;
import zdoctor.skilltree.ModMain;

public abstract class CommonProxy {

	public Logger log;
	public EasyConfig config;

	public BooleanProperty keepSkillsOnDeath;

	public void preInit(FMLPreInitializationEvent e) {
		config = new EasyConfig(e);
		keepSkillsOnDeath = new EasyConfig.BooleanProperty(ModMain.proxy.config, "skilltree.gameplay",
				"keepSkillsOnDeath", true);
		config.close();
		log = e.getModLog();
	}

	public void init(FMLInitializationEvent e) {

	}

	public void postInit(FMLPostInitializationEvent e) {
	}

	public abstract World getWorld();

}
