package org.herbshouse.logic.enemies;

import org.herbshouse.logic.AbstractMovableObject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractEnemy extends AbstractMovableObject {
    private final List<ActionTimer> timers = new CopyOnWriteArrayList<>();

    public void registerTimer(ActionTimer timer){
        timers.add(timer);
    }

    public void checkTimers() {
        for (ActionTimer timer : timers) {
            if (timer.isExceeded()) {
                timer.afterEnd();
                timers.remove(timer);
            }
        }
    }

}
