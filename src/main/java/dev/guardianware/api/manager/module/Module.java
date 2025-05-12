package dev.guardianware.api.manager.module;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.event.EventListener;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.api.utilities.IMinecraft;
import dev.guardianware.client.events.EventRender2D;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.Value;
import dev.guardianware.client.values.impl.ValueBind;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueString;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public abstract class Module implements IMinecraft, EventListener {
    private ArrayList<Value> values = null;
    public String name;
    public String description;
    public Category category;
    private boolean toggled;
    private boolean persistent = false;
    private Color randomColor;
    public ValueString tag = new ValueString("Tag", "Tag", "The module's display name.", "4GquuoBHl7gkSDaNeMb5");
    public ValueBoolean chatNotify = new ValueBoolean("ChatNotify", "Chat Notify", "Notifies you in chat when the module is toggled on or off.", false);
    public ValueBoolean drawn = new ValueBoolean("Drawn", "Drawn", "Makes the module appear on the array list.", true);
    public ValueBind bind = new ValueBind("Bind", "Bind", "The module's toggle bind.", 0);


    public Module() {
        RegisterModule annotation = this.getClass().getAnnotation(RegisterModule.class);
        if (annotation != null) {
            this.name = annotation.name();
            this.tag.setValue(annotation.tag().equals(" ") ? annotation.name() : annotation.tag());
            this.description = annotation.description();
            this.category = annotation.category();
            this.persistent = annotation.persistent();
            this.bind.setValue(annotation.bind());
            this.values = new ArrayList<>();
            if (this.persistent) {
                this.setToggled(true);
                GuardianWare.EVENT_MANAGER.register(this);
            }
            this.randomColor = this.generateRandomColor();
        }
    }

    public Color generateRandomColor() {
        Random random = new Random();
        int randomRed = random.nextInt(255);
        int randomGreen = random.nextInt(255);
        int randomBlue = random.nextInt(255);
        return new Color(randomRed, randomGreen, randomBlue);
    }


    public void onTick() {
    }

    public void onUpdate() {
    }

    public void onRender2D(EventRender2D event) {
    }

    public void onRender3D(Render3DEvent event) {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onLogin() {
    }

    public void onLogout() {
    }

    public void onDeath() {
    }

    public boolean nullCheck() {
        return mc.player == null || mc.world == null;
    }

    public String getHudInfo() {
        return "";
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public String getTag() {
        return this.tag.getValue();
    }

    public void setTag(String tag) {
        this.tag.setValue(tag);
    }

    public boolean isChatNotify() {
        return this.chatNotify.getValue();
    }

    public void setChatNotify(boolean chatNotify) {
        this.chatNotify.setValue(chatNotify);
    }

    public boolean isDrawn() {
        return this.drawn.getValue();
    }

    public void setDrawn(boolean drawn) {
        this.drawn.setValue(drawn);
    }

    public int getBind() {
        return this.bind.getValue();
    }

    public void setBind(int bind) {
        this.bind.setValue(bind);
    }

    public Color getRandomColor() {
        return this.randomColor;
    }

    public void toggle(boolean message) {
        if (this.isToggled()) {
            this.disable(message);
        } else {
            this.enable(message);
        }
    }

    public void enable(boolean message) {
        if (!this.persistent) {
            this.setToggled(true);
            GuardianWare.EVENT_MANAGER.register(this);
            if (message) {
                this.doToggleMessage();
            }
            this.onEnable();
        }
    }

    public void disable(boolean message) {
        if (!this.persistent) {
            this.setToggled(false);
            GuardianWare.EVENT_MANAGER.unregister(this);
            if (message) {
                this.doToggleMessage();
            }
            this.onDisable();
        }
    }

    public void doToggleMessage() {
        if (this.isChatNotify()) {
            int number = 0;
            for (char character : this.getTag().toCharArray()) {
                number += character;
                number *= 10;
            }
            Formatting stateColor;
            String state;
            if (this.isToggled()) state = "ON"; else state = "OFF";
            if (this.isToggled()) stateColor = Formatting.GREEN; else stateColor = Formatting.RED;
            //ChatUtils.sendMessage(ModuleCommands.getSecondColor() + "" + Formatting.BOLD + this.getTag() + " " + ModuleCommands.getFirstColor() + "has been toggled " + (this.isToggled() ? Formatting.GREEN + "on" : Formatting.RED + "off") + ModuleCommands.getFirstColor() + "!", number);
            ChatUtils.sendRawMessage(Formatting.BOLD + this.getTag() + Formatting.RESET + " Toggled " + Formatting.BOLD + stateColor + state);
        }
    }

    public void sendPacket(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) return;
        mc.getNetworkHandler().sendPacket(packet);
    }

    public ArrayList<Value> getValues() {
        return this.values;
    }

    public enum Category {
        COMBAT("Combat"),
        PLAYER("Player"),
        MISCELLANEOUS("Miscellaneous"),
        MOVEMENT("Movement"),
        VISUALS("Visuals"),
        CLIENT("Client"),
        HUD("HUD");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
