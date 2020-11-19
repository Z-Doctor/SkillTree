package zdoctor.mcskilltree.api;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.mcskilltree.skills.Skill;

import java.util.function.Predicate;

public interface IRequirement extends Predicate<ISkillHandler> {

    ITextComponent getDescription();

    static IRequirement asPrerequisite(Skill prerequisite) {
        return new IRequirement() {
            @Override
            public ITextComponent getDescription() {
                return new TranslationTextComponent("requirement.prerequisite." + prerequisite.toString());
            }

            @Override
            public boolean test(ISkillHandler handler) {
                return handler.hasSkill(prerequisite);
            }
        };
    }
    
    static IRequirement asParent(Skill parent) {
        return new IRequirement() {
            @Override
            public ITextComponent getDescription() {
                return new TranslationTextComponent("requirement.parent." + parent.toString());
            }

            @Override
            public boolean test(ISkillHandler handler) {
                return handler.hasSkill(parent);
            }
        };
    }
}
