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
 * Created by Arun on 4/25/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ContactTest {
//    @Rule
//    public ActivityTestRule<MainActivity> mMainActivityTestRule = new
//            ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void Contact_us() {
        String name = "John Doe";
        String email = "John.Doe@example.com";
        String message = "My power is off";

        // fill out the name field
        onView(withId(R.id.editText3)).perform(typeText(name), closeSoftKeyboard());

        // fill out the email field
        onView(withId(R.id.editText4)).perform(typeText(email), closeSoftKeyboard());

        // fill out the message field
        onView(withId(R.id.feedbackbox)).perform(typeText(message), closeSoftKeyboard());

        // click Send your Message
        onView(withId(R.id.send)).perform(click());

        // check if an email activity is started
        onView(startActivity(Intent.createChooser(email, "Send mail..."))); // this line is completely wrong
    }
}