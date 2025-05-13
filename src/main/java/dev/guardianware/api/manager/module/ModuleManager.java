package dev.guardianware.api.manager.module;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.event.EventListener;
import dev.guardianware.api.utilities.IMinecraft;
import dev.guardianware.client.events.*;
import dev.guardianware.client.modules.client.*;
import dev.guardianware.client.modules.combat.*;
import dev.guardianware.client.modules.miscellaneous.FakePlayer;
import dev.guardianware.client.modules.miscellaneous.GodMode;
import dev.guardianware.client.modules.miscellaneous.HotbarReplenishModule;
import dev.guardianware.client.modules.miscellaneous.ModuleMiddleClick;
import dev.guardianware.client.modules.movement.*;
import dev.guardianware.client.modules.player.*;
import dev.guardianware.client.modules.visuals.*;
//import dev.loottech.client.modules.visuals.ModuleNameTags;
import dev.guardianware.client.values.Value;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class ModuleManager implements IMinecraft, EventListener {
    private final ArrayList<Module> modules;
    private final Map<Class<? extends Module>, Module> moduleInstances = new Reference2ReferenceOpenHashMap<>();

    public ModuleManager() {
        GuardianWare.EVENT_MANAGER.register(this);
        this.modules = new ArrayList<>();
        // write modules here

        //Client
        this.register(new ModuleColor());
        this.register(new ModuleCommands());
        this.register(new ModuleGUI());
        this.register(new ModuleHUD());
        //this.register(new ModuleHUDEditor());
        this.register(new ModuleMiddleClick());
        this.register(new ModuleParticles());
        this.register(new ModuleRotations());
        this.register(new FastLatency());
        this.register(new Font());

        //Combat
        this.register(new ModuleAutoArmor());
        this.register(new ModuleHoleFill());
        this.register(new ModuleOffhand());
        this.register(new ModulePopCounter());
        this.register(new ModuleSurround());
        this.register(new AutoCrystal());
        this.register(new AimBot());
        this.register(new KillAura());
        this.register(new AutoMineModule());

        //Miscellaneous
        this.register(new FakePlayer());
        this.register(new GodMode());
        this.register(new HotbarReplenishModule());

        //Movement
        this.register(new ModuleSprint());
        this.register(new ModuleVelocity());
        this.register(new NoSlow());
        this.register(new ModuleFlight());
        this.register(new ElytraFlight());
        this.register(new AutoWalk());

        //Player
        this.register(new ModuleMultiTask());
        this.register(new ChestSwap());
        this.register(new ScientifySexDupe());
        this.register(new PearlPhase());
        this.register(new Swing());

        //Visuals
        this.register(new ModuleCrosshair());
        this.register(new NoRender());
        this.register(new Fullbright());
        this.register(new DeathEffects());
        this.register(new PhaseESP());
        this.register(new VisualRange());
        this.register(new Nametags());
        this.register(new Chams());
        this.register(new ViewModel());
        this.register(new ESP());

        this.modules.sort(Comparator.comparing(Module::getName));
    }

    public void register(Module module) {
        try {
            for (Field field : module.getClass().getDeclaredFields()) {
                if (!Value.class.isAssignableFrom(field.getType())) continue;
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                module.getValues().add((Value)field.get(module));
            }
            module.getValues().add(module.tag);
            module.getValues().add(module.chatNotify);
            module.getValues().add(module.drawn);
            module.getValues().add(module.bind);
            this.modules.add(module);
            moduleInstances.put(module.getClass(), module);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Module> getModules() {
        return this.modules;
    }

    public ArrayList<Module> getModules(Module.Category category) {
        return (ArrayList<Module>) this.modules.stream().filter(mm -> mm.getCategory().equals(category)).collect(Collectors.toList());
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.modules.stream().filter(mm -> mm.getName().equals(name)).findFirst().orElse(null);
        if (module != null) {
            return module.isToggled();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getInstance(Class<T> klass) {
        return (T) moduleInstances.get(klass);
    }

    public Module getInstance(String name) {
        for (Module module : moduleInstances.values()) {
            if (module.name.equalsIgnoreCase(name)) return module;
        }

        return null;
    }

    @Override
    public void onTick(EventTick event) {
        if (mc.player != null && mc.world != null) {
            this.modules.stream().filter(Module::isToggled).forEach(Module::onTick);
        }
    }

    @Override
    public void onMotion(EventMotion event) {
        if (mc.player != null && mc.world != null) {
            this.modules.stream().filter(Module::isToggled).forEach(Module::onUpdate);
        }
    }

    @Override
    public void onRender2D(EventRender2D event) {
        this.modules.stream().filter(Module::isToggled).forEach(m -> m.onRender2D(event));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(1.0f);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.modules.stream().filter(Module::isToggled).forEach(mm -> mm.onRender3D(event));
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void onLogin(EventLogin event) {
        this.modules.stream().filter(Module::isToggled).forEach(Module::onLogin);
    }

    @Override
    public void onLogout(EventLogout event) {
        this.modules.stream().filter(Module::isToggled).forEach(Module::onLogout);
    }
}
