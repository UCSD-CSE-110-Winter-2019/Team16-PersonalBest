package edu.ucsd.cse110.mainpage;
import edu.ucsd.cse110.mainpage.classes.DistanceCalculator;
import org.junit.Test;

import static org.junit.Assert.*;

public class stepsToDistanceTest {
    public DistanceCalculator distance = new DistanceCalculator();

    @Test
    public void stepsToDistanceTestFunc(){
        assertEquals(0.2248816192150116,distance.stepsToDistance(500, 69), 0 );
        assertEquals(0,distance.stepsToDistance(0, 0), 0 );
        assertEquals(0,distance.stepsToDistance(-100, -500), 0 );
    }
}
