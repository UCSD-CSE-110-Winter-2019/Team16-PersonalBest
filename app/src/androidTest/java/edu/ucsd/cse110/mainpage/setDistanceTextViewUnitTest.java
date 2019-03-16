package edu.ucsd.cse110.mainpage;
import android.os.Handler;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
public class setDistanceTextViewUnitTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);
    @Test
    public void setDistanceTextViewUnitTest() {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                TextView dist = (TextView) mainActivity.getActivity().findViewById(R.id.distanceTView);
                mainActivity.getActivity().setDistanceTextView(323);
                assertEquals("Dist: 323.0 miles",dist.getText().toString());
            }
        });
    }
}
