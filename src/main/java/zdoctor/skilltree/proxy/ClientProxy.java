package zdoctor.skilltree.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.client.GuiPageRegistry;
import zdoctor.skilltree.client.KeyHandler;
import zdoctor.skilltree.client.gui.GuiPlayerInfoPage;
import zdoctor.skilltree.skills.pages.PlayerInfoPage;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		MinecraftForge.EVENT_BUS.register(new KeyHandler());
		ClientRegistry.registerKeyBinding(KeyHandler.OPEN_SKILL_TREE);
		GuiPageRegistry.registerGui(PlayerInfoPage.class, GuiPlayerInfoPage.class);
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}

	@Override
	public World getWorld() {
		return FMLClientHandler.instance().getClient().world;
	}

	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().player;
	}

}
