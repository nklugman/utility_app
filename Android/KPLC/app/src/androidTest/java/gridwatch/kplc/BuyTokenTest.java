package gridwatch.kplc;

import android.content.pm.ActivityInfo;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.core.AllOf;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import gridwatch.kplc.activities.LoginActivity;
import gridwatch.kplc.activities.payment.BuyTokensActivity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Arun on 5/12/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BuyTokenTest {
    @Rule
    public ActivityTestRule<BuyTokensActivity> activityRule = new ActivityTestRule<>(BuyTokensActivity.class);

    @Test
    public void AtestAmountText() {
        // check that if you enter a Ksh amount, it appears in the amount box
        String exampleNumber = "1111";
        onView(withId(R.id.buyTokenPurchaseEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
        closeSoftKeyboard();
    }

    @Test
    public void BtestMPESAText() {
        // check that if you enter an MPESA pin, it appears in the pin box
        String exampleNumber = "2222";
        onView(withId(R.id.buyTokenPinEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
        closeSoftKeyboard();
    }

    @Test
    public void CtestAmountText_thenClear() {
        // check that if you enter a Ksh amount, it appears in the amount box, then can be cleared
        String exampleNumber = "1111";
        onView(withId(R.id.buyTokenPurchaseEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
        // clear the text
        onView(withId(R.id.buyTokenPurchaseEditText)).perform(clearText());
        // check if field is empty
        onView(withId(R.id.buyTokenPurchaseEditText)).check(matches(withText("")));
        closeSoftKeyboard();
    }

    @Test
    public void DtestMPESAText_thenClear() {
        // check that if you enter an MPESA pin, it appears in the pin box, then can be cleared
        String exampleNumber = "2222";
        onView(withId(R.id.buyTokenPinEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
        // clear the text
        onView(withId(R.id.buyTokenPinEditText)).perform(clearText());
        // check if field is empty
        onView(withId(R.id.buyTokenPinEditText)).check(matches(withText("")));
        closeSoftKeyboard();
    }

    @Test
    public void EtestAmountText_thenReplace() {
        // check that if you enter a Ksh amount, it appears in the amount box, then can be replaced
        String exampleNumber = "1111";
        String replaceNumber = "3333";
        onView(withId(R.id.buyTokenPurchaseEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
        // replace the text
        onView(withId(R.id.buyTokenPurchaseEditText)).perform(replaceText(replaceNumber));
        // check if text has been replaced
        onView(allOf(withId(R.id.buyTokenPurchaseEditText), withText(replaceNumber))).check(matches(isDisplayed()));
        closeSoftKeyboard();
    }

    @Test
    public void FtestMPESAText_thenReplace() {
        // check that if you enter an MPESA pin, it appears in the pin box, then can be replaced
        String exampleNumber = "2222";
        String replaceNumber = "4444";
        onView(withId(R.id.buyTokenPinEditText)).perform(typeText(exampleNumber));
        // example confirming this number has been successfully typed
        onView(withText(exampleNumber)).check(matches(isDisplayed()));
        // replace the text
        onView(withId(R.id.buyTokenPinEditText)).perform(replaceText(replaceNumber));
        // check if text has been replaced
        onView(allOf(withId(R.id.buyTokenPinEditText), withText(replaceNumber))).check(matches(isDisplayed()));
        closeSoftKeyboard();
    }

    @Test
    public void GwhenDeviceRotates_sameLoginTextInputIsRetained() {
        // check that if you enter text in the 2 fields, the text remains when the device is rotated
        String exampleAmount = "1111";
        String examplePin = "2222";
        onView(withId(R.id.buyTokenPurchaseEditText)).perform(typeText(exampleAmount));
        onView(withId(R.id.loginPrePayEditText)).perform(typeText(examplePin));
        rotateDevice();
        onView(withId(R.id.buyTokenPinEditText)).check(matches(withText(exampleAmount)));
        onView(withId(R.id.buyTokenPinEditText)).check(matches(withText(examplePin)));
        closeSoftKeyboard();
    }

    @Test
    public void HtestSubmit() {
        // test to click confirm and see if you end up back on the buy tokens page
        String exampleAmount = "1111";
        String examplePin = "2222";
        onView(withId(R.id.buyTokenPurchaseEditText)).perform(typeText(exampleAmount));
        onView(withId(R.id.loginPrePayEditText)).perform(typeText(examplePin));
        closeSoftKeyboard();
        onView(withId(R.id.buyTokenConfirmButton)).perform(click());
        onView(withId(R.id.buyTokenPurchaseEditText)).check(matches(AllOf.allOf(isDescendantOfA(withId(R.id.activity_tokens)), isDisplayed())));
    }

    private void rotateDevice() {
        // rotates the device
        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}
