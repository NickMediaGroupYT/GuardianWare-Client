package dev.guardianware.client.commands;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.command.Command;
import dev.guardianware.api.manager.command.RegisterCommand;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.client.modules.client.ModuleCommands;

@RegisterCommand(name="tag", description="Let's you customize any module's tag.", syntax="tag <module> <value>", aliases={"customname", "modtag", "moduletag", "mark"})
public class CommandTag extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length == 2) {
            boolean found = false;
            for (Module module : GuardianWare.MODULE_MANAGER.getModules()) {
                if (!module.getName().equalsIgnoreCase(args[0])) continue;
                found = true;
                module.setTag(args[1].replace("_", " "));
                ChatUtils.sendMessage(ModuleCommands.getSecondColor() + module.getName() + ModuleCommands.getFirstColor() + " is now marked as " + ModuleCommands.getSecondColor() + module.getTag() + ModuleCommands.getFirstColor() + ".", "Tag");
            }
            if (!found) {
                ChatUtils.sendMessage("Could not find module.", "Tag");
            }
        } else {
            this.sendSyntax();
        }
    }
}