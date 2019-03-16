package edu.ucsd.cse110.mainpage;
import edu.ucsd.cse110.mainpage.classes.TimeCalculator;
import org.junit.Test;

import java.time.LocalDateTime;

import static android.os.SystemClock.sleep;
import static org.junit.Assert.assertEquals;



public class timeCalculatorMockingTest {
    public TimeCalculator time = new TimeCalculator();

    @Test
    public void stepsToDistanceTestFunc(){
        long dummyStartTime = System.currentTimeMillis();
        time.startTimer();
        sleep(5000);
        long dummyEndTime = System.currentTimeMillis();
        assertEquals(time.getWalkTime(),dummyEndTime-dummyStartTime, 0 );
    }
}