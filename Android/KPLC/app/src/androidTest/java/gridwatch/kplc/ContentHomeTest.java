package gridwatch.kplc;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import gridwatch.kplc.activities.outage_map.OutageMapActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
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
public class ContentHomeTest {

    @Test
    public void clicksCheckBalance_opensBalance() {
        // click on the outage map button
        onView(withId(R.id.check_balance)).perform(click());

        // check if the check balance page is displayed
        onView(withId(R.id.textView4).check(matches(allOf(isDescendantOfA(withId(R.id.activity_balance)), isDisplayed())));
    }

    @Test
    public void clicksMakePayment_opensPayment() {
        // click on the outage map button
        onView(withId(R.id.make_payment)).perform(click());

        // check if the check payment page is displayed
        onView(withId(R.id.paymentRadioGroup)).check(matches(allOf(isDescendantOfA(withId(R.id.activity_payment)), isDisplayed())));
    }

    @Test
    public void clicksReportOutage_opensOutageReport() {
        // click on the outage map button
        onView(withId(R.id.outage_btn)).perform(click());

        // check if the check outage map page is displayed
        onView(withId(R.id.button2)).check(matches(allOf(isDescendantOfA(withId(R.id.activity_outage)), isDisplayed())));
    }
}
