package dev.guardianware.client.commands;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.command.Command;
import dev.guardianware.api.manager.command.RegisterCommand;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.client.modules.client.ModuleCommands;
import net.minecraft.util.Formatting;

@RegisterCommand(name="toggle", description="Let's you toggle a module by name.", syntax="toggle <module>", aliases={"t"})
public class CommandToggle extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length == 1) {
            boolean found = false;
            for (Module module : GuardianWare.MODULE_MANAGER.getModules()) {
                if (!module.getName().equalsIgnoreCase(args[0])) continue;
                module.toggle(false);
                ChatUtils.sendMessage(ModuleCommands.getSecondColor() + "" + Formatting.BOLD + module.getTag() + ModuleCommands.getFirstColor() + " has been toggled " + (module.isToggled() ? Formatting.GREEN + "on" : Formatting.RED + "off") + ModuleCommands.getFirstColor() + "!", "Toggle");
                found = true;
                break;
            }
            if (!found) {
                ChatUtils.sendMessage("Could not find module.", "Toggle");
            }
        } else {
            this.sendSyntax();
        }
    }
}
