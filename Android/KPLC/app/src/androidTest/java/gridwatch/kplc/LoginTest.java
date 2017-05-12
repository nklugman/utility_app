package gridwatch.kplc;

/**
 * Created by Arun on 5/8/17.
 */
import android.content.pm.ActivityInfo;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.core.AllOf;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import gridwatch.kplc.activities.LoginActivity;


import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.hasContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import org.junit.runners.MethodSorters;

import org.junit.FixMethodOrder;
import org.junit.Test;


@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginTest { // you must be logged out for these tests to work

    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void AclickPrePayButton() {
        // check if clicking the PrePay Button chooses PrePay and does not choose PostPay
        onView(withId(R.id.loginPrePayRadio)).perform(click());
        onView(withId(R.id.loginPrePayRadio)).check(matches(isChecked()));
        onView(withId(R.id.loginPostPayRadio)).check(matches(not(isChecked())));
    }

    @Test
    public void BclickPostPayButton() {
        // check if clicking the PostPay Button chooses PostPay and does not choose PrePay
        onView(withId(R.id.loginPostPayRadio)).perform(click());
        onView(withId(R.id.loginPrePayRadio)).check(matches(not(isChecked())));
        onView(withId(R.id.loginPostPayRadio)).check(matches(isChecked()));
    }

    @Test
    public void CtestPrePayText() {
        // check that if you enter a meter number, it appears in the meter number box under PrePay
        String exampleNumber = "1111";
        onView(withId(R.id.loginPrePayRadio)).perform(click());
        onView(withId(R.id.loginPrePayEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
    }

    @Test
    public void DtestPostPayText() {
        // check that if you enter an account number, it appears in the account number box under PostPay
        String exampleNumber = "2222";
        onView(withId(R.id.loginPostPayRadio)).perform(click());
        onView(withId(R.id.loginPostPayEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
    }

    @Test
    public void EtestPrePayText_thenClear() {
        // check that if you enter a meter number, it appears in the meter number box under PrePay
        String exampleNumber = "1111";
        onView(withId(R.id.loginPrePayRadio)).perform(click());
        onView(withId(R.id.loginPrePayEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
        // clear the text
        onView(withId(R.id.loginPrePayEditText)).perform(clearText());
        // check if field is empty
        onView(withId(R.id.loginPrePayEditText)).check(matches(withText("")));
    }

    @Test
    public void FtestPostPayText_thenClear() {
        // check that if you enter an account number, it appears in the account number box under PostPay
        String exampleNumber = "2222";
        onView(withId(R.id.loginPostPayRadio)).perform(click());
        onView(withId(R.id.loginPostPayEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
        // clear the text
        onView(withId(R.id.loginPostPayEditText)).perform(clearText());
        // check if field is empty
        onView(withId(R.id.loginPostPayEditText)).check(matches(withText("")));
    }

    @Test
    public void GtestPrePayText_thenReplace() {
        // check that if you enter a meter number then change it, it changes in the box
        String exampleNumber = "1111";
        String replaceNumber = "3333";
        onView(withId(R.id.loginPrePayRadio)).perform(click());
        onView(withId(R.id.loginPrePayEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
        // replace the text
        onView(withId(R.id.loginPrePayEditText)).perform(replaceText(replaceNumber));
        // check if text has been replaced
        onView(allOf(withId(R.id.loginPrePayEditText), withText(replaceNumber))).check(matches(isDisplayed()));
    }

    @Test
    public void HtestPostPayText_thenReplace() {
        // check that if you enter an account number then change it, it changes in the box
        String exampleNumber = "2222";
        String replaceNumber = "4444";
        onView(withId(R.id.loginPostPayRadio)).perform(click());
        onView(withId(R.id.loginPostPayEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
        // replace the text
        onView(withId(R.id.loginPostPayEditText)).perform(replaceText(replaceNumber));
        // check if text has been replaced
        onView(allOf(withId(R.id.loginPostPayEditText), withText(replaceNumber))).check(matches(isDisplayed()));
    }

    @Test
    public void IwhenDeviceRotates_sameLoginTextInputIsRetained() {
        // check that if you enter a meter number, it appears in the meter number box under PrePay
        String exampleNumber = "1111";
        onView(withId(R.id.loginPrePayRadio)).perform(click());
        onView(withId(R.id.loginPrePayEditText)).perform(typeText(exampleNumber));
        rotateDevice();
        onView(withId(R.id.loginPrePayEditText)).check(matches(withText(exampleNumber)));
    }

    @Test
    public void JtestPrePayText_CloseSoftKeyboard() {
        // test to open pre pay edit box then close the keyboard
        onView(withId(R.id.loginPrePayRadio)).perform(click());
        onView(withId(R.id.loginPrePayEditText)).perform(click());
        closeSoftKeyboard();
    }

    @Test
    public void KtestPostPayText_CloseSoftKeyboard() {
        // test to open post pay edit box then close the keyboard
        onView(withId(R.id.loginPostPayRadio)).perform(click());
        onView(withId(R.id.loginPostPayEditText)).perform(click());
        closeSoftKeyboard();
    }

    // only 1 of the following 2 tests can work at a time because they require a logout in between them

    @Test
    public void LtestSkipLogin() {
        // test to click Skip button and see if dialog box pops up
        onView(withId(R.id.skip_btn)).perform(click());
        onView(withText(R.string.skip_warning)).inRoot(isDialog()).check(matches(isDisplayed()));

        // test to go back to the login page
        onView(withId(android.R.id.button2)).perform(click());
        onView(withId(R.id.loginPrePayRadio)).perform(click());
        onView(withId(R.id.loginPrePayRadio)).check(matches(isChecked()));

        // click the skip button again
        onView(withId(R.id.skip_btn)).perform(click());
        onView(withText(R.string.skip_warning)).inRoot(isDialog()).check(matches(isDisplayed()));

        // click ok and go to the home page
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.welcome)).check(matches(AllOf.allOf(isDescendantOfA(withId(R.id.content_home)), isDisplayed())));
    }

    @Test
    public void MtestSubmit() {
        // test to open pre pay edit box then close the keyboard
        String exampleNumber = "1111";
        onView(withId(R.id.loginPrePayRadio)).perform(click());
        onView(withId(R.id.loginPrePayEditText)).perform(typeText(exampleNumber));
        closeSoftKeyboard();
        onView(withId(R.id.login_submit)).perform(click());
        onView(withId(R.id.welcome)).check(matches(AllOf.allOf(isDescendantOfA(withId(R.id.content_home)), isDisplayed())));
    }

    private void rotateDevice() {
        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}
