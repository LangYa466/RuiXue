/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package me.tiangong;

public final class MSTimer2 {

    public long time = -1L;
    private long lastMS;

    public boolean hasTimePassed(final long MS) {
        return System.currentTimeMillis() >= time + MS;
    }
    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }
    public void resetTwo() {
        this.lastMS = this.getCurrentMS();
    }

    public long hasTimeLeft(final long MS) {
        return (MS + time) - System.currentTimeMillis();
    }
    public boolean hasReached(double milliseconds) {
        return (double)(this.getCurrentMS() - this.lastMS) >= milliseconds;
    }
    public void reset() {
        time = System.currentTimeMillis();
    }
}
