package zdoctor.skilltree.proxy;

import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import zdoctor.skilltree.api.config.EasyConfig;
import zdoctor.skilltree.api.config.EasyConfigGui;
import zdoctor.skilltree.api.config.EasyConfig.BooleanProperty;
import zdoctor.skilltree.api.skill.tabs.SkillTabs;
import zdoctor.skilltree.skills.pages.PlayerInfoPage;

public abstract class CommonProxy {
	public Logger log;
	public EasyConfig config;

	public BooleanProperty keepSkillsOnDeath;

	public static final SkillTabs PLAYER_INFO = new SkillTabs(0, "PlayerInfo", new PlayerInfoPage()) {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Items.WRITABLE_BOOK);
		}
	};

	public void preInit(FMLPreInitializationEvent e) {

		config = new EasyConfigGui(e);
		keepSkillsOnDeath = new EasyConfig.BooleanProperty(config, "skilltree.gameplay", "keepSkillsOnDeath", true);
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

}
