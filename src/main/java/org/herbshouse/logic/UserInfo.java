package org.herbshouse.logic;

public class UserInfo {
    private int points;

    public int getPoints() {
        return points;
    }

    public void increasePoints(int points) {
        this.points += points;
    }

    public void decreasePoints(int points) {
        this.points -= points;
    }

}
