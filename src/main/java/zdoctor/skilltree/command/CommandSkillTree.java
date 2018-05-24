package zdoctor.skilltree.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.interfaces.ISkillHandler;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.SkillSlot;

public class CommandSkillTree extends CommandBase {

	private List<String> aliases;

	public CommandSkillTree() {
		this.aliases = new ArrayList<String>();
		this.aliases.add("sk");
		this.aliases.add("skill");
	}

	@Override
	public String getName() {
		return "skilltree";
	}

	@Override
	public List<String> getAliases() {
		return aliases;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.skilltree.usage";
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "reset", "addpoints", "give", "remove");
		}
		if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		} else if (args[0].equalsIgnoreCase("reset")) {
			return super.getTabCompletions(server, sender, args, targetPos);
		} else if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("remove")) {
			List<String> list = getListOfStringsMatchingLastWord(args, SkillBase.getSkillRegistry());
			List<String> list2 = getListOfStringsMatchingLastWord(args, "all");
			list.addAll(list2);
			return list;
		}

		return super.getTabCompletions(server, sender, args, targetPos);

	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new WrongUsageException("commands.skilltree.usage", new Object[0]);
		} else {
			String action = args[0].toLowerCase();
			EntityPlayer entity = args.length > 1 ? getPlayer(server, sender, args[1])
					: getCommandSenderAsPlayer(sender);

			switch (action) {
			case "reset":
				SkillTreeApi.resetSkillHandler(entity);
				notifyCommandListener(sender, this, "commands.skilltree.success.reset", entity.getName());
				break;
			case "addpoints":
				String pointString = args.length > 2 ? args[2] : args.length > 1 ? args[1] : " ";
				try {
					int pointsToAdd = Integer.parseInt(pointString);
					SkillTreeApi.addSkillPoints(entity, pointsToAdd);
					if (pointsToAdd >= 0)
						notifyCommandListener(sender, this, "commands.skilltree.success.addPoints", pointsToAdd,
								entity.getName());
					else
						notifyCommandListener(sender, this, "commands.skilltree.success.removePoints", pointsToAdd,
								entity.getName());
				} catch (NumberFormatException e) {
					throw new CommandException("commands.skilltree.failure.addPoints", pointString, entity.getName());
				}
				break;
			case "give":

				String skillName = args.length > 2 ? args[2] : args.length > 1 ? args[1] : "Invalid";
				SkillBase skill = SkillBase.getSkillByKey(new ResourceLocation(skillName));
				if (skillName.equalsIgnoreCase("all")) {
					ISkillHandler skillHandler = SkillTreeApi.getSkillHandler(entity);
					skillHandler.getSkillSlots().forEach(skillSlot -> {
						skillSlot.setObtained();
						skillSlot.setActive();
					});
					// SkillTreeApi.reloadHandler(entity);
					SkillTreeApi.syncSkills(entity);
					notifyCommandListener(sender, this, "commands.skilltree.success.give.all", entity.getName(),
							entity.getName());
					break;
				}

				if (skill == null)
					throw new CommandException("commands.skilltree.failure.give", entity.getName(), skillName);
				if (SkillTreeApi.hasSkill(entity, skill))
					throw new CommandException("commands.skilltree.failure.give.owned", entity.getName(), skillName);

				ArrayList<SkillSlot> neededSkills = new ArrayList<>();
				SkillSlot currentSkillNode = SkillTreeApi.getSkillSlot(entity, skill);
				neededSkills.add(currentSkillNode);
				while (currentSkillNode.getSkill().getParent() != null) {
					currentSkillNode = SkillTreeApi.getSkillSlot(entity, currentSkillNode.getSkill().getParent());
					if (currentSkillNode.isObtained())
						break;
					neededSkills.add(currentSkillNode);
				}

				ISkillHandler skillHandler = SkillTreeApi.getSkillHandler(entity);
				for (int i = neededSkills.size() - 1; i >= 0; i--) {
					skillHandler.getSkillSlot(neededSkills.get(i).getSkill()).setObtained();
					skillHandler.getSkillSlot(neededSkills.get(i).getSkill()).setActive();
				}

				// SkillTreeApi.reloadHandler(entity);
				SkillTreeApi.syncSkills(entity);
				for (int i = neededSkills.size() - 1; i >= 0; i--) {
					if (SkillTreeApi.hasSkill(entity, neededSkills.get(i).getSkill()))
						notifyCommandListener(sender, this, "commands.skilltree.success.give",
								I18n.translateToLocal(neededSkills.get(i).getSkill().getUnlocaizedName()),
								entity.getName(), entity.getName());
					else
						notifyCommandListener(sender, this, "commands.skilltree.failure.give", entity.getName(),
								I18n.translateToLocal(neededSkills.get(i).getSkill().getUnlocaizedName()),
								entity.getName());
				}

				break;
			case "remove":
				String skillName1 = args.length > 2 ? args[2] : args.length > 1 ? args[1] : "Invalid";
				SkillBase skill1 = SkillBase.getSkillByKey(new ResourceLocation(skillName1));
				if (skillName1.equalsIgnoreCase("all")) {
					SkillTreeApi.resetSkillHandler(entity);
					notifyCommandListener(sender, this, "commands.skilltree.success.reset", entity.getName());
					break;
				}

				if (skill1 == null)
					throw new CommandException("commands.skilltree.failure.remove", entity.getName(), skillName1);
				if (!SkillTreeApi.hasSkill(entity, skill1))
					notifyCommandListener(sender, this, "commands.skilltree.failure.remove.unowned", entity.getName(),
							skillName1);
				SkillTreeApi.getSkillHandler(entity).setSkillObtained(skill1, false);
				SkillTreeApi.syncSkills(entity);
				if (!SkillTreeApi.hasSkill(entity, skill1))
					notifyCommandListener(sender, this, "commands.skilltree.success.remove",
							I18n.translateToLocal(skillName1), entity.getName());

				else
					throw new CommandException("commands.skilltree.failure.remove", I18n.translateToLocal(skillName1),
							entity.getName());
				break;
			default:
				throw new CommandException("commands.skilltree.failure.action", action);
			}

		}
	}

}