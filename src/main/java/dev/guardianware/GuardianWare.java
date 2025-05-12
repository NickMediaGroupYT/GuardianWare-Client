package dev.guardianware;

import dev.guardianware.api.manager.command.CommandManager;
import dev.guardianware.api.manager.element.ElementManager;
import dev.guardianware.api.manager.event.EventManager;
import dev.guardianware.api.manager.friend.FriendManager;
import dev.guardianware.api.manager.miscellaneous.ConfigManager;
import dev.guardianware.api.manager.miscellaneous.PlayerManager;
import dev.guardianware.api.manager.module.ModuleManager;
import dev.guardianware.api.manager.rotation.RotationManager;
import dev.guardianware.api.utilities.TPSUtils;
import dev.guardianware.client.gui.click.ClickGuiScreen;
import dev.guardianware.client.gui.hud.HudEditorScreen;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class GuardianWare implements ModInitializer {
    public static final String NAME = "GuardianWare";
    public static final String VERSION = "v14";
    public static final Logger LOGGER = LoggerFactory.getLogger("GuardianWare");

    public static Color COLOR_CLIPBOARD;
    public static CommandManager COMMAND_MANAGER;
    public static EventManager EVENT_MANAGER;
    public static FriendManager FRIEND_MANAGER;
    public static ModuleManager MODULE_MANAGER;
    public static ElementManager ELEMENT_MANAGER;
    public static PlayerManager PLAYER_MANAGER;
    public static ClickGuiScreen CLICK_GUI;
    public static HudEditorScreen HUD_EDITOR;
    public static ConfigManager CONFIG_MANAGER;
    public static RotationManager ROTATION;

    @Override
    public void onInitialize() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("Initialization process for GuardianWare has started!");

        EVENT_MANAGER = new EventManager();
        COMMAND_MANAGER = new CommandManager();
        FRIEND_MANAGER = new FriendManager();
        MODULE_MANAGER = new ModuleManager();
        ELEMENT_MANAGER = new ElementManager();
        PLAYER_MANAGER = new PlayerManager();
        CLICK_GUI = new ClickGuiScreen();
        HUD_EDITOR = new HudEditorScreen();
        CONFIG_MANAGER = new ConfigManager();
        CONFIG_MANAGER.load();
        CONFIG_MANAGER.attach();
        new TPSUtils();
        ROTATION = new RotationManager();

        long endTime = System.currentTimeMillis();
        LOGGER.info("Initialization process for GuardianWare has finished! Took {} ms", endTime - startTime);
    }
}
