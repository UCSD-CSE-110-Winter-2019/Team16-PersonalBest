package edu.ucsd.cse110.mainpage;
import edu.ucsd.cse110.mainpage.classes.StepCounter;
import org.junit.Test;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
public class stepCounterUnitTest {
    public StepCounter stepcounter = new StepCounter();

    @Test
    public void stepsToDistanceTestFunc(){
        stepcounter.startSteps(5631);
        assertEquals(754,stepcounter.getSteps(6385), 0 );
        assertEquals(0,stepcounter.getSteps(-159), 0 );
    }
}
