package edu.ucsd.cse110.mainpage.classes;

/**
 * This class is used to perform a speed calculation
 */


public class SpeedCalculator {

    public static float walkingSpeed(float distance, float timeinSecs) {
        if(timeinSecs<=0)
            return 0;
        else {
            float hour = timeinSecs / (float) 3600;
            float speed = distance / hour;
            return speed;
        }
    }

}
