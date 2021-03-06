package zdoctor.zskilltree.skilltree.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.command.arguments.EntityOptions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.skilltree.trackers.ProgressTracker;
import zdoctor.zskilltree.skilltree.criterion.SkillPage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class SkillTreeEntityOptions {
    private static boolean alreadyRegistered;

    public static void register() {
        if (alreadyRegistered)
            return;
        alreadyRegistered = true;

        // TODO See if I can simplify
        EntityOptions.register("skill_page", filter -> {
                    StringReader reader = filter.getReader();
                    Map<ResourceLocation, Predicate<ProgressTracker>> map = new HashMap<>();
                    reader.expect('{');
                    reader.skipWhitespace();

                    while (reader.canRead() && reader.peek() != '}') {
                        reader.skipWhitespace();
                        ResourceLocation location = ResourceLocation.read(reader);
                        reader.skipWhitespace();
                        reader.expect('=');
                        reader.skipWhitespace();

                        if (reader.canRead() && reader.peek() == '{') {
                            Map<String, Predicate<CriterionProgress>> criterionStatus = Maps.newHashMap();
                            reader.skipWhitespace();
                            reader.expect('{');
                            reader.skipWhitespace();

                            while (reader.canRead() && reader.peek() != '}') {
                                reader.skipWhitespace();
                                String pageName = reader.readUnquotedString();
                                reader.skipWhitespace();
                                reader.expect('=');
                                reader.skipWhitespace();

                                boolean isObtained = reader.readBoolean();
                                criterionStatus.put(pageName, progress -> progress.isObtained() == isObtained);
                                reader.skipWhitespace();

                                if (reader.canRead() && reader.peek() == ',') {
                                    reader.skip();
                                }

                            }
                            reader.skipWhitespace();
                            reader.expect('}');
                            reader.skipWhitespace();

                            map.put(location, tracker -> {
                                for (Map.Entry<String, Predicate<CriterionProgress>> entry : criterionStatus.entrySet()) {
                                    CriterionProgress criterionProgress = tracker.getCriterionProgress(entry.getKey());
                                    if (criterionProgress == null || !entry.getValue().test(criterionProgress))
                                        return false;
                                }
                                return true;
                            });
                        } else {
                            boolean isObtained = reader.readBoolean();
                            map.put(location, progress -> (progress != null && progress.isDone()) == isObtained);
                        }
                        reader.skipWhitespace();
                        if (reader.canRead() && reader.peek() == ',')
                            reader.skip();
                    }

                    reader.expect('}');
                    if (!map.isEmpty()) {
                        filter.addFilter(entity -> {
                            Optional<ISkillTreeTracker> cap = entity.getCapability(ModMain.getSkillTreeCapability()).resolve();
                            if (!cap.isPresent())
                                return false;
                            ISkillTreeTracker skillTree = cap.get();
                            for (Map.Entry<ResourceLocation, Predicate<ProgressTracker>> entry : map.entrySet()) {
                                SkillPage page = ModMain.getInstance().getSkillPageManager().getPage(entry.getKey());
                                if (page == null || !entry.getValue().test(skillTree.getProgress(page)))
                                    return false;
                            }
                            return true;
                        });
                        // TODO Change to all entities(?)
                        filter.setIncludeNonPlayers(true);
                    }
                }, entitySelector -> true,
                new TranslationTextComponent("argument.entity.options.skill_page.description"));
        // TODO Add skill Entity Option
    }
}
