package edu.ucsd.cse110.mainpage;
import android.os.Handler;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class setSpeedTextViewUnitTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);
    @Test
    public void setDistanceTextViewUnitTest() {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                TextView speedText = (TextView) mainActivity.getActivity().findViewById(R.id.walkingSpeed);
                mainActivity.getActivity().setSpeedTextView(12);
                assertEquals("12.0 mph",speedText.getText().toString());
            }
        });
    }
}
