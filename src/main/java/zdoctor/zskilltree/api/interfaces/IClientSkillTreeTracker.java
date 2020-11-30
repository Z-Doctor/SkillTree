package zdoctor.zskilltree.api.interfaces;

import zdoctor.zskilltree.skilltree.criterion.Skill;
import zdoctor.zskilltree.skilltree.criterion.SkillPage;

import javax.annotation.Nullable;

public interface IClientSkillTreeTracker extends ISkillTreeTracker {
    int getMaxVertical();

    int getMaxHorizontal();

    void setListener(IListener listener);

    void setSelectedPage(@Nullable SkillPage pageIn, boolean tellServer);

    void setSelectedSkill(@Nullable Skill skillIn, boolean tellServer);

    SkillPage getDefaultPage();

    interface IListener {
        // TODO Perhaps make skills more like skill pages where they can be selected through the listener
        //  that way I can have another window pop up to display their info

        void setSelectedPage(@Nullable SkillPage pageIn);

        void setSelectedSkill(@Nullable Skill skillIn);

        void skillPageAdded(SkillPage page);

        void reload();
    }
}
