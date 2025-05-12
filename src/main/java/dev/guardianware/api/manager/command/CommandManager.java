package dev.guardianware.api.manager.command;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.event.EventListener;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.api.utilities.IMinecraft;
import dev.guardianware.client.commands.*;
import dev.guardianware.client.events.EventChatSend;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager implements IMinecraft, EventListener {
    private String prefix = ".";
    private final ArrayList<Command> commands;

    public CommandManager() {
        GuardianWare.EVENT_MANAGER.register(this);
        this.commands = new ArrayList<>();
        // write commands here
        this.register(new CommandBind());
        this.register(new CommandConfig());
        this.register(new CommandDrawn());
        this.register(new CommandFolder());
        this.register(new CommandFriend());
        this.register(new CommandNotify());
        this.register(new CommandPrefix());
        this.register(new CommandTag());
        this.register(new CommandToggle());
    }

    public void register(Command command) {
        this.commands.add(command);
    }

    @Override
    public void onChatSend(EventChatSend event) {
        String message = event.getMessage();
        if (message.startsWith(this.getPrefix())) {
            event.cancel();
            message = message.substring(this.getPrefix().length());
            if (message.split(" ").length > 0) {
                String name = message.split(" ")[0];
                boolean found = false;
                for (Command command : this.getCommands()) {
                    if (!command.getAliases().contains(name.toLowerCase()) && !command.getName().equalsIgnoreCase(name)) continue;
                    mc.inGameHud.getChatHud().addToMessageHistory(event.getMessage());
                    command.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length));
                    found = true;
                    break;
                }
                if (!found) {
                    ChatUtils.sendMessage("Command could not be found.");
                }
            }
        }
    }

    public ArrayList<Command> getCommands() {
        return this.commands;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
