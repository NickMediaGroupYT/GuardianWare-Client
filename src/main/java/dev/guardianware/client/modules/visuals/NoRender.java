package dev.guardianware.client.modules.visuals;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.values.impl.ValueBoolean;

@RegisterModule(name="NoRender", tag="NoRender", description="dont render some things.", category=Module.Category.VISUALS)
public class NoRender extends Module {
    public ValueBoolean arrows = new ValueBoolean("Arrows", "Arrows", "Dosen't render arrows.", true);
    public ValueBoolean weather = new ValueBoolean("Weather", "Weather", "Dosen't render weather.", true);
    public ValueBoolean explosions = new ValueBoolean("explosions", "Explosions", "Dosen't render explosions.", true);
    public ValueBoolean blockOverlay = new ValueBoolean("blockOverlay", "BlockOverlay", "Dosen't render block Overlay.", true);
    public ValueBoolean totemOverlay = new ValueBoolean("TotemOverlay", "TotemOverlay", "Dosen't render totem Overlay.", true);
    public ValueBoolean liquidOverlay = new ValueBoolean("LiquidOverlay", "LiquidOverlay", "Dosen't render liquid Overlays.", true);
    public ValueBoolean fireOverlay = new ValueBoolean("FireOverlay", "FireOverlay", "Dosen't render Fire Overlays.", true);
    public ValueBoolean bossBar = new ValueBoolean("BossBar", "BossBor", "Dosen't render boss bar.", true);
    public ValueBoolean statusOverlay = new ValueBoolean("StatusOverlay", "StatusOverlay", "Dosen't render status effects on HUD (potions on top right).", true);
}
