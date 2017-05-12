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

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Arun on 5/8/17.
 */

@RunWith (AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContactTest {
    public static final String SAMPLE_NAME = "JOHN DOE";
    public static final String SAMPLE_EMAIL = "GMAIL.COM";
    public static final String SAMPLE_MESSAGE = "HI";

    @Rule
    public ActivityTestRule<ContactActivity> activityRule = new ActivityTestRule<>(ContactActivity.class);

    @Test
    public void AtestNameText() {
        // check that if you enter a name, it appears in the name box
        onView(withId(R.id.editText3)).perform(typeText(SAMPLE_NAME));
        // example confirming this name has been successfully typed
        onView(withText(SAMPLE_NAME)).check(matches(isDisplayed()));
        closeSoftKeyboard();
    }

    @Test
    public void BtestEmailText() {
        // check that if you enter an email address, it appears in the email box
        onView(withId(R.id.editText4)).perform(typeText(SAMPLE_EMAIL));
        // example confirming this email has been successfully typed
        onView(withText(SAMPLE_EMAIL)).check(matches(isDisplayed()));
        closeSoftKeyboard();
    }

    @Test
    public void CtestMessageText() {
        // check that if you enter a message pin, it appears in the message box
        onView(withId(R.id.feedbackbox)).perform(typeText(SAMPLE_MESSAGE));
        // example confirming this message has been successfully typed
        onView(withText(SAMPLE_MESSAGE)).check(matches(isDisplayed()));
        closeSoftKeyboard();
    }

    @Test
    public void DtestNameText_thenClear() {
        // check that if you enter a name, it appears in the name box, then can be cleared
        onView(withId(R.id.editText3)).perform(typeText(SAMPLE_NAME));
        // example confirming this name has been successfully typed
        onView(withText(SAMPLE_NAME)).check(matches(isDisplayed()));
        // clear the text
        onView(withId(R.id.editText3)).perform(clearText());
        // check if field is empty
        onView(withId(R.id.editText3)).check(matches(withText("")));
        closeSoftKeyboard();
    }

    @Test
    public void EtestEmailText_thenClear() {
        // check that if you enter an email address, it appears in the email box, then can be cleared
        onView(withId(R.id.editText4)).perform(typeText(SAMPLE_EMAIL));
        // example confirming this email has been successfully typed
        onView(withText(SAMPLE_EMAIL)).check(matches(isDisplayed()));
        // clear the text
        onView(withId(R.id.editText4)).perform(clearText());
        // check if field is empty
        onView(withId(R.id.editText4)).check(matches(withText("")));
        closeSoftKeyboard();
    }

    @Test
    public void FtestMessageText_thenClear() {
        // check that if you enter a message pin, it appears in the message box, then can be cleared
        onView(withId(R.id.feedbackbox)).perform(typeText(SAMPLE_MESSAGE));
        // example confirming this message has been successfully typed
        onView(withText(SAMPLE_MESSAGE)).check(matches(isDisplayed()));
        // clear the text
        onView(withId(R.id.feedbackbox)).perform(clearText());
        // check if field is empty
        onView(withId(R.id.feedbackbox)).check(matches(withText("")));
        closeSoftKeyboard();
    }

    @Test
    public void GtestNameText_thenReplace() {
        // check that if you enter a name, it appears in the name box, then can be replaced
        onView(withId(R.id.editText3)).perform(typeText(SAMPLE_MESSAGE));
        // example confirming this name has been successfully typed
        onView(withText(SAMPLE_MESSAGE)).check(matches(isDisplayed()));
        // replace the text
        onView(withId(R.id.editText3)).perform(replaceText(SAMPLE_NAME));
        // check if text has been replaced
        onView(allOf(withId(R.id.editText3), withText(SAMPLE_NAME))).check(matches(isDisplayed()));
        closeSoftKeyboard();
    }

    @Test
    public void HtestEmailText_thenReplace() {
        // check that if you enter an email address, it appears in the email box, then can be replaced
        onView(withId(R.id.editText4)).perform(typeText(SAMPLE_MESSAGE));
        // example confirming this email has been successfully typed
        onView(withText(SAMPLE_MESSAGE)).check(matches(isDisplayed()));
        // replace the text
        onView(withId(R.id.editText4)).perform(replaceText(SAMPLE_EMAIL));
        // check if text has been replaced
        onView(allOf(withId(R.id.editText4), withText(SAMPLE_EMAIL))).check(matches(isDisplayed()));
        closeSoftKeyboard();
    }

    @Test
    public void ItestMessageText_thenReplace() {
        // check that if you enter a message pin, it appears in the message box, then can be replaced
        onView(withId(R.id.feedbackbox)).perform(typeText(SAMPLE_NAME));
        // example confirming this message has been successfully typed
        onView(withText(SAMPLE_NAME)).check(matches(isDisplayed()));
        // replace the text
        onView(withId(R.id.feedbackbox)).perform(replaceText(SAMPLE_MESSAGE));
        // check if text has been replaced
        onView(allOf(withId(R.id.feedbackbox), withText(SAMPLE_MESSAGE))).check(matches(isDisplayed()));
        closeSoftKeyboard();
    }

    @Test
    public void JwhenDeviceRotates_sameContactTextInputIsRetained() {
        // check to see that if you input information, it stays the same when the screen is rotated
        onView(ViewMatchers.withId(R.id.editText3)).perform(typeText(SAMPLE_NAME));
        onView(ViewMatchers.withId(R.id.editText4)).perform(typeText(SAMPLE_EMAIL));
        onView(ViewMatchers.withId(R.id.feedbackbox)).perform(typeText(SAMPLE_MESSAGE));
        rotateDevice();
        onView(withId(R.id.editText3)).check(matches(withText(SAMPLE_NAME)));
        onView(withId(R.id.editText4)).check(matches(withText(SAMPLE_EMAIL)));
        onView(withId(R.id.feedbackbox)).check(matches(withText(SAMPLE_MESSAGE)));
        closeSoftKeyboard();
    }

    private void rotateDevice() {
        // rotates the device
        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

}
