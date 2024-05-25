package dev.redelegends.parkour.parkour.objects;

public class PlayerTimerCache {

    private final String name;
    private String checkPoint = "0";

    public PlayerTimerCache(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCheckPoint() {
        return checkPoint;
    }

    public void setCheckPoint(String checkPoint) {
        this.checkPoint = checkPoint;
    }
}
