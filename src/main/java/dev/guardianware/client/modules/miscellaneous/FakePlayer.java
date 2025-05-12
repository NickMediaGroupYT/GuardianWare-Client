package dev.guardianware.client.modules.miscellaneous;

import com.google.common.eventbus.Subscribe;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.entity.FakePlayerEntity;
import dev.guardianware.client.events.EventLogout;

@RegisterModule(name="FakePlayer", tag="FakePlayer", description="Summon lightning at player deaths.", category= Module.Category.MISCELLANEOUS)
public class FakePlayer extends Module {
    private FakePlayerEntity fakePlayer;

    public void onEnable() {
        if (mc.player != null && mc.world != null) {
            this.fakePlayer = new FakePlayerEntity(mc.player, "FakePlayer");
            this.fakePlayer.spawnPlayer();
        }

    }

    public void onDisable() {
        if (this.fakePlayer != null) {
            this.fakePlayer.despawnPlayer();
            this.fakePlayer = null;
        }

    }

    @Subscribe
    public void onLogout(EventLogout event) {
        this.fakePlayer = null;
        this.toggle(false);
    }

}
