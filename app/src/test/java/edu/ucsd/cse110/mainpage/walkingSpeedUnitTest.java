package edu.ucsd.cse110.mainpage;
import edu.ucsd.cse110.mainpage.classes.SpeedCalculator;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class walkingSpeedUnitTest {
    public SpeedCalculator sp = new SpeedCalculator();

    @Test
    public void walkingSpeedUnitTest(){
        assertEquals(3,sp.walkingSpeed(3,3600),0);
        assertEquals(0,sp.walkingSpeed(0,0),0);
        assertEquals(0,sp.walkingSpeed(100,0),0);
    }
}
