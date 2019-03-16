package edu.ucsd.cse110.mainpage;

import android.os.Handler;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class setStepUnitTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);
    @Test
    public void setStepUnitTest() {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                TextView step = (TextView) mainActivity.getActivity().findViewById(R.id.stepsView);
                mainActivity.getActivity().setStepCount(599);
                assertEquals("599 Steps",step.getText().toString());
            }
        });
    }
}
