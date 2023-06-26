package com.zdoctorsmods.skilltreemod.server;

import java.util.Collection;
import java.util.Collections;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.zdoctorsmods.skilltreemod.SkillTreeMod;
import com.zdoctorsmods.skilltreemod.skills.Skill;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SkillCommands {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_SKILLS = (context, builder) -> {
        Collection<Skill> skills = SkillTreeMod.getSkillManager().getAllSkills();
        return SharedSuggestionProvider.suggestResource(skills.stream().map(Skill::getId), builder);
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

        LiteralArgumentBuilder<CommandSourceStack> pointsCommand = skillCommands.then(Commands.literal("points"));
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
                .then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(context -> {
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

        dispatcher.register(skillCommands);
    }

    private static int performPoints(CommandSourceStack source, Collection<ServerPlayer> targets, Action action,
            int amount) {
        int effected = 0;
        for (ServerPlayer player : targets) {
            boolean success = false;
            switch (action) {
                case ADD:
                    success = SkillTreeMod.addPoints(player, amount);
                    break;
                case REMOVE:
                    success = SkillTreeMod.addPoints(player, -amount);
                    break;
                case SET:
                    success = SkillTreeMod.setPoints(player, amount);
                    break;
                case GET:
                    PlayerSkills playerSkills = SkillTreeMod.getPlayerSkills(player);
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
            } else {
                throw new CommandRuntimeException(Component.translatable(action.getKey() + ".many.failure",
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
        ADD("sp_add"),
        REMOVE("sp_remove"),
        SET("sp_set"),
        GET("sp_get");

        private final String key;

        Action(String key) {
            this.key = "commands.skilltreemod." + key;
        }

        protected String getKey() {
            return key;
        }
    }
}
