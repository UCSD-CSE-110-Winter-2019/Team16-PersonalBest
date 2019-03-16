package edu.ucsd.cse110.mainpage;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.NumberPicker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.ucsd.cse110.mainpage.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EnterHeightTest {

    private int feetTestValue = 5;
    private int inchesTestValue = 7;
    private int finalInchesTestValue = 67;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void cancelAsyncTasks() {
        mActivityTestRule.getActivity().cancelStepAsyncTask();
    }

    @Before
    public void enterHeight() {
        Intent promptHeightIntent = new Intent(mActivityTestRule.getActivity(), EnterHeightActivity.class);
        mActivityTestRule.getActivity().startActivityForResult(promptHeightIntent, 0);
    }

    @Test
    public void enterHeightTest() {

        onView(withId(R.id.feetPicker)).perform(new ViewAction() {
            @Override
            public Matcher getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);
            }

            @Override
            public String getDescription() {
                return "Set the value of the feet NumberPicker";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((NumberPicker)view).setValue(feetTestValue);
            }
        });

        onView(withId(R.id.inchesPicker)).perform(new ViewAction() {
            @Override
            public Matcher getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);
            }

            @Override
            public String getDescription() {
                return "Set the value of the inches NumberPicker";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((NumberPicker)view).setValue(inchesTestValue);
            }
        });

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.confirmButton), withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatButton.perform(click());

        SharedPreferences userSharedPref = mActivityTestRule.getActivity().getSharedPreferences("userdata", mActivityTestRule.getActivity().MODE_PRIVATE);
        int height = userSharedPref.getInt("height",-1);
        Assert.assertEquals(height, finalInchesTestValue);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
