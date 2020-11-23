package zdoctor.zskilltree.client.multiplayer;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ObjectHolder;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.IClientSkillTreeTracker;
import zdoctor.zskilltree.skilltree.data.handlers.SkillTreeTracker;
import zdoctor.zskilltree.network.play.server.SCriterionTrackerSyncPacket;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
    public void read(SCriterionTrackerSyncPacket packetIn) {
        sorted_pages.clear();
        for (SkillPageAlignment value : SkillPageAlignment.values()) {
            sorted_pages.put(value, new SkillPage[0]);
        }

        completed.clear();

        if (packetIn.isFirstSync()) {
            pages.clear();
            skills.clear();
        }

        for (CriterionTracker trackable : packetIn.getToAdd()) {
            if (trackable instanceof SkillPage)
                pages.put(trackable.getId(), (SkillPage) trackable);
            else if (trackable instanceof Skill)
                skills.put(trackable.getId(), (Skill) trackable);
        }


        packetIn.getToRemove().forEach(pages::remove);
        packetIn.getToRemove().forEach(skills::remove);

        Set<ResourceLocation> orphanedSkills = new HashSet<>(skills.keySet());

        pages.values().stream().sorted(SkillPage::compare)
                .forEach(page -> {
                    SkillPage skillPage = page.copy();
                    for (ResourceLocation id : skillPage.getRootSkills().keySet()) {
                        Skill skill = skills.get(id);
                        if (skill != null) {
                            skillPage.putRootSkill(id, skill);
                            orphanedSkills.remove(id);
                        }
                    }
                    addPageSafe(skillPage);
                    completed.add(skillPage);
                });

        if (!orphanedSkills.isEmpty()) {
            LOGGER.debug("Found {} skills that have no page. Perhaps they have the skill but not its parent page. " +
                    "I should make a system for that. {}", orphanedSkills.size(), Arrays.deepToString(orphanedSkills.toArray()));
        }

        maxHorizontal = Math.max(0, sorted_pages.get(SkillPageAlignment.HORIZONTAL).length - 1);
        maxVertical = Math.max(0, sorted_pages.get(SkillPageAlignment.VERTICAL).length - 1);
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

    @Override
    public void onSkillClicked(Skill skill) {
        // TODO Send packet to server about the click
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
