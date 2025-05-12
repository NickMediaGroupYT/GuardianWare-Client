package dev.guardianware.client.commands;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.command.Command;
import dev.guardianware.api.manager.command.RegisterCommand;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.client.modules.client.ModuleCommands;
import net.minecraft.util.Formatting;

@RegisterCommand(name="drawn", description="Let's you disable or enable module drawing on the module list.", syntax="drawn <module> <value>", aliases={"shown", "show", "draw"})
public class CommandDrawn extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length == 2) {
            boolean found = false;
            if (args[0].equalsIgnoreCase("all")) {
                for (Module m : GuardianWare.MODULE_MANAGER.getModules()) {
                    m.setDrawn(Boolean.parseBoolean(args[1]));
                }
                ChatUtils.sendMessage(ModuleCommands.getSecondColor() + "All modules" + ModuleCommands.getFirstColor() + " are now " + (Boolean.parseBoolean(args[1]) ? Formatting.GREEN + "shown" : Formatting.RED + "hidden") + ModuleCommands.getFirstColor() + ".");
            } else {
                for (Module module : GuardianWare.MODULE_MANAGER.getModules()) {
                    if (!module.getName().equalsIgnoreCase(args[0])) continue;
                    found = true;
                    module.setDrawn(Boolean.parseBoolean(args[1]));
                    ChatUtils.sendMessage(ModuleCommands.getSecondColor() + module.getName() + ModuleCommands.getFirstColor() + " is now " + (module.isDrawn() ? Formatting.GREEN + "shown" : Formatting.RED + "hidden") + ModuleCommands.getFirstColor() + ".", "Drawn");
                }
            }
            if (!found) {
                ChatUtils.sendMessage("Could not find module.", "Drawn");
            }
        } else {
            this.sendSyntax();
        }
    }
}