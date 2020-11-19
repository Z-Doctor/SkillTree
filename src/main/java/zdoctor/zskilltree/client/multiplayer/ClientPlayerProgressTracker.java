package zdoctor.zskilltree.client.multiplayer;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.api.interfaces.IClientProgressTracker;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.handlers.SkillTreeTracker;
import zdoctor.zskilltree.network.play.server.SCriterionTrackerSyncPacket;
import zdoctor.zskilltree.skillpages.SkillPage;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerProgressTracker extends SkillTreeTracker implements IClientProgressTracker {
    private final HashMap<ResourceLocation, SkillPage.Builder> pageBuilders = new HashMap<>();

    private final HashMap<SkillPageAlignment, SkillPage[]> sorted_pages = new HashMap<>();

    private IListener listener;
    private SkillPage selectedPage;
    private int maxVertical;
    private int maxHorizontal;

    public ClientPlayerProgressTracker(ClientPlayerEntity player) {
        super(player);
    }

    @Override
    public void read(SCriterionTrackerSyncPacket packetIn) {
        sorted_pages.clear();
        for (SkillPageAlignment value : SkillPageAlignment.values()) {
            sorted_pages.put(value, new SkillPage[0]);
        }

        completed.clear();

        if (packetIn.isFirstSync()) {
            pageBuilders.clear();
        }

        for (CriterionTracker trackable : packetIn.getToAdd()) {
            if (trackable instanceof SkillPage) {
                SkillPage page = (SkillPage) trackable;
                pageBuilders.put(page.getId(), page.copy());
            }
        }

        packetIn.getToRemove().forEach(pageBuilders::remove);

        pageBuilders.entrySet().stream().sorted(Map.Entry.comparingByValue(SkillPage.Builder::compare))
                .forEach(entry -> {
                    ResourceLocation id = entry.getKey();
                    SkillPage skillPage = entry.getValue().build(id);
                    addPageSafe(skillPage);
                    completed.add(skillPage);
                });

        maxHorizontal = sorted_pages.get(SkillPageAlignment.HORIZONTAL).length;
        maxVertical = sorted_pages.get(SkillPageAlignment.VERTICAL).length;
        if (listener != null)
            listener.reload();

    }

    @Override
    public int getMaxVertical() {
        return maxVertical;
    }

    @Override
    public int getMaxHorizontal() {
        return maxHorizontal;
    }

    @Override
    public void setListener(IListener listener) {
        this.listener = listener;
        if (listener != null) {
            completed.forEach(trackable -> {
                if (trackable instanceof SkillPage)
                    listener.skillPageAdded((SkillPage) trackable);
            });
            listener.setSelectedPage(selectedPage);
        }
    }

    @Override
    public void setSelectedPage(@Nullable SkillPage pageIn, boolean tellServer) {
        if (pageIn != null && tellServer) {
            // Todo Use?
        }

        this.selectedPage = pageIn;
        if (this.listener != null)
            this.listener.setSelectedPage(pageIn);
    }

    public SkillPage getPageAt(int index, SkillPageAlignment alignment) {
        SkillPage[] tabs = sorted_pages.get(alignment);
        if (index >= tabs.length)
            return null;
        return tabs[index];
    }

    protected synchronized int getNextIndex(SkillPageAlignment alignment) {
        SkillPage[] tabs = sorted_pages.get(alignment);
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i] == null)
                return i;
        }
        return tabs.length;
    }

    protected synchronized int addPageSafe(SkillPage page) {
        int index = page.getIndex();
        SkillPage[] tabs = sorted_pages.get(page.getAlignment());
        if (index == -1) {
            index = getNextIndex(page.getAlignment());
        } else {
            for (; index < tabs.length; index++)
                if (tabs[index] == null)
                    break;
        }

        if (index >= tabs.length) {
            SkillPage[] tmp = new SkillPage[index + 1];
            System.arraycopy(tabs, 0, tmp, 0, tabs.length);
            sorted_pages.put(page.getAlignment(), tmp);
            tabs = tmp;
        }
        tabs[index] = page;
        page.setIndex(index);
        return index;
    }


    protected synchronized SkillPage setPageAt(int index, SkillPage page) {
        SkillPage[] tabs = sorted_pages.get(page.getAlignment());

        if (index >= tabs.length) {
            SkillPage[] tmp = new SkillPage[index + 1];
            System.arraycopy(tabs, 0, tmp, 0, tabs.length);
            sorted_pages.put(page.getAlignment(), tmp);
            tabs = tmp;
        }
        SkillPage oldPage = tabs[index];
        tabs[index] = page;
        return oldPage;
    }

}
