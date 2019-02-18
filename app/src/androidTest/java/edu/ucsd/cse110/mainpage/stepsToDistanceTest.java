package edu.ucsd.cse110.mainpage;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class stepsToDistanceTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void stepsToDistanceTestFunc(){
        assertEquals(mainActivity.getActivity().stepsToDistance(21), 0.008897489868104458, 0 );
    }
}
