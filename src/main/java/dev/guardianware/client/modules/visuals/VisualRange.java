package dev.guardianware.client.modules.visuals;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.client.values.impl.ValueBoolean;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RegisterModule(name="VisualRange", tag="VisualRange", description="", category=Module.Category.VISUALS)
public class VisualRange extends Module {
    private final ValueBoolean displayPosition = new ValueBoolean("DisplayPosition", "DisplayPosition", "", true);
    private final ValueBoolean notify = new ValueBoolean("Notify", "Notify", "", true);
    private final ValueBoolean sound = new ValueBoolean("Sound", "Sound", "", true);

    List<PlayerEntity> players = new CopyOnWriteArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();
        this.players.clear();
        if (mc.player == null || mc.world == null) return;

        // Add existing players to the list
        for (PlayerEntity entity : mc.world.getPlayers()) {
            if (entity != mc.player) {
                this.players.add(entity);
            }
        }
    }

    @Override
    public void onUpdate() {
        List<AbstractClientPlayerEntity> currentPlayers = mc.world.getPlayers();

        // Check for new players entering the visual range
        for (PlayerEntity entity : currentPlayers) {
            if (!this.players.contains(entity) && !entity.equals(mc.player)) {
                // If the displayPosition setting is true, fetch the player's coordinates
                String message = "[VisualRange] "+ Formatting.AQUA+entity.getDisplayName().getString() +Formatting.GRAY+ " entered visual range";

                if ((Boolean) this.displayPosition.getValue()) {
                    // Round the coordinates to integers
                    int x = (int) Math.round(entity.getX());
                    int y = (int) Math.round(entity.getY());
                    int z = (int) Math.round(entity.getZ());

                    // Format the message with the rounded coordinates
                    String coordinates = String.format("[%d, %d, %d]", x, y, z);
                    message += " at " + coordinates;
                }
                if (notify.getValue()) {
                    ChatUtils.sendMessage(message);
                }
                this.players.add(entity);
                if (sound.getValue()) {
                    mc.player.getWorld().playSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP , mc.player.getSoundCategory(), 1, 3, false);
                }}
        }

        // Check for players leaving the visual range
        for (PlayerEntity entity : this.players) {
            if (!currentPlayers.contains(entity) && !entity.equals(mc.player)) {
                // If the displayPosition setting is true, fetch the player's coordinates
                String message = "[VisualRange] "+Formatting.AQUA+entity.getDisplayName().getString() + Formatting.GRAY+ " left visual range";

                if ((Boolean) this.displayPosition.getValue()) {
                    // Round the coordinates to integers
                    int x = (int) Math.round(entity.getX());
                    int y = (int) Math.round(entity.getY());
                    int z = (int) Math.round(entity.getZ());

                    // Format the message with the rounded coordinates
                    String coordinates = String.format("[%d, %d, %d]", x, y, z);
                    message += " at " + coordinates;
                }
                if (notify.getValue()) {
                    ChatUtils.sendMessage(message);
                }
                this.players.remove(entity);
            }
        }
    }

}
