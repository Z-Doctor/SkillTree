package zdoctor.mcskilltree.skilltree.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.impl.GiveCommand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.api.ISkillHandler;
import zdoctor.mcskilltree.api.SkillApi;
import zdoctor.mcskilltree.registries.SkillTreeRegistries;
import zdoctor.mcskilltree.skills.Skill;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Mod.EventBusSubscriber
public class SkillTreeCommands {
    private static final DynamicCommandExceptionType SKILL_NOT_FOUND = new DynamicCommandExceptionType((skill) -> new TranslationTextComponent("skill.skillNotFound", skill));

    // TODO Flickering, non-issue but annoying
    public static final SuggestionProvider<CommandSource> SUGGEST_SKILL = (context, builder) -> {
        Collection<Skill> collection = SkillTreeRegistries.SKILLS.getValues();
        return searchLocations(collection.stream().map(Skill::getRegistryName)::iterator, builder);
    };

    public static CompletableFuture<Suggestions> searchLocations(Iterable<ResourceLocation> iterable, SuggestionsBuilder builder) {
        String s = builder.getRemaining().toLowerCase(Locale.ROOT);
        parseForNamespace(iterable, s, location -> location,
                location -> builder.suggest(location.toString()));
        return builder.buildFuture();
    }

    public static <T> void parseForNamespace(Iterable<T> iterable, String strIn, Function<T, ResourceLocation> converter, Consumer<T> transformer) {
        boolean flag = strIn.indexOf(58) > -1;
        for (T t : iterable) {
            ResourceLocation resourcelocation = converter.apply(t);
            String s = resourcelocation.toString();
            if (flag) {
                if (s.startsWith(strIn)) {
                    transformer.accept(t);
                }
            } else if (s.contains(strIn)) {
                transformer.accept(t);
            }
        }

    }

    public static final RequiredArgumentBuilder<CommandSource, ResourceLocation> SKILL_RESOURCE = Commands.argument("skill", ResourceLocationArgument.resourceLocation());

    public static final Predicate<CommandSource> HAS_PERMISSION = (commandSource -> commandSource.hasPermissionLevel(2));

    public static Command<CommandSource> applySkillCommand(Action action, Mode mode) {
        return command -> {
            try {
                if (mode == Mode.EVERYTHING)
                    return forEachSkill(command.getSource(), EntityArgument.getPlayers(command, "targets"), action, SkillTreeRegistries.SKILLS.getValues());
                else
                    return forEachSkill(command.getSource(), EntityArgument.getPlayers(command, "targets"), action,
                            getMatchingSkills(getSkill(command, "skill"), mode));
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
                return 0;
            }
        };
    }

    public static Command<CommandSource> adjustSkillPoints(boolean give) {
        return command -> {
            int effected = 0;
            int amount = IntegerArgumentType.getInteger(command, "amount");
            Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(command, "targets");
            for (ServerPlayerEntity player : targets) {
                boolean success = give ? SkillApi.addSkillPoints(player, amount) : SkillApi.deductSkillPoints(player, amount, true);
                if (success)
                    effected += 1;
            }
            CommandSource source = command.getSource();
            String key = give ? "give" : "take";
            if (effected == 0) {
                if (targets.size() == 1) {
                    throw new CommandException(new TranslationTextComponent(key + ".skillpoint" + (amount == 1 ? "." : "s.") + "failure", amount, targets.iterator().next().getDisplayName()));
                } else {
                    throw new CommandException(new TranslationTextComponent(key + ".skillpoint" + (amount == 1 ? "." : "s.") + "many.failure", amount, targets.size()));
                }
            } else {
                if (targets.size() == 1) {
                    source.sendFeedback(new TranslationTextComponent(key + ".skillpoint" + (amount == 1 ? "." : "s.") + "success", amount, targets.iterator().next().getDisplayName()), true);
                } else {
                    source.sendFeedback(new TranslationTextComponent(key + ".skillpoint" + (amount == 1 ? "." : "s.") + "many.success", amount, targets.size()), true);
                }
            }

            return effected;
        };
    }


    @SubscribeEvent
    public static void register(FMLServerStartingEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
        McSkillTree.LOGGER.debug("Registering Skill Commands");

        dispatcher.register(Commands.literal("skill").requires(HAS_PERMISSION).
                then(Commands.literal("grant").then(Commands.argument("targets", EntityArgument.players()).
                        then(Commands.literal("only").then(SKILL_RESOURCE.suggests(SUGGEST_SKILL).
                                executes(applySkillCommand(Action.GRANT, Mode.ONLY)))).
                        then(Commands.literal("through").then(SKILL_RESOURCE.suggests(SUGGEST_SKILL).
                                executes(applySkillCommand(Action.GRANT, Mode.THROUGH)))).
                        then(Commands.literal("from").then(SKILL_RESOURCE.suggests(SUGGEST_SKILL).
                                executes(applySkillCommand(Action.GRANT, Mode.FROM)))).
                        then(Commands.literal("until").then(SKILL_RESOURCE.suggests(SUGGEST_SKILL).
                                executes(applySkillCommand(Action.GRANT, Mode.UNTIL)))).
                        then(Commands.literal("everything").
                                executes(applySkillCommand(Action.GRANT, Mode.EVERYTHING)))
                )).
                then(Commands.literal("revoke").then(Commands.argument("targets", EntityArgument.players()).
                        then(Commands.literal("only").then(SKILL_RESOURCE.suggests(SUGGEST_SKILL).
                                executes(applySkillCommand(Action.REVOKE, Mode.ONLY)))).
                        then(Commands.literal("through").then(SKILL_RESOURCE.suggests(SUGGEST_SKILL).
                                executes(applySkillCommand(Action.REVOKE, Mode.THROUGH)))).
                        then(Commands.literal("from").then(SKILL_RESOURCE.suggests(SUGGEST_SKILL).
                                executes(applySkillCommand(Action.REVOKE, Mode.FROM)))).
                        then(Commands.literal("until").then(SKILL_RESOURCE.suggests(SUGGEST_SKILL).
                                executes(applySkillCommand(Action.REVOKE, Mode.UNTIL)))).
                        then(Commands.literal("everything").
                                executes(applySkillCommand(Action.REVOKE, Mode.EVERYTHING)))
                )).
                then(Commands.literal("skillpoints").then(Commands.argument("targets", EntityArgument.players()).
                        then(Commands.literal("give").then(Commands.argument("amount", IntegerArgumentType.integer(1)).
                                executes(adjustSkillPoints(true)))).
                        then(Commands.literal("deduct").then(Commands.argument("amount", IntegerArgumentType.integer(1)).
                                executes(adjustSkillPoints(false))))
                )));

        dispatcher.register(Commands.literal("skillpoints").executes(command -> {
            CommandSource source = command.getSource();
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
            int amount = SkillApi.getSkillPoints(player);
            source.sendFeedback(new StringTextComponent(String.format("You have %s skillpoints", amount)), false);
            if (amount == 1)
                source.sendFeedback(new TranslationTextComponent("skillpoint.self.query.single", amount), false);
            else
                source.sendFeedback(new TranslationTextComponent("skillpoint.self.query", amount), false);
            return 1;
        }));
//        net.minecraft.command.impl.GiveCommand
    }


    private static int forEachSkill(CommandSource source, Collection<ServerPlayerEntity> targets, Action action, Collection<Skill> skills) {
        int effected = 0;

        for (ServerPlayerEntity serverplayerentity : targets) {
            effected += action.applySkillsTo(serverplayerentity, skills);
        }

        if (effected == 0) {
            if (skills.size() == 1) {
                if (targets.size() == 1) {
                    throw new CommandException(new TranslationTextComponent(action.getPrefix() + ".one.to.one.failure", skills.iterator().next().getDisplayText(), targets.iterator().next().getDisplayName()));
                } else {
                    throw new CommandException(new TranslationTextComponent(action.getPrefix() + ".one.to.many.failure", skills.iterator().next().getDisplayText(), targets.size()));
                }
            } else if (targets.size() == 1) {
                throw new CommandException(new TranslationTextComponent(action.getPrefix() + ".many.to.one.failure", skills.size(), targets.iterator().next().getDisplayName()));
            } else {
                throw new CommandException(new TranslationTextComponent(action.getPrefix() + ".many.to.many.failure", skills.size(), targets.size()));
            }
        } else {
            if (skills.size() == 1) {
                if (targets.size() == 1) {
                    source.sendFeedback(new TranslationTextComponent(action.getPrefix() + ".one.to.one.success", skills.iterator().next().getDisplayText(), targets.iterator().next().getDisplayName()), true);
                } else {
                    source.sendFeedback(new TranslationTextComponent(action.getPrefix() + ".one.to.many.success", skills.iterator().next().getDisplayText(), targets.size()), true);
                }
            } else if (targets.size() == 1) {
                source.sendFeedback(new TranslationTextComponent(action.getPrefix() + ".many.to.one.success", skills.size(), targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendFeedback(new TranslationTextComponent(action.getPrefix() + ".many.to.many.success", skills.size(), targets.size()), true);
            }

            return effected;
        }
    }

    private static void getParents(List<Skill> list, Skill skill) {
        if (!skill.hasParents())
            return;

        for (Skill parent : skill.getParents()) {
            getParents(list, parent);
            list.add(parent);
        }
    }

    private static List<Skill> getMatchingSkills(Skill skill, Mode mode) {
        List<Skill> list = Lists.newArrayList();
        if (mode.includesParents) {
            getParents(list, skill);
        }

        list.add(skill);
        if (mode.includesChildren) {
            list.addAll(skill.getAllChildren());
        }

        return list;
    }

    public static Skill getSkill(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        ResourceLocation resourcelocation = context.getArgument(name, ResourceLocation.class);
        Skill skill = SkillTreeRegistries.SKILLS.getValue(resourcelocation);
        if (skill == null) {
            throw SKILL_NOT_FOUND.create(resourcelocation);
        } else {
            return skill;
        }
    }

    enum Mode {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);

        private final boolean includesParents;
        private final boolean includesChildren;

        Mode(boolean includesParentsIn, boolean includesChildrenIn) {
            this.includesParents = includesParentsIn;
            this.includesChildren = includesChildrenIn;
        }
    }

    enum Action {
        GRANT("grant") {
            @Override
            public int applySkillsTo(LivingEntity entity, Collection<Skill> skills) {
                int i = 0;
                ISkillHandler skillHandler = SkillApi.getSkillHandler(entity);
                for (Skill skill : skills) {
                    if (skillHandler.give(skill))
                        ++i;
                }
//                if(i > 0)
//                    skillHandler.updateSkillData();
                return i;
            }
        },
        REVOKE("revoke");

        private final String prefix;

        Action(String name) {
            this.prefix = "commands.skill." + name;
        }

        public int applySkillsTo(LivingEntity entity, Collection<Skill> skills) {
            int i = 0;
            ISkillHandler skillHandler = SkillApi.getSkillHandler(entity);
            for (Skill skill : skills) {
                if (skillHandler.revoke(skill))
                    ++i;
            }

            return i;
        }

        public String getPrefix() {
            return prefix;
        }
    }
}
