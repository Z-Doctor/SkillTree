package zdoctor.mcskilltree.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.client.gui.skilltree.SkillTreeScreen;
import zdoctor.mcskilltree.skilltree.tabs.TestSkillTree;

import java.awt.event.KeyEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = {Dist.CLIENT})
public class KeyBindingHandler {
    public static final KeyBinding KeyBindSkillTree = new KeyBinding("key.skilltree", KeyEvent.VK_K,
            "key.categories.inventory");

    public static final KeyBinding RECENTER_SKILL_TREE = new KeyBinding("key.skilltree.recenter",
            KeyEvent.VK_R, "key.categories.inventory");

    static {
        ClientRegistry.registerKeyBinding(KeyBindSkillTree);
        ClientRegistry.registerKeyBinding(RECENTER_SKILL_TREE);
    }

    @SubscribeEvent
    public static void onInputEvent(InputEvent.KeyInputEvent keyInputEvent) {
        if (KeyBindSkillTree.isKeyDown()) {
//            McSkillTree.LOGGER.info("Toggle Skill Tree");
            Minecraft.getInstance().displayGuiScreen(new SkillTreeScreen());
        }
    }
}
