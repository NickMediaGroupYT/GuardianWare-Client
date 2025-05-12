package dev.guardianware.api.utilities;

public class TimerUtils {
    public long lastMilliseconds = System.currentTimeMillis();

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - this.lastMilliseconds > time;
    }

    public long timeElapsed() {
        return System.currentTimeMillis() - this.lastMilliseconds;
    }

    public void reset() {
        this.lastMilliseconds = System.currentTimeMillis();
    }
}
