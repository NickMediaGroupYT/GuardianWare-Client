package dev.guardianware.client.commands;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.command.Command;
import dev.guardianware.api.manager.command.RegisterCommand;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.client.modules.client.ModuleCommands;
import net.minecraft.util.Formatting;

@RegisterCommand(name="notify", description="Let's you disable or enable module toggle messages.", syntax="notify <module> <value>", aliases={"chatnotify", "togglemsg", "togglemsgs", "togglemessages"})
public class CommandNotify extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length == 2) {
            boolean found = false;
            for (Module module : GuardianWare.MODULE_MANAGER.getModules()) {
                if (!module.getName().equalsIgnoreCase(args[0])) continue;
                found = true;
                module.setChatNotify(Boolean.parseBoolean(args[1]));
                ChatUtils.sendMessage(ModuleCommands.getSecondColor() + module.getName() + ModuleCommands.getFirstColor() + " now has Toggle Messages " + (module.isChatNotify() ? Formatting.GREEN + "enabled" : Formatting.RED + "disabled") + ModuleCommands.getFirstColor() + ".", "Notify");
            }
            if (!found) {
                ChatUtils.sendMessage("Could not find module.", "Notify");
            }
        } else {
            this.sendSyntax();
        }
    }
}