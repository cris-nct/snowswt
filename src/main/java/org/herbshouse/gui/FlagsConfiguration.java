package org.herbshouse.gui;

import org.eclipse.swt.graphics.Transform;
import org.herbshouse.logic.Point2D;

public class FlagsConfiguration {
    public static final int DESIRED_FPS = 60;

    public static final int SLEEP_SNOWFLAKE_GENERATOR = 5;
    public static final int SLEEP_ENEMY_GENERATOR = 2;
    public static final int SLEEP_GENERATOR_DOING_NOTHING = 100;

    private boolean normalWind;
    private boolean happyWind;
    private boolean debug;
    private boolean attack;
    private int attackType = 1;
    private boolean flipImage;
    private boolean bigBalls;
    private boolean freezeSnowflakes;
    private boolean mercedesSnowflakes;
    private boolean enemies = false;
    private boolean youtube;

    private int snowingLevel = 5;
    private Point2D mouseLoc = new Point2D();
    private Transform transform;

    void increaseSnowingLevel() {
        this.snowingLevel++;
    }

    void decreaseSnowingLevel() {
        this.snowingLevel--;
    }

    void switchMercedesSnowflakes() {
        this.mercedesSnowflakes = !mercedesSnowflakes;
    }

    void switchFreezeSnowflakes() {
        this.freezeSnowflakes = !freezeSnowflakes;
    }

    void switchYoutube() {
        this.youtube = !youtube;
    }

    void switchBigBalls() {
        this.bigBalls = !bigBalls;
    }

    void switchAttack() {
        this.attack = !attack;
    }

    void switchEnemies() {
        this.enemies = !enemies;
    }

    void switchDebug() {
        this.debug = !debug;
    }

    void switchFlipImage() {
        this.flipImage = !flipImage;
    }

    public Transform getTransform() {
        return transform;
    }

    void setTransform(Transform transform) {
        this.transform = transform;
    }

    void switchNormalWind() {
        this.normalWind = !normalWind;
    }

    void switchHappyWind() {
        this.happyWind = !happyWind;
    }

    public boolean isYoutube() {
        return youtube;
    }

    public int getAttackType() {
        return attackType;
    }

    void setAttackType(int i) {
        this.attackType = i;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isMercedesSnowflakes() {
        return mercedesSnowflakes;
    }

    public boolean isFlipImage() {
        return flipImage;
    }

    public boolean isHappyWind() {
        return happyWind;
    }

    public boolean isNormalWind() {
        return normalWind;
    }

    public boolean isBigBalls() {
        return bigBalls;
    }

    public boolean isFreezeSnowflakes() {
        return freezeSnowflakes;
    }

    public int getSnowingLevel() {
        return snowingLevel;
    }

    public void setMouseCurrentLocation(Point2D mouseLoc) {
        this.mouseLoc = mouseLoc;
    }

    public Point2D getMouseLoc() {
        return mouseLoc;
    }

    public boolean isEnemies() {
        return enemies;
    }

    public boolean isAttack() {
        return attack;
    }
}
