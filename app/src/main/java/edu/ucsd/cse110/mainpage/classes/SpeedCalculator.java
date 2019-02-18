package edu.ucsd.cse110.mainpage.classes;

/**
 * This class is used to perform a speed calculation
 */


public class SpeedCalculator {

    public static float walkingSpeed(float distance, float timeinSecs) {
        float hour = timeinSecs / (float)3600;
        float speed = distance / hour;
        return speed;
    }

}
