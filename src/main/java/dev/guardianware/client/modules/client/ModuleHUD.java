package dev.guardianware.client.modules.client;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.ColorUtils;
import dev.guardianware.api.utilities.TPSUtils;
import dev.guardianware.api.utilities.TimerUtils;
import dev.guardianware.client.events.EventRender2D;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueEnum;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiling.jfr.event.PacketSentEvent;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;

@RegisterModule(name="HUD", tag = "HUD", description="Render information on screen.", category=Module.Category.CLIENT)
public class ModuleHUD extends Module {
    public static ModuleHUD INSTANCE;
    ValueBoolean watermark = new ValueBoolean("Watermark", "Watermark", "", true);
    ValueBoolean direction = new ValueBoolean("Direction", "Direction", "", true);
    ValueBoolean potionEffects = new ValueBoolean("PotionEffects", "Potion Effects", "", true);
    public ValueEnum effectHud = new ValueEnum("EffectHUD", "Effect HUD", "", effectHuds.Hide);
    ValueBoolean serverBrand = new ValueBoolean("ServerBrand", "Server Brand", "", true);
    ValueBoolean tps = new ValueBoolean("TPS", "TPS", "", true);
    ValueBoolean fps = new ValueBoolean("FPS", "FPS", "", true);
    ValueBoolean speed = new ValueBoolean("Speed", "Speed", "", true);
    ValueBoolean ping = new ValueBoolean("Ping", "Ping", "", true);
    ValueBoolean packetPS = new ValueBoolean("PacketsPS", "Packets/s", "", false);
    ValueEnum ordering = new ValueEnum("Ordering", "Ordering", "", orderings.Length);
    ValueBoolean coords = new ValueBoolean("Coords", "Coords", "", true);
    ValueBoolean netherCoords = new ValueBoolean("NetherCoords", "Nether Coords", "", true);
    ValueBoolean durability = new ValueBoolean("Durability", "Durability", "", true);
    ValueBoolean arrayList = new ValueBoolean("ArrayList", "Array List", "", true);
    ValueEnum modulesColor = new ValueEnum("ModulesColor", "Modules Color", "Color mode for array list.", modulesColors.Normal);
    ValueEnum rendering = new ValueEnum("Rendering", "Rendering", "", renderings.Up);
    DecimalFormat format = new DecimalFormat("#.#");
    ArrayList<Module> modules;
    private int packets;
    TimerUtils packetTimer = new TimerUtils();
    private int maxFPS = 0;
    int compAdd;
    ArrayList<String> comps;
    Formatting reset = Formatting.RESET;
    Formatting gray = Formatting.GRAY;

    public ModuleHUD() {
        INSTANCE = this;
    }

    @EventHandler
    public void onPacketSend(PacketSentEvent event) {
        ++this.packets;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.packetTimer.hasTimeElapsed(1000L)) {
            this.packets = 0;
            this.packetTimer.reset();
        }
        if (mc.getCurrentFps() > this.maxFPS) {
            this.maxFPS = mc.getCurrentFps();
        }
    }

    @Override
    public void onRender2D(EventRender2D event) {
        DrawContext context = event.getContext();
        if (this.nullCheck()) {
            return;
        }

        // Durability color
        Color durabilityColor = Color.WHITE;
        ItemStack heldItem = mc.player.getMainHandStack();
        if (this.isItemTool(heldItem.getItem())) {
            float green = ((float) heldItem.getMaxDamage() - (float) heldItem.getDamage()) / (float) heldItem.getMaxDamage();
            float red = 1.0f - green;
            durabilityColor = new Color(red, green, 0.0f);
        }
        this.modules = new ArrayList<>();
        this.compAdd = 0;
        this.comps = new ArrayList<>();
        // Screen dimensions
        float sWidth = mc.getWindow().getScaledWidth();
        float sHeight = mc.getWindow().getScaledHeight();

        if (watermark.getValue()) {
            String watermarkText = GuardianWare.NAME+" "+ GuardianWare.VERSION;
            context.drawTextWithShadow(mc.textRenderer, watermarkText, 2,2, ModuleColor.getColor().getRGB());
        }

        // Direction
        if (this.direction.getValue()) {
            String directionText = this.getDirectionName() + this.gray + " [" + this.reset + this.getFacing(mc.player.getHorizontalFacing().getName()) + this.gray + "]";
            context.drawTextWithShadow(mc.textRenderer, directionText, (int) 2.0f, (int) (sHeight - 12.0f - (this.coords.getValue() ? 10 : 0)), -1);
        }

        // Coordinates
        if (this.coords.getValue()) {
            String coordsText = this.gray + "XYZ" + this.reset + " " + this.format.format(mc.player.getX()) + this.gray + ", " + this.reset + this.format.format(mc.player.getY()) + this.gray + ", " + this.reset + this.format.format(mc.player.getZ()) +
                    (this.netherCoords.getValue() ? this.gray + " [" + this.reset + this.format.format(mc.player.getWorld().getRegistryKey().getValue().equals("minecraft:the_nether") ? mc.player.getX() * 8.0 : mc.player.getX() / 8.0) + this.gray + ", " + this.reset + this.format.format(mc.player.getWorld().getRegistryKey().getValue().equals("minecraft:the_nether") ? mc.player.getZ() * 8.0 : mc.player.getZ() / 8.0) + this.gray + "]" : "");
            context.drawTextWithShadow(mc.textRenderer, coordsText, (int) 2.0f, (int) (sHeight - 12.0f), -1);
        }

        // ArrayList
        if (this.arrayList.getValue()) {
            modules.clear();
            for (Module module : GuardianWare.MODULE_MANAGER.getModules()) {
                if (module.isToggled() && module.isDrawn()) {
                    modules.add(module);
                }
            }
            if (!modules.isEmpty()) {
                int addY = 0;
                if (this.ordering.getValue().equals(orderings.Length)) {
                    for (Module m : modules.stream().sorted(Comparator.comparing(s -> mc.textRenderer.getWidth(s.getTag() + (s.getHudInfo().isEmpty() ? "" : this.gray + " [" + Formatting.WHITE + s.getHudInfo() + this.gray + "]")) * -1.0f)).toList()) {
                        String string = m.getTag() + (m.getHudInfo().isEmpty() ? "" : this.gray + " [" + Formatting.WHITE + m.getHudInfo() + this.gray + "]");
                        float x = sWidth - 2.0f - mc.textRenderer.getWidth(string);
                        float y = this.rendering.getValue().equals(renderings.Up) ? (float) (2 + addY * 10 + (this.effectHud.getValue().equals(effectHuds.Shift) && !mc.player.getActiveStatusEffects().isEmpty() ? 25 : 0)) : sHeight - 12.0f - (float) (addY * 10);
                        context.drawTextWithShadow(mc.textRenderer, string, (int) x, (int) y, this.modulesColor.getValue().equals(modulesColors.Normal) ? ModuleColor.getColor().getRGB() : (this.modulesColor.getValue().equals(modulesColors.Random) ? m.getRandomColor().getRGB() : ColorUtils.rainbow(addY).getRGB()));
                        ++addY;
                    }
                } else {
                    for (Module m : modules.stream().sorted(Comparator.comparing(Module::getName)).toList()) {
                        String string = m.getTag() + (m.getHudInfo().isEmpty() ? "" : this.gray + " [" + Formatting.WHITE + m.getHudInfo() + this.gray + "]");
                        float x = sWidth - 2.0f - mc.textRenderer.getWidth(string);
                        float y = this.rendering.getValue().equals(renderings.Up) ? (float) (2 + addY * 10 + (this.effectHud.getValue().equals(effectHuds.Shift) && !mc.player.getActiveStatusEffects().isEmpty() ? 25 : 0)) : sHeight - 12.0f - (float) (addY * 10);
                        context.drawTextWithShadow(mc.textRenderer, string, (int) x, (int) y, (this.modulesColor.getValue().equals(modulesColors.Normal) ? ModuleColor.getColor().getRGB() : (this.modulesColor.getValue().equals(modulesColors.Random) ? m.getRandomColor().getRGB() : ColorUtils.rainbow(addY).getRGB())));
                        ++addY;
                    }
                }
            }
        }

        // Potion effects
        if (this.potionEffects.getValue()) {
            int[] potCount = new int[]{0};
            try {
                mc.player.getActiveStatusEffects().forEach((effect, instance) -> {
                    String name = Text.translatable(effect.getType().name()).getString();
                    int duration = instance.getDuration();
                    int amplifier = instance.getAmplifier() + 1;
                    int potionColor = effect.value().getColor();
                    double p1 = duration % 60.0;
                    DecimalFormat format2 = new DecimalFormat("00");
                    String seconds = format2.format(p1);
                    String s = name + " " + amplifier + Formatting.WHITE + " " + duration / 60 + ":" + seconds;
                    context.drawTextWithShadow(mc.textRenderer, s, (int) (sWidth - 2.0f - mc.textRenderer.getWidth(s)), (int) (this.rendering.getValue().equals(renderings.Down) ? (float) (2 + potCount[0] * 10 + (this.effectHud.getValue().equals(effectHuds.Shift) && !mc.player.getActiveStatusEffects().isEmpty() ? 25 : 0)) : sHeight - 12.0f - (float) (potCount[0] * 10)), new Color(potionColor).getRGB());
                    potCount[0] = potCount[0] + 1;
                    ++this.compAdd;
                });
            } catch (NullPointerException nullPointerException) {
                nullPointerException.printStackTrace();
            }
        }

        // FPS
        if (this.fps.getValue()) {
            this.comps.add(this.gray + "FPS " + this.reset + mc.getCurrentFps() + this.gray + " [" + this.reset + this.maxFPS + this.gray + "]");
        }

        // TPS
        if (this.tps.getValue()) {
            this.comps.add(this.gray + "TPS " + this.reset + String.format("%.2f", TPSUtils.getTickRate()));
        }

        // Ping
        if (this.ping.getValue()) {
            this.comps.add(this.gray + "Ping " + this.reset + this.getPing());
        }

        // Packets/s
        if (this.packetPS.getValue()) {
            this.comps.add(this.gray + "Packets/s " + this.reset + this.packets);
        }

        // Speed
        if (this.speed.getValue()) {
            DecimalFormat df = new DecimalFormat("#.#");
            double d = mc.player.getX() - mc.player.prevX;
            double deltaZ = mc.player.getZ() - mc.player.prevZ;
            float tickRate = mc.getRenderTime();
            String speedText = df.format((double) (MathHelper.sqrt((float) (d * d + deltaZ * deltaZ)) / tickRate) * 3.6);
            this.comps.add(this.gray + "Speed " + this.reset + speedText + "km/h");
        }

        // Server brand
        if (this.serverBrand.getValue()) {
            this.comps.add(this.gray + this.getServerBrand());
        }

        // Durability
        if (this.durability.getValue()) {
            this.comps.add(this.gray + "Durability " + this.reset + (heldItem.getMaxDamage() - heldItem.getDamage()));
        }

        // Render components
        if (!this.comps.isEmpty()) {
            for (String string : this.comps.stream().sorted(Comparator.comparing(s -> mc.textRenderer.getWidth(s) * -1.0f)).toList()) {
                if (string.startsWith(this.gray + "Durability") && !this.isItemTool(heldItem.getItem())) continue;
                context.drawTextWithShadow(mc.textRenderer, string, (int) (sWidth - 2.0f - mc.textRenderer.getWidth(string)), (int) (this.rendering.getValue().equals(renderings.Down) ? (float) (2 + this.compAdd * 10 + (this.effectHud.getValue().equals(effectHuds.Shift) && !mc.player.getActiveStatusEffects().isEmpty() ? 25 : 0)) : sHeight - 12.0f - (float) (this.compAdd * 10)), string.startsWith(this.gray + "Durability") ? durabilityColor.getRGB() : -1);
                ++this.compAdd;
            }
        }
    }

    public boolean isItemTool(Item item) {
        return item instanceof ArmorItem || item == Items.NETHERITE_SWORD
                || item == Items.NETHERITE_PICKAXE || item == Items.NETHERITE_AXE
                || item == Items.NETHERITE_SHOVEL || item == Items.NETHERITE_HOE
                || item == Items.DIAMOND_SWORD || item == Items.DIAMOND_PICKAXE
                || item == Items.DIAMOND_AXE || item == Items.DIAMOND_SHOVEL
                || item == Items.DIAMOND_HOE || item == Items.IRON_SWORD
                || item == Items.IRON_PICKAXE || item == Items.IRON_AXE
                || item == Items.IRON_SHOVEL || item == Items.IRON_HOE
                || item == Items.GOLDEN_SWORD || item == Items.GOLDEN_PICKAXE
                || item == Items.GOLDEN_AXE || item == Items.GOLDEN_SHOVEL
                || item == Items.GOLDEN_HOE || item == Items.STONE_SWORD
                || item == Items.STONE_PICKAXE || item == Items.STONE_AXE
                || item == Items.STONE_SHOVEL || item == Items.STONE_HOE
                || item == Items.WOODEN_SWORD || item == Items.WOODEN_PICKAXE
                || item == Items.WOODEN_AXE || item == Items.WOODEN_SHOVEL
                || item == Items.WOODEN_HOE;
    }

    private String getDirectionName() {
        return mc.player.getHorizontalFacing().getName().substring(0, 1).toUpperCase() + mc.player.getHorizontalFacing().getName().substring(1).toLowerCase();
    }

    private String getFacing(String input) {
        switch (input.toLowerCase()) {
            case "north": {
                return "-Z";
            }
            case "east": {
                return "+X";
            }
            case "south": {
                return "+Z";
            }
        }
        return "-X";
    }

    private int getPing() {
        int ping;
        if (mc.player == null || mc.getNetworkHandler() == null || mc.getNetworkHandler().getPlayerListEntry(mc.player.getGameProfile().getName()) == null) {
            ping = -1;
        } else {
            PlayerListEntry playerInfo = mc.getNetworkHandler().getPlayerListEntry(mc.player.getGameProfile().getName());
            ping = playerInfo != null ? playerInfo.getLatency() : -1;
        }
        return ping;
    }

    private String getServerBrand() {
        String serverBrand;
        ServerInfo serverData = mc.getCurrentServerEntry();
        if (serverData == null) {
            serverBrand = "Vanilla";
        } else {
            serverBrand = mc.player.networkHandler.getBrand();
            if (serverBrand == null) {
                serverBrand = "Vanilla";
            }
        }
        return serverBrand;
    }

    public enum renderings {
        Up,
        Down
    }

    public enum modulesColors {
        Normal,
        Random,
        Rainbow
    }

    public enum orderings {
        Length,
        ABC
    }

    public enum effectHuds {
        Show,
        Hide,
        Shift
    }
}