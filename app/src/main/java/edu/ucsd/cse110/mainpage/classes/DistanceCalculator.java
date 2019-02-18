package edu.ucsd.cse110.mainpage.classes;

public class DistanceCalculator {

    public static float stepsToDistance(long steps, int height) {
        float strideLength = 0;
        if (height != -1) {
            strideLength = (float) (height * 0.413);
        }
        float feetPerStride = strideLength/12;
        float stepsPerMile = 5280/feetPerStride;
        float totalDistanceMiles = steps/stepsPerMile;
        return totalDistanceMiles;
    }


}
