package edu.ucsd.cse110.mainpage;
import android.os.Handler;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class updatePersonalBestTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);
    @Test
    public void updatePersonalBestTest() {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                TextView bestText = (TextView) mainActivity.getActivity().findViewById(R.id.personalBest);
                mainActivity.getActivity().setStepCount(2130);
                mainActivity.getActivity().updatePersonalBest();
                assertEquals("Best: 2130 Steps",bestText.getText().toString());
            }
        });
    }
}
