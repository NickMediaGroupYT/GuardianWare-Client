package dev.guardianware.api.manager.event;

import dev.guardianware.client.events.*;

public interface EventListener {
    // write listeners here
    default void onChatSend(EventChatSend event) {
    }

    default void onClient(EventClient event) {
    }

    default void onKey(EventKey event) {
    }

    default void onLogin(EventLogin event) {
    }

    default void onLogout(EventLogout event) {
    }

    default void onMotion(EventMotion event) {
    }

    default void onPacketSend(EventPacketSend event) {
    }

    default void onPacketReceive(EventPacketReceive event) {
    }

    default void onPush(EventPush event) {
    }

    default void onRender2D(EventRender2D event) {
    }

    default void onRender3D(Render3DEvent event) {
    }

    default void onTick(EventTick event) {

    }
    default void onDeath(EventDeath event) {

    }
    default void onAttackBlock(EventAttackBlock event) {

    }
}
