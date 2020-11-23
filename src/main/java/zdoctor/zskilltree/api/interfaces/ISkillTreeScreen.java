package zdoctor.zskilltree.api.interfaces;

import net.minecraft.client.gui.IGuiEventListener;

public interface ISkillTreeScreen extends IClientSkillTreeTracker.IListener, IGuiEventListener, ImageDisplayInfo {
    int getTabPageNumber();

    IClientSkillTreeTracker getClientTracker();

    <T extends IGuiEventListener> T addListener(T listener);

}
