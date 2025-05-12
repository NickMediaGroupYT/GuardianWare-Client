package dev.guardianware.api.utilities;

import dev.guardianware.client.modules.client.ModuleCommands;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatUtils implements IMinecraft {
    public static void sendMessage(String message) {
        if (mc.player == null || mc.world == null || mc.inGameHud == null) {
            return;
        }
        Text component = Text.literal(getWatermark() + (!ModuleCommands.INSTANCE.watermarkMode.getValue().equals(ModuleCommands.WatermarkModes.None) ? " " : "") + ModuleCommands.getFirstColor() + message);
        mc.inGameHud.getChatHud().addMessage(component);
    }

    public static void sendMessage(String message, int id) {
        if (mc.player == null || mc.world == null || mc.inGameHud == null) {
            return;
        }
        Text component = Text.literal(getWatermark() + (!ModuleCommands.INSTANCE.watermarkMode.getValue().equals(ModuleCommands.WatermarkModes.None) ? " " : "") + ModuleCommands.getFirstColor() + message);
        mc.inGameHud.getChatHud().addMessage(component);
        //((IChatHud) mc.inGameHud.getChatHud()).clientMessage(component, id);
    }

    public static void sendMessage(String message, String name) {
        if (mc.player == null || mc.world == null || mc.inGameHud == null) {
            return;
        }
        Text component = Text.literal(getWatermark() + (!ModuleCommands.INSTANCE.watermarkMode.getValue().equals(ModuleCommands.WatermarkModes.None) ? " " : "") + Formatting.AQUA + "[" + name + "]: " + ModuleCommands.getFirstColor() + message);
        mc.inGameHud.getChatHud().addMessage(component);
    }

    public static void sendMessage(String message, String name, int id) {
        if (mc.player == null || mc.world == null || mc.inGameHud == null) {
            return;
        }
        Text component = Text.literal(getWatermark() + (!ModuleCommands.INSTANCE.watermarkMode.getValue().equals(ModuleCommands.WatermarkModes.None) ? " " : "") + Formatting.AQUA + "[" + name + "]: " + ModuleCommands.getFirstColor() + message);

        mc.inGameHud.getChatHud().addMessage(component);//((IChatHud) mc.inGameHud.getChatHud()).clientMessage(component, id);
    }

    public static void sendRawMessage(String message) {
        if (mc.player == null || mc.world == null || mc.inGameHud == null) {
            return;
        }
        Text component = Text.literal(message);
        mc.player.sendMessage(component);
    }

    public static String getWatermark() {
        if (!ModuleCommands.INSTANCE.watermarkMode.getValue().equals(ModuleCommands.WatermarkModes.None)) {
            return ModuleCommands.getSecondWatermarkColor() + ModuleCommands.INSTANCE.firstSymbol.getValue() + ModuleCommands.getFirstWatermarkColor() + (ModuleCommands.INSTANCE.watermarkMode.getValue().equals(ModuleCommands.WatermarkModes.Custom) ? ModuleCommands.INSTANCE.watermarkText.getValue() : (ModuleCommands.INSTANCE.watermarkMode.getValue().equals(ModuleCommands.WatermarkModes.Japanese) ? "ワンダーホエール" : "LootTech")) + ModuleCommands.getSecondWatermarkColor() + ModuleCommands.INSTANCE.secondSymbol.getValue();
        }
        return "";
    }
}
