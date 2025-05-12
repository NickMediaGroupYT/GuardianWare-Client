package dev.guardianware.api.manager.rotation;

public class Rotation {
    private final int priority;
    private float yaw, pitch;
    private boolean grim;

    public Rotation(int priority, float yaw, float pitch, boolean grim)
    {
        this.priority = priority;
        this.yaw = yaw;
        this.pitch = pitch;
        this.grim = grim;
    }

    public Rotation(int priority, float yaw, float pitch) {
        this(priority, yaw, pitch,false);
    }

    public int getPriority() {
        return priority;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isGrim() {
        return grim;
    }

}