package zdoctor.skilltree.proxy;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import zdoctor.skilltree.EasyConfig;
import zdoctor.skilltree.EasyConfig.BooleanProperty;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.skills.SkillBase;

public abstract class CommonProxy {
	public static final ArrayList<SkillBase> SkillWatcher_Registry = new ArrayList<>();

	public Logger log;
	public EasyConfig config;

	public BooleanProperty keepSkillsOnDeath;
	
	public void preInit(FMLPreInitializationEvent e) {
		config = new EasyConfig(e);
		keepSkillsOnDeath = new EasyConfig.BooleanProperty(ModMain.proxy.config, "skilltree.gameplay",
				"keepSkillsOnDeath", true);
		config.close();
		log = e.getModLog();

		log.debug("FMLPreInitializationEvent - Side: {}", FMLCommonHandler.instance().getEffectiveSide());
	}

	public void init(FMLInitializationEvent e) {
		log.debug("FMLInitializationEvent - Side: {}", FMLCommonHandler.instance().getEffectiveSide());
	}

	public void postInit(FMLPostInitializationEvent e) {
		log.debug("FMLPostInitializationEvent - Side: {}", FMLCommonHandler.instance().getEffectiveSide());
	}

	public abstract World getWorld();
	public abstract EntityPlayer getPlayer();
	
	public Side getEffectiveSide( ) {
		return getWorld().isRemote ? Side.CLIENT : Side.SERVER;
	}

}
