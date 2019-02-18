package edu.ucsd.cse110.mainpage.classes;

public class StepCounter {
    private long currentsteps;

    public StepCounter() {
    }

    public void startSteps(long steps) {
        this.currentsteps = steps;
    }

    public long getSteps(long endSteps) {
        return endSteps - this.currentsteps;
    }

}
