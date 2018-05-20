package zdoctor.skilltree.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.client.gui.GuiSkillTree;

@SideOnly(Side.CLIENT)
public class KeyHandler {
	public static final KeyBinding OPEN_SKILL_TREE = new KeyBinding("key.skilltree", Keyboard.KEY_K,
			"key.categories.inventory");

	@SubscribeEvent
	public void tickEvent(TickEvent.PlayerTickEvent e) {
		onTick(e);
	}

	private void onTick(PlayerTickEvent e) {
		if (e.side == Side.SERVER || FMLClientHandler.instance().getClient().currentScreen != null)
			return;

		if (e.phase == Phase.START) {
			if (FMLClientHandler.instance().getClient().inGameHasFocus && OPEN_SKILL_TREE.isPressed()) {
				SkillTreeApi.SyncClientSkills(Minecraft.getMinecraft().player);
				FMLClientHandler.instance().showGuiScreen(new GuiSkillTree());
			}
		}
	}
}
