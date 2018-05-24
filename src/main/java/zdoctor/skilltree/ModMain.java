package zdoctor.skilltree;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import zdoctor.skilltree.command.CommandSkillTree;
import zdoctor.skilltree.network.SkillTreePacketHandler;
import zdoctor.skilltree.proxy.CommonProxy;
import zdoctor.skilltree.skills.CapabilitySkillHandler;

@Mod(modid = ModMain.MODID, version = ModMain.VERSION)
public class ModMain {
	public static final String MODID = "skilltree";
	public static final String VERSION = "1.2.0.1";

	@Instance
	public static ModMain INSTANCE = new ModMain();

	@SidedProxy(clientSide = "zdoctor.skilltree.proxy.ClientProxy", serverSide = "zdoctor.skilltree.proxy.ServerProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		SkillTreePacketHandler.initPackets();
		CapabilitySkillHandler.register();
		MinecraftForge.EVENT_BUS.register(new CapabilitySkillHandler());
		proxy.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandSkillTree());
	}
}
