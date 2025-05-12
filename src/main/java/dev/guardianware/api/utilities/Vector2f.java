package dev.guardianware.api.utilities;

public class Vector2f {
    public float x;
    public float y;

    public void setY(float y) {
        this.y = y;
    }

    public float getY() {
        return this.y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getX() {
        return this.x;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }
}