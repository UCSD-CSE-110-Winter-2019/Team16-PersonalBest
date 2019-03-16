package edu.ucsd.cse110.mainpage.classes;

/**
 * This class is used to maintain the steps for a walk
 */
public class StepCounter {
    private long currentsteps;

    public StepCounter() {
    }

    public void startSteps(long steps) {
        this.currentsteps = steps;
    }

    public long getSteps(long endSteps) {
        if(this.currentsteps<=endSteps)
            return endSteps - this.currentsteps;
        else
            return 0;
    }

}
