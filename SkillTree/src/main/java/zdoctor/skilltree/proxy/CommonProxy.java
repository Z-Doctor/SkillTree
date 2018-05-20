package zdoctor.skilltree.proxy;

import org.apache.logging.log4j.Logger;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import zdoctor.skilltree.EasyConfig;
import zdoctor.skilltree.EasyConfig.BooleanProperty;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.network.SkillTreePacketHandler;
import zdoctor.skilltree.skills.CapabilitySkillHandler;

public abstract class CommonProxy {

	public Logger log;
	public EasyConfig config;

	public BooleanProperty keepSkillsOnDeath;

	public void preInit(FMLPreInitializationEvent e) {
		SkillTreePacketHandler.initPackets();
		CapabilitySkillHandler.register();
		MinecraftForge.EVENT_BUS.register(new CapabilitySkillHandler());

		config = new EasyConfig(e);
		keepSkillsOnDeath = new EasyConfig.BooleanProperty(ModMain.proxy.config, "skilltree.gameplay",
				"keepSkillsOnDeath", true);
		config.close();
		log = e.getModLog();
		// for (int i = 0; i < 32; i++) {
		// if (i % 2 == 0)
		// continue;
		// new SkillTabs(i, "Test: " + i, new SkillPageBase("Test: " + i) {
		// }) {
		//
		// @Override
		// public ItemStack getTabIconItem() {
		// return new ItemStack(Items.DIAMOND);
		// }
		// };
		// }
	}

	public void init(FMLInitializationEvent e) {

	}

	public void postInit(FMLPostInitializationEvent e) {
	}

	public abstract World getWorld();

}
