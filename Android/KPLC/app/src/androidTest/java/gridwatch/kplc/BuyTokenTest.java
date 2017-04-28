package gridwatch.kplc;

import android.content.Intent;
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
 * Created by Arun on 4/24/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BuyTokenTest {
//    @Rule
//    public ActivityTestRule<MainActivity> mMainActivityTestRule = new
//            ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void test_token_buying() {
        String tokennumber = "10";
        String mpesapin = "0";

        // fill out the token field
        onView(withId(R.id.buyTokenPurchaseEditText)).perform(typeText(tokennumber), closeSoftKeyboard());

        // fill out the M-PESA Pin field
        onView(withId(R.id.buyTokenPinEditText)).perform(typeText(mpesapin), closeSoftKeyboard());

        // click Confirm
        onView(withId(R.id.buyTokenConfirmButton)).perform(click());

        // check if the ___ page is displayed
        onView(withId(R.id.nav_view)).check(matches(allOf(isDescendantOfA(withId(R.id.drawer_layout)), isDisplayed())));
    }
}
