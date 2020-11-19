package zdoctor.mcskilltree.util.text;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.mcskilltree.api.ISkillHandler;
import zdoctor.mcskilltree.api.ISkillGetter;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skills.SkillData;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillTranslationTextComponent extends TranslationTextComponent {
    protected static final LanguageMap LOCAL_LANGUAGE = LanguageMap.getInstance();

    private final String key;
    private final ISkillHandler handler;
    private final Skill skill;
    private final Object[] formatArgs;
    private String reroute;
    Pattern skillProperty = Pattern.compile("\\$skill\\.(\\S*)");

    // For complex/out of order translations do '%#$s' where '#' is the position of the argument you want
    public SkillTranslationTextComponent(String translationKey, ISkillHandler handler, Skill skill, Object... args) {
        super(translationKey, args);
        this.key = translationKey;
        this.handler = handler;
        this.skill = skill;
        this.formatArgs = args;
    }

    /**
     * When set, if the translate fails (i.e. the entry was not made in the language file) will attempt
     * translate again instead with the given key
     *
     * @param key The key to use in case of failure when translating
     */
    public SkillTranslationTextComponent withDefault(String key) {
        this.reroute = key;
        return this;
    }

    @Override
    public SkillTranslationTextComponent shallowCopy() {
        Object[] aobject = new Object[this.formatArgs.length];

        for (int i = 0; i < this.formatArgs.length; ++i) {
            if (this.formatArgs[i] instanceof ITextComponent) {
                aobject[i] = ((ITextComponent) this.formatArgs[i]).deepCopy();
            } else {
                aobject[i] = this.formatArgs[i];
            }
        }

        return new SkillTranslationTextComponent(this.key, handler, skill, aobject);
    }

    // TODO Fix is so that values update as skills do
    @Override
    protected void initializeFromFormat(String format) {
        if (format.equals(key) && this.reroute != null) {
            format = LOCAL_LANGUAGE.translateKey(this.reroute);
        }

        super.initializeFromFormat(format);
        SkillData data = handler.getData(skill);
        if (data != null) {
            for (int i = 0; i < this.children.size(); i++) {
                ITextComponent child = this.children.get(i);
                String s = child.getFormattedText();
                Matcher matcher = skillProperty.matcher(s);

                boolean flag = false;
                while (matcher.find()) {
                    // TODO Fix cast exception with getters when hot swapping
                    //  prob going to have to change how the getters are done
                    String match = matcher.group(1);
                    ISkillGetter<?> property = skill.getProperty(new ResourceLocation(match));

                    if (property != null) {
                        s = s.replace(matcher.group(0), property.get(skill, handler).toString());
                        flag = true;
                    }
                }

                if (flag) {
                    ITextComponent replacement = new StringTextComponent(s);
                    replacement.setStyle(child.getStyle());
                    children.set(i, replacement);
                }
            }
        }

    }

    @Override
    public String toString() {
        return "SkillTranslationTextComponent{" +
                "key='" + key + '\'' +
                ", handler=" + handler +
                ", skill=" + skill +
                ", args=" + Arrays.toString(formatArgs) +
                ", skillProperty=" + skillProperty +
                ", siblings=" + siblings +
                ", style=" + getStyle() +
                '}';
    }
}
