package edu.ucsd.cse110.mainpage.AddFriend;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.ucsd.cse110.mainpage.MainActivity;
import edu.ucsd.cse110.mainpage.R;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddFriendsUITestInvalid {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void cancelAsyncTasks() {
        mActivityTestRule.getActivity().cancelStepAsyncTask();
    }

    @Test
    public void addFriendsUITestInvalid() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.viewFriendsBtn), withText("View Friends")));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.addFriendBtn), withText("+")));
        appCompatButton2.perform(click());

        ViewInteraction dialog = onView(withText("Add Friend!"));
        dialog.inRoot(isDialog()).check(matches(isDisplayed()));

        ViewInteraction text = onView(withId(R.id.friendEmailField));
        text.check(matches(isDisplayed()));

        text.perform(replaceText("bobby@notgmail"));

        ViewInteraction appCompatButton3 = onView(withId(R.id.addFriendDialogBtn));
        appCompatButton3.perform(click());

        onView(withText("Sorry we couldn't \nfind your friend!")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}
