package zdoctor.zskilltree.api.interfaces;

import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import javax.annotation.Nullable;

public interface IClientProgressTracker extends ISkillTreeTracker {
    int getMaxVertical();

    int getMaxHorizontal();

    void setListener(IListener listener);

    void setSelectedPage(@Nullable SkillPage pageIn, boolean tellServer);

    interface IListener {
        void setSelectedPage(@Nullable SkillPage pageIn);

        void skillPageAdded(SkillPage page);

        void reload();
    }
}
