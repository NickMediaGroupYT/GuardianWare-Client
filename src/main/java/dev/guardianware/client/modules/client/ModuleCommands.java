package dev.guardianware.client.modules.client;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.values.impl.ValueCategory;
import dev.guardianware.client.values.impl.ValueEnum;
import dev.guardianware.client.values.impl.ValueString;
import net.minecraft.util.Formatting;

@RegisterModule(name = "Commands", description = "Let's you customize commands and sending text.", category = Module.Category.CLIENT, persistent = true)
public class ModuleCommands extends Module {
    public static ModuleCommands INSTANCE;
    public final ValueCategory watermarkCategory = new ValueCategory("Watermark", "The category for the Watermark.");
    public final ValueEnum watermarkMode = new ValueEnum("WatermarkMode", "Mode", "The mode for the watermark.", this.watermarkCategory, WatermarkModes.Normal);
    public final ValueString watermarkText = new ValueString("WatermarkText", "Text", "The watermark text.", this.watermarkCategory, "GuardianWare");
    public final ValueString firstSymbol = new ValueString("WatermarkFirstSymbol", "First Symbol", "The first symbol on the watermark.", this.watermarkCategory, "[");
    public final ValueString secondSymbol = new ValueString("WatermarkSecondSymbol", "Second Symbol", "The second symbol on the watermark.", this.watermarkCategory, "]");
    public final ValueCategory firstWatermarkColorCategory = new ValueCategory("First Mark Color", "The colors for the first color on the Watermark.");
    public final ValueEnum firstWatermarkColor = new ValueEnum("FirstWatermarkColor", "Color", "The color of the first watermark color.", this.firstWatermarkColorCategory, ColorModes.Purple);
    public final ValueEnum firstWatermarkBrightness = new ValueEnum("FirstWatermarkBrightness", "Brightness", "The brightness of the second watermark color.", this.firstWatermarkColorCategory, LightModes.Light);
    public final ValueCategory secondWatermarkColorCategory = new ValueCategory("Second Mark Color", "The colors for the second color on the Watermark.");
    public final ValueEnum secondWatermarkColor = new ValueEnum("SecondWatermarkColor", "Color", "The color of the second watermark color.", this.secondWatermarkColorCategory, ColorModes.Purple);
    public final ValueEnum secondWatermarkBrightness = new ValueEnum("SecondWatermarkBrightness", "Brightness", "The brightness of the second watermark color.", this.secondWatermarkColorCategory, LightModes.Dark);
    public final ValueCategory firstColorCategory = new ValueCategory("First Color", "The first color in the chat sending.");
    public final ValueEnum firstColorMode = new ValueEnum("FirstColorMode", "Color", "The color for the First Color.", this.firstColorCategory, ColorModes.Purple);
    public final ValueEnum firstColorBrightness = new ValueEnum("FirstColorBrightness", "Brightness", "The brightness for the First Color.", this.firstColorCategory, LightModes.Light);
    public final ValueCategory secondColorCategory = new ValueCategory("Second Color", "The second color in the chat sending.");
    public final ValueEnum secondColorMode = new ValueEnum("SecondColorMode", "Color", "The color for the Second Color.", this.secondColorCategory, ColorModes.Purple);
    public final ValueEnum secondColorBrightness = new ValueEnum("SecondColorBrightness", "Brightness", "The brightness for the Second Color.", this.secondColorCategory, LightModes.Dark);

    public ModuleCommands() {
        INSTANCE = this;
    }

    public static Formatting getFirstColor() {
        switch ((ColorModes) ModuleCommands.INSTANCE.firstColorMode.getValue()) {
            case Black: {
                if (ModuleCommands.INSTANCE.firstColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.DARK_GRAY;
                }
                return Formatting.BLACK;
            }
            case Gray: {
                if (ModuleCommands.INSTANCE.firstColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.GRAY;
                }
                return Formatting.DARK_GRAY;
            }
            case Blue: {
                if (ModuleCommands.INSTANCE.firstColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.BLUE;
                }
                return Formatting.DARK_BLUE;
            }
            case Green: {
                if (ModuleCommands.INSTANCE.firstColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.GREEN;
                }
                return Formatting.DARK_GREEN;
            }
            case Aqua: {
                if (ModuleCommands.INSTANCE.firstColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.AQUA;
                }
                return Formatting.DARK_AQUA;
            }
            case Red: {
                if (ModuleCommands.INSTANCE.firstColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.RED;
                }
                return Formatting.DARK_RED;
            }
            case Yellow: {
                if (ModuleCommands.INSTANCE.firstColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.YELLOW;
                }
                return Formatting.GOLD;
            }
            case Purple: {
                if (ModuleCommands.INSTANCE.firstColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.LIGHT_PURPLE;
                }
                return Formatting.DARK_PURPLE;
            }
        }
        if (ModuleCommands.INSTANCE.firstColorBrightness.getValue().equals(LightModes.Light)) {
            return Formatting.WHITE;
        }
        return Formatting.GRAY;
    }

    public static Formatting getSecondColor() {
        switch ((ColorModes) ModuleCommands.INSTANCE.secondColorMode.getValue()) {
            case Black: {
                if (ModuleCommands.INSTANCE.secondColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.DARK_GRAY;
                }
                return Formatting.BLACK;
            }
            case Gray: {
                if (ModuleCommands.INSTANCE.secondColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.GRAY;
                }
                return Formatting.DARK_GRAY;
            }
            case Blue: {
                if (ModuleCommands.INSTANCE.secondColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.BLUE;
                }
                return Formatting.DARK_BLUE;
            }
            case Green: {
                if (ModuleCommands.INSTANCE.secondColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.GREEN;
                }
                return Formatting.DARK_GREEN;
            }
            case Aqua: {
                if (ModuleCommands.INSTANCE.secondColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.AQUA;
                }
                return Formatting.DARK_AQUA;
            }
            case Red: {
                if (ModuleCommands.INSTANCE.secondColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.RED;
                }
                return Formatting.DARK_RED;
            }
            case Yellow: {
                if (ModuleCommands.INSTANCE.secondColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.YELLOW;
                }
                return Formatting.GOLD;
            }
            case Purple: {
                if (ModuleCommands.INSTANCE.secondColorBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.LIGHT_PURPLE;
                }
                return Formatting.DARK_PURPLE;
            }
        }
        if (ModuleCommands.INSTANCE.secondColorBrightness.getValue().equals(LightModes.Light)) {
            return Formatting.WHITE;
        }
        return Formatting.GRAY;
    }

    public static Formatting getFirstWatermarkColor() {
        switch ((ColorModes) ModuleCommands.INSTANCE.firstWatermarkColor.getValue()) {
            case Black: {
                if (ModuleCommands.INSTANCE.firstWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.DARK_GRAY;
                }
                return Formatting.BLACK;
            }
            case Gray: {
                if (ModuleCommands.INSTANCE.firstWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.GRAY;
                }
                return Formatting.DARK_GRAY;
            }
            case Blue: {
                if (ModuleCommands.INSTANCE.firstWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.BLUE;
                }
                return Formatting.DARK_BLUE;
            }
            case Green: {
                if (ModuleCommands.INSTANCE.firstWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.GREEN;
                }
                return Formatting.DARK_GREEN;
            }
            case Aqua: {
                if (ModuleCommands.INSTANCE.firstWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.AQUA;
                }
                return Formatting.DARK_AQUA;
            }
            case Red: {
                if (ModuleCommands.INSTANCE.firstWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.RED;
                }
                return Formatting.DARK_RED;
            }
            case Yellow: {
                if (ModuleCommands.INSTANCE.firstWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.YELLOW;
                }
                return Formatting.GOLD;
            }
            case Purple: {
                if (ModuleCommands.INSTANCE.firstWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.LIGHT_PURPLE;
                }
                return Formatting.DARK_PURPLE;
            }
        }
        if (ModuleCommands.INSTANCE.firstWatermarkBrightness.getValue().equals(LightModes.Light)) {
            return Formatting.WHITE;
        }
        return Formatting.GRAY;
    }

    public static Formatting getSecondWatermarkColor() {
        switch ((ColorModes) ModuleCommands.INSTANCE.secondWatermarkColor.getValue()) {
            case Black: {
                if (ModuleCommands.INSTANCE.secondWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.DARK_GRAY;
                }
                return Formatting.BLACK;
            }
            case Gray: {
                if (ModuleCommands.INSTANCE.secondWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.GRAY;
                }
                return Formatting.DARK_GRAY;
            }
            case Blue: {
                if (ModuleCommands.INSTANCE.secondWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.BLUE;
                }
                return Formatting.DARK_BLUE;
            }
            case Green: {
                if (ModuleCommands.INSTANCE.secondWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.GREEN;
                }
                return Formatting.DARK_GREEN;
            }
            case Aqua: {
                if (ModuleCommands.INSTANCE.secondWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.AQUA;
                }
                return Formatting.DARK_AQUA;
            }
            case Red: {
                if (ModuleCommands.INSTANCE.secondWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.RED;
                }
                return Formatting.DARK_RED;
            }
            case Yellow: {
                if (ModuleCommands.INSTANCE.secondWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.YELLOW;
                }
                return Formatting.GOLD;
            }
            case Purple: {
                if (ModuleCommands.INSTANCE.secondWatermarkBrightness.getValue().equals(LightModes.Light)) {
                    return Formatting.LIGHT_PURPLE;
                }
                return Formatting.DARK_PURPLE;
            }
        }
        if (ModuleCommands.INSTANCE.secondWatermarkBrightness.getValue().equals(LightModes.Light)) {
            return Formatting.WHITE;
        }
        return Formatting.GRAY;
    }

    public enum LightModes {
        Light,
        Dark
    }

    public enum ColorModes {
        Gold,
        Black,
        Gray,
        Blue,
        Green,
        Aqua,
        Red,
        Yellow,
        White,
        Purple
    }

    public enum WatermarkModes {
        Normal,
        Japanese,
        Custom,
        None
    }
}
