package gridwatch.kplc;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

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
 * Created by Arun on 4/27/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PaymentTest {
//    @Rule
//    public ActivityTestRule<MainActivity> mMainActivityTestRule = new
//            ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void Pay_current_balance() {
        String mpesapin = "0";
        String amount = "10";

        // locate and click on the current balance button
        onView(withId(R.id.currentbalance)).perform(click());

        // fill in the M-PESA Pin
        onView(withId(R.id.pin)).perform(typeText(mpesapin), closeSoftKeyboard());

        // locate and click on the confirm button
        onView(withId(R.id.confirm_button)).perform(click());

        // check if the ___ page is displayed
        onView(withId(R.id.nav_view)).check(matches(allOf(isDescendantOfA(withId(R.id.layout_home)), isDisplayed())));
    }

    @Test
    public void Pay_other_balance() {
        String mpesapin = "0"
        String amount = "10"

        // locate and click on the current balance button
        onView(withId(R.id.otheramount)).perform(click());

        // fill out the prepay box with an account number
        onView(withId(R.id.amount)).perform(typeText(amount), closeSoftKeyboard());

        // fill in the M-PESA Pin
        onView(withId(R.id.pin)).perform(typeText(mpesapin), closeSoftKeyboard());

        // locate and click on the confirm button
        onView(withId(R.id.confirm_button)).perform(click());

        // check if the ___ page is displayed
        onView(withId(R.id.nav_view)).check(matches(allOf(isDescendantOfA(withId(R.id.layout_home)), isDisplayed())));
    }
}