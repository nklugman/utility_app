package gridwatch.kplc;

import android.app.Activity;
import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import gridwatch.kplc.activities.ContactActivity;
import gridwatch.kplc.activities.HomeActivity;
import gridwatch.kplc.activities.billing.BalanceHistoryActivity;
import gridwatch.kplc.activities.news_feed.NewsfeedActivity;
import gridwatch.kplc.activities.outage_map.OutageMapActivity;
import gridwatch.kplc.activities.outage_reporting.OutageActivity;
import gridwatch.kplc.activities.payment.BuyTokensActivity;
import gridwatch.kplc.activities.settings.SettingsActivity;


import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.v4.content.ContextCompat.startActivity;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeTest {

    @Rule
    public final ActivityTestRule<OutageMapActivity> OutageMapActivityRule =
            new ActivityTestRule<>(OutageMapActivity.class, true, false);

    @Rule
    public final ActivityTestRule<ContactActivity> ContactActivityRule =
            new ActivityTestRule<>(ContactActivity.class, true, false);

    @Rule
    public final ActivityTestRule<OutageActivity> ReportActivityRule =
            new ActivityTestRule<>(OutageActivity.class, true, false);

    @Rule
    public final ActivityTestRule<NewsfeedActivity> NewsfeedActivityRule =
            new ActivityTestRule<>(NewsfeedActivity.class, true, false);

//    @Rule
//    public final ActivityTestRule<BuyTokensActivity> TokensActivityRule =
//            new ActivityTestRule<>(BuyTokensActivity.class, true, false);

//    @Rule
//    public final ActivityTestRule<SettingsActivity> SettingActivityRule =
//            new ActivityTestRule<>(SettingsActivity.class, true, false);







    @Test
    public void clicksOutageMapButton_opensOutageMap() { // works with workaround
        // click on the outage map button
        OutageMapActivityRule.launchActivity(null);

        // check if the outage map screen is displayed
        onView(withId(R.id.outage_map_webview)).check(matches(allOf(isDescendantOfA(withId(R.id.activity_outage_map)), isDisplayed())));
    }

    @Test
    public void clicksContactButton_opensContact() { // working
        // click on the contact button
        ContactActivityRule.launchActivity(null);

        // check if the contact us page is displayed
        onView(withId(R.id.editText3)).check(matches(allOf(isDescendantOfA(withId(R.id.activity_contact)), isDisplayed())));
    }

    @Test
    public void clicksOutageReportButton_opensOutageReport() { // working
        // click on the outage report button
        ReportActivityRule.launchActivity(null);

        // check if the outage report screen is displayed
        onView(withId(R.id.textView9)).check(matches(allOf(isDescendantOfA(withId(R.id.activity_report)), isDisplayed())));
    }

    @Test
    public void clicksNewsfeedButton_opensNewsfeed() { //working
        // click on the newsfeed button
        NewsfeedActivityRule.launchActivity(null);

        // check if the news feed screen is displayed
        onView(withId(R.id.inputSearch)).check(matches(allOf(isDescendantOfA(withId(R.id.activity_newsfeed)), isDisplayed())));
    }

//    @Test
//    public void clicksBuyTokenButton_opensBuyToken() { // can only work if logged in
//        // click on the buy tokens button
//        TokensActivityRule.launchActivity(null);
//
//        // check if the buy tokens screen is displayed
//        onView(withId(R.id.buyTokenPinEditText)).check(matches(allOf(isDescendantOfA(withId(R.id.activity_tokens)), isDisplayed())));
//    }

//    @Test
//    public void clicksSettingsButton_opensSettings() { // not working because settingsactivity.java does not use layout of settings.xml
//        // click on the settings button
//        SettingActivityRule.launchActivity(null);
//
//        // check if the settings page is displayed
//        onView(withId(R.id.editSettings)).check(matches(withId(R.xml.settings)));
//        //onView(withId(R.id.editSettings)).check(matches(allOf(isDescendantOfA(withId(R.xml.settings)), isDisplayed())));
//    }

    @Rule
    public ActivityTestRule<HomeActivity> activityRule = new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void AclickPrePayButton() {
        // report an outage
        onView(withId(R.id.outage_btn)).perform(click());

    }

}

