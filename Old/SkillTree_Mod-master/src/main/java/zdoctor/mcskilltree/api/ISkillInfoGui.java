package zdoctor.mcskilltree.api;

import net.minecraft.client.gui.IGuiEventListener;
import zdoctor.mcskilltree.client.gui.skills.SkillEntryGui;

public interface ISkillInfoGui extends IGuiEventListener {

    boolean withinBounds(double mouseX, double mouseY);

    void render(int mouseX, int mouseY, float partialTicks);

    SkillEntryGui getSkillEntry();

    void onClose();

}
