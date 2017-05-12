package gridwatch.kplc;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.core.AllOf;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import gridwatch.kplc.activities.HomeActivity;
import gridwatch.kplc.activities.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Arun on 5/11/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OutageReportTest {
    @Rule
    public ActivityTestRule<HomeActivity> activityRule = new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void AreportOutage_Failure() {
        String exampleNumber = "1111";
        // test to click Report Outage button and see if dialog box pops up
        onView(withId(R.id.outage_btn)).perform(click());
        onView(withText(R.string.report_dialog)).inRoot(isDialog()).check(matches(isDisplayed()));

        // hit cancel to go back to the home page
        onView(withId(android.R.id.button2)).perform(click());
        onView(withId(R.id.outage_btn)).perform(click());
        onView(withText(R.string.report_dialog)).inRoot(isDialog()).check(matches(isDisplayed()));

        // type the wrong code then press enter
        //onView(withId(R.id.edittext.getText())).perform(typeText(exampleNumber));
        onView(withId(android.R.id.button1)).perform(click());

        // see if failure message pops up
        onView(withText(R.string.report_dialog)).inRoot(isDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void BreportOutage_Success() {
        //String exampleNumber = R.id.random;
        // test to click Report Outage button and see if dialog box pops up
        onView(withId(R.id.outage_btn)).perform(click());
        onView(withText(R.string.report_dialog)).inRoot(isDialog()).check(matches(isDisplayed()));

        // type the right code then press enter
        //onView(withId(R.id.edittext.getText())).perform(typeText(exampleNumber));
        onView(withId(android.R.id.button1)).perform(click());

        // see if it goes back to home page
        onView(withId(R.id.outage_btn)).perform(click());
        onView(withText(R.string.report_dialog)).inRoot(isDialog()).check(matches(isDisplayed()));
    }
}
