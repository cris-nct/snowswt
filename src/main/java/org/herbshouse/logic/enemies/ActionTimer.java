package org.herbshouse.logic.enemies;

public abstract class ActionTimer{

    private final long startTime;

    private final int duration;

    public ActionTimer(int duration) {
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
    }

    abstract void afterEnd();

    public boolean isExceeded() {
        return (System.currentTimeMillis() - startTime) > duration;
    }

}
