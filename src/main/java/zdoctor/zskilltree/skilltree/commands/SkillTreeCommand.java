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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

// TODO Add grantAll and revokeAll command
// TODO Add command to list all the skills a entity has, doesn't have and have, and a list of all skills marked as such
// Command to make new skill pages and trees?
public class SkillTreeCommand {
    public static final SuggestionProvider<CommandSource> SUGGEST_SKILLPAGE = (context, builder) -> {
        Collection<SkillPage> pages = ModMain.getInstance().getSkillPageManager().getAllSkillPages();
        return ISuggestionProvider.func_212476_a(pages.stream().filter(page -> !page.getCriteria().isEmpty()).map(SkillPage::getRegistryName), builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_SKILL = (context, builder) -> {
        Collection<Skill> pages = ModMain.getInstance().getSkillManager().getAllSkills();
        return ISuggestionProvider.func_212476_a(pages.stream().map(Skill::getRegistryName), builder);
    };
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DynamicCommandExceptionType SKILL_NOT_FOUND =
            new DynamicCommandExceptionType((skill) -> new TranslationTextComponent("commands.skilltree.skillNotFound", skill));
    private static final DynamicCommandExceptionType SKILL_PAGE_NOT_FOUND =
            new DynamicCommandExceptionType((skill) -> new TranslationTextComponent("commands.skilltree.pageNotFound", skill));

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
                .then(buildRevokeCommand());
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
                .then(Commands.literal("skill").then(buildSkillArg()).executes(SkillTreeCommand::grantSkill)));
    }

    private static LiteralArgumentBuilder<CommandSource> buildRevokeCommand() {
        return Commands.literal("revoke").then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.literal("page").then(buildSkillPageArg().executes(SkillTreeCommand::revokePage)))
                .then(Commands.literal("skill").then(buildSkillArg()).executes(SkillTreeCommand::revokeSkill)));
//        return Commands.literal("revoke").then(Commands.argument("targets", EntityArgument.entities())
//                .then(buildSkillPageArg().executes(SkillTreeCommand::revokePage)))
//                .then(buildSkillArg().executes(SkillTreeCommand::revokeSkill));
    }

    private static ISkillTreeTracker[] processCommand(CommandContext<CommandSource> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");

        return targets.stream().map(livingEntity -> livingEntity.getCapability(ModMain.SKILL_TREE_CAPABILITY)).map(LazyOptional::resolve)
                .filter(Optional::isPresent).map(Optional::get).toArray(ISkillTreeTracker[]::new);
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
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.page.success.single", page.getDisplayInfo().getPageName(), handlers[0].getOwner().getDisplayName()), true);
        else if (i > 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.page.success.many", page.getDisplayInfo().getPageName(), i), true);
        else
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.page.fail.none", page.getDisplayInfo().getPageName()), true);
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
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.skill.success.single", skill.getDisplayInfo().getSkillName(), handlers[0].getOwner().getDisplayName()), true);
        else if (i > 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.skill.success.many", skill.getDisplayInfo().getSkillName(), i), true);
        else
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.grant.skill.fail.none", skill.getDisplayInfo().getSkillName()), true);
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
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.page.success.single", page.getDisplayInfo().getPageName(), handlers[0].getOwner().getDisplayName()), true);
        else if (i > 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.page.success.many", page.getDisplayInfo().getPageName(), i), true);
        else
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.page.fail.any", page.getDisplayInfo().getPageName()), true);
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
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.skill.success.single", skill.getSkillName(), handlers[0].getOwner().getDisplayName()), true);
        else if (i > 1)
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.skill.success.many", skill.getSkillName(), i), true);
        else
            source.sendFeedback(new TranslationTextComponent("commands.skilltree.revoke.skill.fail.any", skill.getSkillName()), true);
        return i;

    }


}