package zdoctor.zskilltree.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.IClientSkillTreeTracker;
import zdoctor.zskilltree.client.gui.SkillTreeScreen;
import zdoctor.zskilltree.client.gui.old.GuiSkillTreeScreen;

public final class KeyBindings {
    public static final KeyBinding SKILL_TREE = new KeyBinding("key.zskilltree.open", 75, "key.categories.inventory");
    public static final KeyBinding RECENTER_SKILL_TREE = new KeyBinding("key.zskilltree.recenter", 82, "key.categories.inventory");

    public static void initBindings() {
        MinecraftForge.EVENT_BUS.register(KeyBindings.class);
        ClientRegistry.registerKeyBinding(SKILL_TREE);
        ClientRegistry.registerKeyBinding(RECENTER_SKILL_TREE);
    }

    @SubscribeEvent
    public static void processKeybinds(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LivingEntity player = Minecraft.getInstance().player;
            while (SKILL_TREE.isPressed() && player != null) {
                player.getCapability(ModMain.SKILL_TREE_CAPABILITY).ifPresent(cap -> {
                    if (cap instanceof IClientSkillTreeTracker)
//                        Minecraft.getInstance().displayGuiScreen(new GuiSkillTreeScreen((IClientSkillTreeTracker) cap));
                        Minecraft.getInstance().displayGuiScreen(new SkillTreeScreen((IClientSkillTreeTracker) cap));
                });
            }
        }
    }
}
