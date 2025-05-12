package dev.guardianware.client.commands;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.command.Command;
import dev.guardianware.api.manager.command.RegisterCommand;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.client.modules.client.ModuleCommands;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@RegisterCommand(name="bind", description="Let's you bind a module with commands.", syntax="bind <name> <key> | clear", aliases={"key", "keybind", "b"})
public class CommandBind extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length == 2) {
            String name = args[0];
            String key = args[1];
            boolean found = false;
            for (Module module : GuardianWare.MODULE_MANAGER.getModules()) {
                if (!module.getName().equalsIgnoreCase(name)) continue;
                int keyCode = InputUtil.fromTranslationKey("key.keyboard." + key.toLowerCase()).getCode();
                module.setBind(keyCode);
                ChatUtils.sendMessage("Bound " + ModuleCommands.getSecondColor() + module.getTag() + ModuleCommands.getFirstColor() + " to " + ModuleCommands.getSecondColor() + GLFW.glfwGetKeyName(module.getBind(), 1).toUpperCase() + ModuleCommands.getFirstColor() + ".", "Bind");
                found = true;
                break;
            }
            if (!found) {
                ChatUtils.sendMessage("Could not find module.", "Bind");
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                for (Module module : GuardianWare.MODULE_MANAGER.getModules()) {
                    module.setBind(0);
                }
                ChatUtils.sendMessage("Successfully cleared all binds.", "Bind");
            } else {
                this.sendSyntax();
            }
        } else {
            this.sendSyntax();
        }
    }
}
