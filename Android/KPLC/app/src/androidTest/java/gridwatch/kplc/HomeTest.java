package gridwatch.kplc;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.android.sample.espressouitestsample.R;
import com.android.sample.espressouitestsample.signup.SignUpActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import gridwatch.kplc.activities.ContactActivity;
import gridwatch.kplc.activities.billing.BalanceHistoryActivity;
import gridwatch.kplc.activities.billing.UsageChartsActivity;
import gridwatch.kplc.activities.news_feed.NewsfeedActivity;
import gridwatch.kplc.activities.outage_map.OutageMapActivity;
import gridwatch.kplc.activities.payment.BuyTokensActivity;
import gridwatch.kplc.activities.payment.MakePaymentActivity;
import gridwatch.kplc.activities.settings.SettingsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeTest {
//    @Rule
//    public ActivityTestRule<MainActivity> mMainActivityTestRule = new
//            ActivityTestRule<MainActivity>(MainActivity.class);

    private void launch_class(Class to_launch) {
        Intent e = new Intent(this, to_launch);
        startActivity(e);
    }

    @Test
    public void clicksOutageMapButton_opensOutageMap() {
        // click on the outage map button
        launch_class(OutageMapActivity.class);

        // check if the outage map screen is displayed
        onView(withId(R.id.button2)).check(matches(allOf(isDescendantOfA(withId(R.id.activity_outage_map)), isDisplayed())));
    }

    @Test
    public void clicksPaymentButton_opensPayment() {
        // click on the payment button
        launch_class(MakePaymentActivity.class);

        // check if the payment screen is displayed
        onView(withId(R.id.paymentRadioGroup)).check(matches(allOf(isDescendantOfA(withId(R.id.activity_payment)), isDisplayed())));
    }

    @Test
    public void clicksBalanceButton_opensBalance() {
        // click on the balance button
        launch_class(BalanceHistoryActivity.class);

        // check if the payment screen is displayed
        onView(withId(R.id.textView4).check(matches(allOf(isDescendantOfA(withId(R.id.activity_balance)), isDisplayed())));
    }

    @Test
    public void clicksUsageButton_opensUsage() {
        // click on the balance button
        launch_class(UsageChartsActivity.class);

        // check if the payment screen is displayed
        onView(withId(R.id.textView4).check(matches(allOf(isDescendantOfA(withId(R.id.activity_usage)), isDisplayed())));
    }

    @Test
    public void clicksTokensButton_opensTokens() {
        // click on the balance button
        launch_class(BuyTokensActivity.class);

        // check if the payment screen is displayed
        onView(withId(R.id.buyTokenBalanceTextView).check(matches(allOf(isDescendantOfA(withId(R.id.activity_token)), isDisplayed())));
    }

    @Test
    public void clicksNewsfeedButton_opensNewsfeed() {
        // click on the balance button
        launch_class(NewsfeedActivity.class);

        // check if the payment screen is displayed
        onView(withId(R.id.listview_newsfeed).check(matches(allOf(isDescendantOfA(withId(R.id.activity_newsfeed)), isDisplayed())));
    }

    @Test
    public void clicksSettingsButton_opensSettings() {
        // click on the balance button
        launch_class(SettingsActivity.class);

        // check if the payment screen is displayed
        onView(withId(R.id.accountinfoedit).check(matches(allOf(isDescendantOfA(withId(R.id.activity_settings)), isDisplayed())));
    }

    @Test
    public void clicksContactButton_opensContact() {
        // click on the balance button
        launch_class(ContactActivity.class);

        // check if the payment screen is displayed
        onView(withId(R.id.send).check(matches(allOf(isDescendantOfA(withId(R.id.activity_contact)), isDisplayed())));
    }



//    @Test
//    public void clickLoginButton_openLoginScreen() {
//        //locate and click on the login button
//        onView(withId(R.id.button_login)).perform(click());
//
//        //check that the login screen is displayed
//        onView(withId(R.id.edit_text_email)).check(matches(allOf(isDescendantOfA(withId(R.id.layout_login)), isDisplayed())));
//    }
}

