package zdoctor.zskilltree.commands;

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
import zdoctor.zskilltree.skill.Skill;
import zdoctor.zskilltree.skillpages.SkillPage;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class SkillTreeCommand {
    public static final SuggestionProvider<CommandSource> SUGGEST_SKILLPAGE = (context, builder) -> {
        Collection<SkillPage> pages = ModMain.getInstance().getSkillPageManager().getAllSkillPages();
        return ISuggestionProvider.func_212476_a(pages.stream().filter(page -> !page.getCriteria().isEmpty()).map(SkillPage::getId), builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_SKILL = (context, builder) -> {
        Collection<Skill> pages = ModMain.getInstance().getSkillManager().getAllSkills();
        return ISuggestionProvider.func_212476_a(pages.stream().map(Skill::getId), builder);
    };
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DynamicCommandExceptionType SKILL_NOT_FOUND =
            new DynamicCommandExceptionType((skill) -> new TranslationTextComponent("skilltree.skillNotFound", skill));
    private static final DynamicCommandExceptionType SKILLPAGE_NOT_FOUND =
            new DynamicCommandExceptionType((skill) -> new TranslationTextComponent("skilltree.pageNotFound", skill));

    private static Predicate<CommandSource> permission(int permissionLevel) {
        return commandSource -> commandSource.hasPermissionLevel(permissionLevel);
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(buildSkillTreeCommand());
    }

    private static LiteralArgumentBuilder<CommandSource> buildSkillTreeCommand() {
        return Commands.literal("skilltree").requires(permission(2))
                .then(buildGrantCommand())
                .then(buildRevokeCommand());
    }

    private static LiteralArgumentBuilder<CommandSource> buildGrantCommand() {
        return Commands.literal("grant").then(Commands.argument("targets", EntityArgument.entities())
                .then(buildSkillPageArg().executes(SkillTreeCommand::grantPage)));
//                .then(buildSkillArg());
    }

    private static LiteralArgumentBuilder<CommandSource> buildRevokeCommand() {
        return Commands.literal("revoke").then(Commands.argument("targets", EntityArgument.entities())
                .then(buildSkillPageArg().executes(SkillTreeCommand::revokePage)));
//                .then(buildSkillArg());
    }

    private static ISkillTreeTracker[] processCommand(CommandContext<CommandSource> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");

        return targets.stream().map(livingEntity -> livingEntity.getCapability(ModMain.SKILLTREE_CAPABILITY)).map(LazyOptional::resolve)
                .filter(Optional::isPresent).map(Optional::get).toArray(ISkillTreeTracker[]::new);
    }

    private static int grantPage(CommandContext<CommandSource> context) throws CommandSyntaxException {
        int i = 0;
        CommandSource source = context.getSource();
        ResourceLocation pageId = ResourceLocationArgument.getResourceLocation(context, "page");
        SkillPage page = ModMain.getInstance().getSkillPageManager().getPage(pageId);

        if (page == null)
            throw SKILLPAGE_NOT_FOUND.create(pageId);

        ISkillTreeTracker[] handlers = processCommand(context);

        for (ISkillTreeTracker handler : handlers) {
            if (handler.grant(page))
                i++;
        }

        if (handlers.length == 1 && i == 1)
            source.sendFeedback(new TranslationTextComponent("skilltree.grant.page.success.single", page.getDisplayInfo().getPageName(), handlers[0].getOwner().getDisplayName()), true);
        else if (i > 1)
            source.sendFeedback(new TranslationTextComponent("skilltree.grant.page.success.many", page.getDisplayInfo().getPageName(), i), true);
        else
            source.sendFeedback(new TranslationTextComponent("skilltree.grant.page.fail.any", page.getDisplayInfo().getPageName()), true);
        return i;

    }

    private static int revokePage(CommandContext<CommandSource> context) throws CommandSyntaxException {
        int i = 0;
        CommandSource source = context.getSource();
        ResourceLocation pageId = ResourceLocationArgument.getResourceLocation(context, "page");
        SkillPage page = ModMain.getInstance().getSkillPageManager().getPage(pageId);

        if (page == null)
            throw SKILLPAGE_NOT_FOUND.create(pageId);

        ISkillTreeTracker[] handlers = processCommand(context);
        for (ISkillTreeTracker handler : handlers) {
            if (handler.revoke(page))
                i++;
        }

        if (handlers.length == 1 && i == 1)
            source.sendFeedback(new TranslationTextComponent("skilltree.revoke.page.success.single", page.getDisplayInfo().getPageName(), handlers[0].getOwner().getDisplayName()), true);
        else if (i > 1)
            source.sendFeedback(new TranslationTextComponent("skilltree.revoke.page.success.many", page.getDisplayInfo().getPageName(), i), true);
        else
            source.sendFeedback(new TranslationTextComponent("skilltree.revoke.page.fail.any", page.getDisplayInfo().getPageName()), true);
        return i;

    }


    private static RequiredArgumentBuilder<CommandSource, ResourceLocation> buildSkillPageArg() {
        return Commands.argument("page", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_SKILLPAGE);
    }

    private static RequiredArgumentBuilder<CommandSource, ResourceLocation> buildSkillArg() {
        return Commands.argument("skill", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_SKILL);
    }


}