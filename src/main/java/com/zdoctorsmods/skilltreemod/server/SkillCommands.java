package com.zdoctorsmods.skilltreemod.server;

import java.util.Collection;
import java.util.Collections;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.zdoctorsmods.skilltreemod.SkillTree;
import com.zdoctorsmods.skilltreemod.skills.Skill;
import com.zdoctorsmods.skilltreemod.skills.SkillProgress;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SkillCommands {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_SKILLS = (context, builder) -> {
        Collection<Skill> skills = SkillTree.getSkillManager().getAllSkills();
        return SharedSuggestionProvider
                .suggestResource(skills.stream().filter(skill -> skill.getParent() != null).map(Skill::getId), builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("skillpoints").executes(context -> {
            return performPoints(context.getSource(),
                    Collections.singleton(context.getSource().getPlayer()),
                    Action.GET, 0);
        }));

        // MOD Commands
        LiteralArgumentBuilder<CommandSourceStack> skillCommands = Commands.literal("skill")
                .requires(source -> source.hasPermission(1));

        skillCommands.then(Commands.literal("has").then(Commands.argument("target", EntityArgument.player()).then(
                Commands.argument("skill", ResourceLocationArgument.id()).suggests(SUGGEST_SKILLS).executes(context -> {
                    return performSkill(context.getSource(), EntityArgument.getPlayer(context, "target"), Action.QUERY,
                            getSkill(ResourceLocationArgument.getId(context, "skill")));
                }))));
        skillCommands.then(Commands.literal("grant").then(Commands.argument("target", EntityArgument.player()).then(
                Commands.argument("skill", ResourceLocationArgument.id()).suggests(SUGGEST_SKILLS).executes(context -> {
                    return performSkill(context.getSource(), EntityArgument.getPlayer(context, "target"), Action.GRANT,
                            getSkill(ResourceLocationArgument.getId(context, "skill")));
                }))));
        skillCommands.then(Commands.literal("revoke").then(Commands.argument("target", EntityArgument.player()).then(
                Commands.argument("skill", ResourceLocationArgument.id()).suggests(SUGGEST_SKILLS).executes(context -> {
                    return performSkill(context.getSource(), EntityArgument.getPlayer(context, "target"), Action.REVOKE,
                            getSkill(ResourceLocationArgument.getId(context, "skill")));
                }))));

        LiteralArgumentBuilder<CommandSourceStack> pointsCommand = Commands.literal("points");
        pointsCommand.then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(context -> {
                    return performPoints(context.getSource(), EntityArgument.getPlayers(context, "targets"),
                            Action.ADD,
                            IntegerArgumentType.getInteger(context, "amount"));
                }))));
        pointsCommand.then(Commands.literal("remove").then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(context -> {
                    return performPoints(context.getSource(), EntityArgument.getPlayers(context, "targets"),
                            Action.REMOVE,
                            IntegerArgumentType.getInteger(context, "amount"));
                }))));
        pointsCommand.then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes(context -> {
                    return performPoints(context.getSource(), EntityArgument.getPlayers(context, "targets"),
                            Action.SET,
                            IntegerArgumentType.getInteger(context, "amount"));
                }))));
        pointsCommand.then(Commands.literal("get").executes(context -> {
            return performPoints(context.getSource(),
                    Collections.singleton(context.getSource().getPlayer()),
                    Action.GET, 0);
        }).then(Commands.argument("target", EntityArgument.player())
                .executes(context -> {
                    return performPoints(context.getSource(),
                            Collections.singleton(EntityArgument.getPlayer(context, "target")),
                            Action.GET, 0);
                })));

        skillCommands.then(pointsCommand);

        dispatcher.register(skillCommands);
    }

    private static Skill getSkill(ResourceLocation id) {
        return SkillTree.getSkillManager().getSkill(id);
    }

    private static int performSkill(CommandSourceStack source, ServerPlayer target, Action action,
            Skill skill) {
        int effected = 0;
        PlayerSkills playerSkills = SkillTree.getPlayerSkills(target);
        boolean result = false;
        if (playerSkills != null) {
            switch (action) {
                case QUERY:
                    effected = 1;
                    result = playerSkills.hasSkill(skill);
                    break;
                case GRANT:
                    SkillProgress progress = playerSkills.getOrStartProgress(skill);
                    if (!progress.isDone()) {
                        effected = 1;
                        for (var criteria : progress.getRemainingCriteria()) {
                            playerSkills.award(skill, criteria);
                        }
                        result = true;
                    }
                    break;
                case REVOKE:
                    SkillProgress progress2 = playerSkills.getOrStartProgress(skill);
                    if (progress2.isDone()) {
                        effected = 1;
                        for (var criteria : progress2.getCompletedCriteria()) {
                            playerSkills.revoke(skill, criteria);
                        }
                        result = true;
                    }
                    break;
                default:
                    break;
            }

        }
        if (effected == 0) {
            throw new CommandRuntimeException(Component.translatable(action.getKey() + ".single.failure",
                    target.getDisplayName()));

        } else {
            if (result)
                source.sendSuccess(
                        Component.translatable(action.getKey() + ".single.success." + String.valueOf(result),
                                target, skill.getDisplay().getTitle()),
                        true);
        }
        return effected;
    }

    private static int performPoints(CommandSourceStack source, Collection<ServerPlayer> targets, Action action,
            int amount) {
        int effected = 0;
        for (ServerPlayer player : targets) {
            boolean success = false;
            switch (action) {
                case ADD:
                    success = SkillTree.addPoints(player, amount);
                    break;
                case REMOVE:
                    success = SkillTree.addPoints(player, -amount);
                    break;
                case SET:
                    success = SkillTree.setPoints(player, amount);
                    break;
                case GET:
                    PlayerSkills playerSkills = SkillTree.getPlayerSkills(player);
                    if (playerSkills != null) {
                        success = true;
                        amount = playerSkills.getSkillPoints();
                    }
                    break;
                default:
                    break;
            }
            if (success)
                effected++;
        }
        if (effected == 0) {
            if (targets.size() == 1) {
                throw new CommandRuntimeException(Component.translatable(action.getKey() + ".single.failure",
                        targets.iterator().next().getDisplayName(), amount));
            } else if (targets.size() > 1) {
                throw new CommandRuntimeException(Component.translatable(action.getKey() + ".many.failure",
                        targets.iterator().next().getDisplayName(), amount));
            } else {
                throw new CommandRuntimeException(Component.translatable(action.getKey() + ".none.failure",
                        targets.iterator().next().getDisplayName(), amount));
            }
        } else {
            if (targets.size() == 1) {
                source.sendSuccess(
                        Component.translatable(action.getKey() + ".single.success",
                                targets.iterator().next().getDisplayName(), amount),
                        true);
            } else {
                source.sendSuccess(
                        Component.translatable(action.getKey() + ".many.success", targets.size(), amount), true);
            }

            return effected;
        }

    }

    public enum Action {
        GRANT("grant"),
        REVOKE("revoke"),
        QUERY("query"),
        ADD("sp_add"),
        REMOVE("sp_remove"),
        SET("sp_set"),
        GET("sp_get"),;

        private final String key;

        Action(String key) {
            this.key = "commands.skilltreemod." + key;
        }

        protected String getKey() {
            return key;
        }
    }
}
