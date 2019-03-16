package edu.ucsd.cse110.mainpage;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.ucsd.cse110.mainpage.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SetGoalUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    public int goalToSet = 500;

    @Before
    public void cancelAsyncTasks() {
        mActivityTestRule.getActivity().cancelStepAsyncTask();
    }

    @Test
    public void setGoalUITest() {
        ViewInteraction editText = onView(
                allOf(withId(R.id.currGoal)));
        editText.perform(scrollTo(), replaceText( "" + goalToSet ));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.goalBtn), withText("Set Goal")));
        appCompatButton.perform(scrollTo(), click());

        onView(withText("Your new daily steps goal is " + goalToSet)).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}
