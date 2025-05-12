package dev.guardianware.client.commands;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.command.Command;
import dev.guardianware.api.manager.command.RegisterCommand;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.client.modules.client.ModuleCommands;

@RegisterCommand(name="Prefix", description="Let's you change your command prefix.", syntax="prefix <input>", aliases={"commandprefix", "cmdprefix", "commandp", "cmdp"})
public class CommandPrefix extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length == 1) {
            if (args[0].length() > 2) {
                ChatUtils.sendMessage("The prefix must not be longer than 2 characters.", "Prefix");
            } else {
                GuardianWare.COMMAND_MANAGER.setPrefix(args[0]);
                ChatUtils.sendMessage("Prefix set to \"" + ModuleCommands.getSecondColor() + GuardianWare.COMMAND_MANAGER.getPrefix() + ModuleCommands.getFirstColor() + "\"!", "Prefix");
            }
        } else {
            this.sendSyntax();
        }
    }
}
