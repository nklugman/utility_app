package gridwatch.kplc;

import android.content.pm.ActivityInfo;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


import gridwatch.kplc.activities.ContactActivity;
import gridwatch.kplc.activities.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Arun on 5/8/17.
 */

@RunWith (AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RotationTest {
    public static final String SAMPLE_NAME = "JOHN DOE";
    public static final String SAMPLE_EMAIL = "GMAIL.COM";
    public static final String SAMPLE_MESSAGE = "HI";

    @Rule
    public ActivityTestRule<ContactActivity> activityRule = new ActivityTestRule<>(ContactActivity.class);

    @Test
    public void whenDeviceRotates_sameContactTextInputIsRetained() {
        // check to see that if you input an email, it stays the same when the screen is rotated
        onView(ViewMatchers.withId(R.id.editText3)).perform(typeText(SAMPLE_NAME));
        onView(ViewMatchers.withId(R.id.editText4)).perform(typeText(SAMPLE_EMAIL));
        onView(ViewMatchers.withId(R.id.feedbackbox)).perform(typeText(SAMPLE_MESSAGE));
        rotateDevice();
        onView(withId(R.id.editText3)).check(matches(withText(SAMPLE_NAME)));
        onView(withId(R.id.editText4)).check(matches(withText(SAMPLE_EMAIL)));
        onView(withId(R.id.feedbackbox)).check(matches(withText(SAMPLE_MESSAGE)));
    }

    private void rotateDevice() {
        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

}
