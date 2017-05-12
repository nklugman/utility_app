package gridwatch.kplc;

import gridwatch.kplc.activities.settings.SettingsActivity;


import android.content.pm.ActivityInfo;
import android.preference.Preference;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


import java.util.concurrent.TimeUnit;

import gridwatch.kplc.activities.ContactActivity;
import gridwatch.kplc.activities.LoginActivity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static gridwatch.kplc.R.id.editSettings;
import static gridwatch.kplc.R.layout.activity_settings;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by Arun on 5/12/17.
 */

public class SettingsTest {
    @Rule
    public ActivityTestRule<SettingsActivity> mActivityRule = new ActivityTestRule<>(
            SettingsActivity.class);

    @Test
    public void CtestAmountText_thenClear() throws InterruptedException {
        onData(withId(editSettings)).perform(click());
        TimeUnit.SECONDS.sleep(2);
        onView(withId(R.id.editSettings)).perform(typeText("1111"));
        TimeUnit.SECONDS.sleep(2);
//        onData(allOf(
//            is(instanceOf(Preference.class)),
//            withId(activity_settings),
//            .onChildView(withText("Account Number"))
//            .check(matches(isDisplayed())));
    }
}
