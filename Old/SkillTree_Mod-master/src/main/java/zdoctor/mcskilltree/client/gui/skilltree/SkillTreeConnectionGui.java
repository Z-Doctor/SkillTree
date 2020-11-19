package zdoctor.mcskilltree.client.gui.skilltree;

import com.google.common.collect.Lists;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.mcskilltree.skilltree.SkillTree;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@OnlyIn(Dist.CLIENT)
public class SkillTreeConnectionGui extends AbstractGui {
    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/advancements/widgets.png");
    private static final Pattern PATTERN = Pattern.compile("(.+) \\S+");
    private final SkillTreeTabGui skillTreeTab;
    private final SkillTree skillTree;
    private final DisplayInfo displayInfo;
    private final String title;
//    private final int width;
//    private final List<String> description;
    private final Minecraft minecraft;
    private SkillTreeConnectionGui parent;
    private final List<SkillTreeConnectionGui> children = Lists.newArrayList();
    private final int x;
    private final int y;

    public SkillTreeConnectionGui(SkillTreeTabGui skillTreeTabGui, Minecraft minecraft, SkillTree skillTree, DisplayInfo display) {
        this.skillTreeTab = skillTreeTabGui;
        this.skillTree = skillTree;
        this.displayInfo = display;
        this.minecraft = minecraft;
        this.title = minecraft.fontRenderer.trimStringToWidth(display.getTitle().getFormattedText(), 163);
        this.x = MathHelper.floor(display.getX() * 28.0F);
        this.y = MathHelper.floor(display.getY() * 27.0F);
//        int requirementCount = skillTree.getRequirementCount();
//        int requirementWidth = String.valueOf(requirementCount).length();
//        int k = requirementCount > 1 ? minecraft.fontRenderer.getStringWidth("  ") + minecraft.fontRenderer.getStringWidth("0") * j * 2 + minecraft.fontRenderer.getStringWidth("/") : 0;
//        int l = 29 + minecraft.fontRenderer.getStringWidth(this.title) + k;
//        String formattedText = display.getDescription().getFormattedText();
//        this.description = this.findOptimalLines(formattedText, l);
//
//        for(String s1 : this.description) {
//            l = Math.max(l, minecraft.fontRenderer.getStringWidth(s1));
//        }
//
//        this.width = l + 3 + 5;
    }

    private List<String> findOptimalLines(String formattedText, int p_192995_2_) {
        if (formattedText.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<String> list = this.minecraft.fontRenderer.listFormattedStringToWidth(formattedText, p_192995_2_);
            if (list.size() < 2) {
                return list;
            } else {
                String s = list.get(0);
                String s1 = list.get(1);
                int i = this.minecraft.fontRenderer.getStringWidth(s + ' ' + s1.split(" ")[0]);
                if (i - p_192995_2_ <= 10) {
                    return this.minecraft.fontRenderer.listFormattedStringToWidth(formattedText, i);
                } else {
                    Matcher matcher = PATTERN.matcher(s);
                    if (matcher.matches()) {
                        int j = this.minecraft.fontRenderer.getStringWidth(matcher.group(1));
                        if (p_192995_2_ - j <= 10) {
                            return this.minecraft.fontRenderer.listFormattedStringToWidth(formattedText, j);
                        }
                    }

                    return list;
                }
            }
        }
    }
}
