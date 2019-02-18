package edu.ucsd.cse110.mainpage;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class stepsToDistanceTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void walkingSpeedUnitTest(){
        //assertEquals(mainActivity.getActivity().stepsToDistance(),,0);
    }
}
