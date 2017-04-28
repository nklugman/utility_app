package gridwatch.kplc;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import gridwatch.kplc.activities.outage_map.OutageMapActivity;
import gridwatch.kplc.activities.payment.MakePaymentActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by Arun on 4/23/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {
//    @Rule
//    public ActivityTestRule<MainActivity> mMainActivityTestRule = new
//            ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void Prepay_login() {
        String account_number = "1111";

        // locate and click on the prepay button
        onView(withId(R.id.loginPrePayRadio)).perform(click());

        // fill out the prepay box with an account number
        onView(withId(R.id.loginPrePayEditText)).perform(typeText(account_number), closeSoftKeyboard());

        // click submit
        onView(withId(R.id.login_submit)).perform(click());

        // check if the home page is displayed
        onView(withId(R.id.nav_view)).check(matches(allOf(isDescendantOfA(withId(R.id.drawer_layout)), isDisplayed())));
    }

    @Test
    public void Postpay_login() {
        String account_number = "1111";

        // locate and click on the prepay button
        onView(withId(R.id.loginPostPayRadio)).perform(click());

        // fill out the prepay box with an account number
        onView(withId(R.id.loginPostPayEditText)).perform(typeText(account_number), closeSoftKeyboard());

        // click submit
        onView(withId(R.id.login_submit)).perform(click());

        // check if the home page is displayed
        onView(withId(R.id.nav_view)).check(matches(allOf(isDescendantOfA(withId(R.id.drawer_layout)), isDisplayed())));
    }

    @Test
    public void Skip_login() {
        // press skip
        onView(withId(R.id.skip_btn)).perform(click());

        // click ok (not sure what id is yet)
        onView(withId(R.id.login_ok)).perform(click());

        // check if the home page is displayed
        onView(withId(R.id.nav_view)).check(matches(allOf(isDescendantOfA(withId(R.id.drawer_layout)), isDisplayed())));
    }
}