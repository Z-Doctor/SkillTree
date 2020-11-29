package zdoctor.zskilltree.client.multiplayer;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ObjectHolder;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.IClientSkillTreeTracker;
import zdoctor.zskilltree.network.play.SkillInteractionPacket;
import zdoctor.zskilltree.network.play.server.SCriterionTrackerSyncPacket;
import zdoctor.zskilltree.skilltree.data.criterion.ProgressTracker;
import zdoctor.zskilltree.skilltree.data.trackers.SkillTreeTracker;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ClientSkillTreeTracker extends SkillTreeTracker implements IClientSkillTreeTracker {
    @ObjectHolder("zskilltree:player_info")
    public static final SkillPage playerInfo = null;

    private final HashMap<ResourceLocation, SkillPage> pages = new HashMap<>();
    private final HashMap<ResourceLocation, Skill> skills = new HashMap<>();

    private final HashMap<SkillPageAlignment, SkillPage[]> sorted_pages = new HashMap<>();

    private IListener listener;
    private SkillPage selectedPage;
    private int maxVertical;
    private int maxHorizontal;

    public ClientSkillTreeTracker(ClientPlayerEntity player) {
        super(player);
    }

    @Override
    protected void process(boolean firstSync, Set<CriterionTracker> toAdd, Set<ResourceLocation> toRemove, Map<ResourceLocation, ProgressTracker> progressUpdate) {
        super.process(firstSync, toAdd, toRemove, progressUpdate);
    }

    @Override
    public void read(SCriterionTrackerSyncPacket packetIn) {
        super.read(packetIn);
        for (CriterionTracker trackable : packetIn.getToAdd()) {
            if (trackable instanceof SkillPage)
                pages.put(trackable.getRegistryName(), (SkillPage) trackable);
            else if (trackable instanceof Skill)
                skills.put(trackable.getRegistryName(), (Skill) trackable);
        }


        for (ResourceLocation id : packetIn.getToRemove()) {
            pages.remove(id);
            skills.remove(id);
        }

        sorted_pages.clear();
        for (SkillPageAlignment value : SkillPageAlignment.values()) {
            sorted_pages.put(value, new SkillPage[0]);
        }

        HashMap<ResourceLocation, Skill> orphanedSkills = new HashMap<>(skills);

        pages.values().stream().sorted(SkillPage::compare).map(page -> page.putFrom(orphanedSkills)).forEach(this::addPageSafe);

        if (!orphanedSkills.isEmpty()) {
            LOGGER.debug("Found {} skills that have no page. Perhaps they have the skill but not its parent page. " +
                    "I should make a system for that. {}", orphanedSkills.size(), Arrays.deepToString(orphanedSkills.keySet().toArray()));
        }

        maxHorizontal = Math.max(0, sorted_pages.get(SkillPageAlignment.HORIZONTAL).length - 1);
        maxVertical = Math.max(0, sorted_pages.get(SkillPageAlignment.VERTICAL).length - 1);
        if (listener != null)
            listener.reload();

    }

    @Override
    public void flushDirty() {
        // TODO Should I change this maybe some kind of listener(?)
    }

    @Override
    protected void reset() {
        super.reset();
        pages.clear();
        skills.clear();
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
            pages.values().forEach(listener::skillPageAdded);
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

    @Override
    public void setSelectedSkill(@Nullable Skill skillIn, boolean tellServer) {
        if (skillIn != null && tellServer) {
            SkillInteractionPacket packet = new SkillInteractionPacket(skillIn, SkillInteractionPacket.Type.BUY);
            ModMain.getInstance().getPacketChannel().sendToServer(packet);
        }

        if (this.listener != null)
            this.listener.setSelectedSkill(skillIn);
    }

    @Override
    public SkillPage getDefaultPage() {
        return playerInfo;
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

    public synchronized SkillPage setPageAt(int index, SkillPage page) {
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
