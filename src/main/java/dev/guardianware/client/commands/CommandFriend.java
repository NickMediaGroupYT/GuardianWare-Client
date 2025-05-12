package dev.guardianware.client.commands;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.command.Command;
import dev.guardianware.api.manager.command.RegisterCommand;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.client.modules.client.ModuleCommands;
import net.minecraft.util.Formatting;

@RegisterCommand(name="Friend", description="Let's you add friends.", syntax="friend <add/del> <name>", aliases={"friend", "f"})
public class CommandFriend extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length == 1) {
            ChatUtils.sendMessage("You have " + (GuardianWare.FRIEND_MANAGER.getFriends().size() + 1) + " friends");
            return;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                if (GuardianWare.FRIEND_MANAGER.isFriend(args[1])) {
                    ChatUtils.sendMessage(ModuleCommands.getSecondColor() + args[1] + ModuleCommands.getFirstColor() + " is already a friend!");
                    return;
                }
                if (!GuardianWare.FRIEND_MANAGER.isFriend(args[1])) {
                    GuardianWare.FRIEND_MANAGER.addFriend(args[1]);
                    ChatUtils.sendMessage(Formatting.GREEN + "Added " + ModuleCommands.getSecondColor() + args[1] + ModuleCommands.getFirstColor() + " to friends list");
                }
            }
            if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove")) {
                if (!GuardianWare.FRIEND_MANAGER.isFriend(args[1])) {
                    ChatUtils.sendMessage(ModuleCommands.getSecondColor() + args[1] + ModuleCommands.getFirstColor() + " is not a friend!");
                    return;
                }
                if (GuardianWare.FRIEND_MANAGER.isFriend(args[1])) {
                    GuardianWare.FRIEND_MANAGER.removeFriend(args[1]);
                    ChatUtils.sendMessage(Formatting.RED + "Removed " + ModuleCommands.getSecondColor() + args[1] + ModuleCommands.getFirstColor() + " from friends list");
                }
            }
        } else {
            this.sendSyntax();
        }
    }
}