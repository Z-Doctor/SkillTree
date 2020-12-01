package zdoctor.zskilltree.skilltree.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.SkillTreeApi;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.skilltree.criterion.Skill;
import zdoctor.zskilltree.skilltree.criterion.SkillPage;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

// TODO Add grantAll and revokeAll command
// TODO Add command to list all the skills a entity has, doesn't have and have, and a list of all skills marked as such
// Command to make new skill pages and trees?
// TODO Fix commands for skills
public class SkillTreeCommand {
    public static final SuggestionProvider<CommandSource> SUGGEST_SKILLPAGE = (context, builder) -> {
        Collection<SkillPage> pages = ModMain.getInstance().getSkillPageManager().getAllSkillPages();
        return ISuggestionProvider.func_212476_a(pages.stream().filter(page -> !page.getCriteria().isEmpty()).map(SkillPage::getRegistryName), builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_SKILL = (context, builder) -> {
        Collection<Skill> skills = ModMain.getInstance().getSkillManager().getAllSkills();
        return ISuggestionProvider.func_212476_a(skills.stream().filter(skill -> !skill.getCriteria().isEmpty()).map(Skill::getRegistryName), builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_TRACKABLE = (context, builder) -> {
        Collection<CriterionTracker> trackers = ModMain.getInstance().getSkillTreeDataManager().getAllTrackers().values();
        return ISuggestionProvider.func_212476_a(trackers.stream().filter(skill -> !skill.getCriteria().isEmpty()).map(CriterionTracker::getRegistryName), builder);
    };
    private static final DynamicCommandExceptionType SKILL_NOT_FOUND =
            new DynamicCommandExceptionType((skill) -> new TranslationTextComponent("commands.skilltree.skillNotFound", skill));
    private static final DynamicCommandExceptionType SKILL_PAGE_NOT_FOUND =
            new DynamicCommandExceptionType((skill) -> new TranslationTextComponent("commands.skilltree.pageNotFound", skill));
    private static final DynamicCommandExceptionType TRACKABLE_NOT_FOUND =
            new DynamicCommandExceptionType((skill) -> new TranslationTextComponent("commands.skilltree.trackableNotFound", skill));

    private static Predicate<CommandSource> permission() {
        return commandSource -> commandSource.hasPermissionLevel(2);
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        // TODO Make it so players can use commands to check if a player has access to a skill tree or has a skill
        dispatcher.register(buildSkillTreeCommand());
    }

    private static LiteralArgumentBuilder<CommandSource> buildSkillTreeCommand() {
        return Commands.literal("skilltree").requires(permission())
                .then(buildGrantCommand())
                .then(buildRevokeCommand())
                .then(buildCheckCommand());
    }

    private static LiteralArgumentBuilder<CommandSource> buildCheckCommand() {
        return Commands.literal("check")
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.literal("obtained").then(Commands.argument("tracker", ResourceLocationArgument.resourceLocation())
                                .suggests(SUGGEST_TRACKABLE).executes(context -> checkIfObtained(context, true))))
                        .then(Commands.literal("unobtained").then(Commands.argument("tracker", ResourceLocationArgument.resourceLocation())
                                .suggests(SUGGEST_TRACKABLE).executes(context -> checkIfObtained(context, false)))));
    }

    private static int checkIfObtained(CommandContext<CommandSource> context, boolean obtained) throws CommandSyntaxException {
        int i = 0;
        CommandSource source = context.getSource();
        ResourceLocation trackerId = ResourceLocationArgument.getResourceLocation(context, "tracker");
        CriterionTracker tracker = ModMain.getInstance().getSkillTreeDataManager().getTracker(trackerId);

        if (tracker == null)
            throw TRACKABLE_NOT_FOUND.create(trackerId);

        ISkillTreeTracker[] handlers = processCommand(context);

        for (ISkillTreeTracker handler : handlers) {
            if (handler.isDone(tracker) == obtained)
                i++;
        }
        if (i == 1) {
            if (obtained)
                source.sendFeedback(new TranslationTextComponent("commands.skilltree.check.tracker.obtained.single", tracker.getDisplayName(), handlers[0].getOwner().getDisplayName()), true);
            else
                source.sendFeedback(new TranslationTextComponent("commands.skilltree.check.tracker.unobtained.single", tracker.getDisplayName(), handlers[0].getOwner().getDisplayName()), true);
        } else if (i > 1) {
            if (obtained)
                source.sendFeedback(new TranslationTextComponent("commands.skilltree.check.tracker.obtained.many", tracker.getDisplayName(), i), true);
            else
                source.sendFeedback(new TranslationTextComponent("commands.skilltree.check.tracker.unobtained.many", tracker.getDisplayName(), i), true);
        } else {
            if (obtained)
                source.sendFeedback(new TranslationTextComponent("commands.skilltree.check.tracker.fail.obtained.none", tracker.getDisplayName()), true);
            else
                source.sendFeedback(new TranslationTextComponent("commands.skilltree.check.tracker.fail.unobtained.none", tracker.getDisplayName()), true);
        }
        return i;
    }

    private static RequiredArgumentBuilder<CommandSource, ResourceLocation> buildSkillPageArg() {
        return Commands.argument("page", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_SKILLPAGE);
    }

    private static RequiredArgumentBuilder<CommandSource, ResourceLocation> buildSkillArg() {
        return Commands.argument("skill", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_SKILL);
    }

    private static LiteralArgumentBuilder<CommandSource> buildGrantCommand() {
        return Commands.literal("grant").then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.literal("page").then(buildSkillPageArg().executes(SkillTreeCommand::grantPage)))
                .then(Commands.literal("skill").then(buildSkillArg().executes(SkillTreeCommand::grantSkill))));
    }

    private static LiteralArgumentBuilder<CommandSource> buildRevokeCommand() {
        return Commands.literal("revoke").then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.literal("page").then(buildSkillPageArg().executes(SkillTreeCommand::revokePage)))
                .then(Commands.literal("skill").then(buildSkillArg().executes(SkillTreeCommand::revokeSkill))));
//        return Commands.literal("revoke").then(Commands.argument("targets", EntityArgument.entities())
//                .then(buildSkillPageArg().executes(SkillTreeCommand::revokePage)))
//                .then(buildSkillArg().executes(SkillTreeCommand::revokeSkill));
    }

    private static ISkillTreeTracker[] processCommand(CommandContext<CommandSource> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");

        return targets.stream().map(SkillTreeApi::getTracker).filter(Objects::nonNull).toArray(ISkillTreeTracker[]::new);
    }

    private static int grantPage(CommandContext<CommandSource> context) throws CommandSyntaxException {
        int i = 0;
        CommandSource source = context.getSource();
        ResourceLocation pageId = ResourceLocationArgument.getResourceLocation(context, "page");
        SkillPage page = ModMain.getInstance().getSkillPageManager().getPage(pageId);

        if (page == null)
            throw SKILL_PAGE_NOT_FOUND.create(pageId);

        ISkillTreeTracker[] handlers = processCommand(context);

        for (ISkillTreeTracker handler : handlers) {
            if (handler.grant(page))
                i++;
        }

        if (handlers.length == 1 && i == 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.page.success.single", page.getDisplayName(), handlers[0].getOwner().getDisplayName()), true);
        else if (i > 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.page.success.many", page.getDisplayName(), i), true);
        else
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.page.fail.none", page.getDisplayName()), true);
        return i;

    }

    private static int grantSkill(CommandContext<CommandSource> context) throws CommandSyntaxException {
        int i = 0;
        CommandSource source = context.getSource();
        ResourceLocation skillId = ResourceLocationArgument.getResourceLocation(context, "skill");
        Skill skill = ModMain.getInstance().getSkillManager().getSkill(skillId);

        if (skill == null)
            throw SKILL_NOT_FOUND.create(skillId);

        ISkillTreeTracker[] handlers = processCommand(context);

        for (ISkillTreeTracker handler : handlers) {
            if (handler.grant(skill))
                i++;
        }

        if (handlers.length == 1 && i == 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.skill.success.single", skill.getDisplayName(), handlers[0].getOwner().getDisplayName()), true);
        else if (i > 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.skill.success.many", skill.getDisplayName(), i), true);
        else
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.skill.fail.none", skill.getDisplayName()), true);
        return i;

    }

    private static int revokePage(CommandContext<CommandSource> context) throws CommandSyntaxException {
        int i = 0;
        CommandSource source = context.getSource();
        ResourceLocation pageId = ResourceLocationArgument.getResourceLocation(context, "page");
        SkillPage page = ModMain.getInstance().getSkillPageManager().getPage(pageId);

        if (page == null)
            throw SKILL_PAGE_NOT_FOUND.create(pageId);

        ISkillTreeTracker[] handlers = processCommand(context);
        for (ISkillTreeTracker handler : handlers) {
            if (handler.revoke(page))
                i++;
        }

        if (handlers.length == 1 && i == 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.page.success.single", page.getDisplayName(), handlers[0].getOwner().getDisplayName()), true);
        else if (i > 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.page.success.many", page.getDisplayName(), i), true);
        else
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.page.fail.any", page.getDisplayName()), true);
        return i;

    }

    private static int revokeSkill(CommandContext<CommandSource> context) throws CommandSyntaxException {
        int i = 0;
        CommandSource source = context.getSource();
        ResourceLocation skillId = ResourceLocationArgument.getResourceLocation(context, "skill");
        Skill skill = ModMain.getInstance().getSkillManager().getSkill(skillId);

        if (skill == null)
            throw SKILL_NOT_FOUND.create(skillId);

        ISkillTreeTracker[] handlers = processCommand(context);
        for (ISkillTreeTracker handler : handlers) {
            if (handler.revoke(skill))
                i++;
        }

        if (handlers.length == 1 && i == 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.skill.success.single", skill.getDisplayName(), handlers[0].getOwner().getDisplayName()), true);
        else if (i > 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.skill.success.many", skill.getDisplayName(), i), true);
        else
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.skill.fail.any", skill.getDisplayName()), true);
        return i;

    }


}