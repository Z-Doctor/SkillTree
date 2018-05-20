package zdoctor.mcskilltree;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import zdoctor.mcskilltree.proxy.CommonProxy;
import zdoctor.skilltree.api.SkillTreeApi;

@Mod(modid = ModMain.MODID, dependencies = SkillTreeApi.DEPENDENCY)
public class ModMain {
	public static final String MODID = "mcskilltree";

	@Instance
	public static ModMain INSTANCE = new ModMain();

	@SidedProxy(clientSide = "zdoctor.mcskilltree.proxy.ClientProxy", serverSide = "zdoctor.mcskilltree.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
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

}
