package edu.ucsd.cse110.mainpage.classes;

/**
 * This class is used to maintain the time for a walk
 */

public class TimeCalculator {
    private long startTime;

    public TimeCalculator() {}

    public void startTimer() {
        this.startTime = System.currentTimeMillis();
    }

    public long getWalkTime() {
        return System.currentTimeMillis() - this.startTime;
    }

}
