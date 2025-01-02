package org.herbshouse.gui;

public class FlagsConfiguration {
    private boolean normalWind;
    private boolean happyWind;
    private boolean debug;
    private boolean attack;
    private boolean flipImage;
    private boolean bigBalls;
    private boolean freezeSnowflakes;
    private boolean mercedesSnowflakes;
    private int snowingLevel = 1;
    private int mouseLocX;
    private int mouseLocY;

    void increaseSnowingLevel() {
        this.snowingLevel++;
    }

    void decreaseSnowingLevel() {
        this.snowingLevel--;
    }

    void switchMercedesSnowflakes(){
        this.mercedesSnowflakes=!mercedesSnowflakes;
    }

    void switchFreezeSnowflakes() {
        this.freezeSnowflakes = !freezeSnowflakes;
    }

    void switchBigBalls() {
        this.bigBalls = !bigBalls;
    }

    void switchAttack() {
        this.attack = !attack;
    }

    void switchDebug() {
        this.debug = !debug;
    }

    void switchFlipImage() {
        this.flipImage = !flipImage;
    }

    void switchNormalWind() {
        this.normalWind = !normalWind;
    }

    void switchHappyWind() {
        this.happyWind = !happyWind;
    }

    public boolean isAttack() {
        return attack;
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

    public void setMouseCurrentLocation(int mouselocX, int mouseLocY) {
        this.mouseLocX = mouselocX;
        this.mouseLocY = mouseLocY;
    }

    public int getMouseLocX() {
        return mouseLocX;
    }

    public int getMouseLocY() {
        return mouseLocY;
    }
}
