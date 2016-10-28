package gridwatch.kplc.activities.social_media.Twitter;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import gridwatch.kplc.R;
import io.fabric.sdk.android.Fabric;

public class TwitterActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "DyAJEKsgPQ6L0is9rOlSzpWQb";
    private static final String TWITTER_SECRET = "hsP2rm9qHhBhoXBJmQ23gQwjdFaafulPAzlPm84atc99cYP3KM";

    private TwitterLoginButton loginButton;
    TextView textView;
    TwitterSession session;
    public static String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_twitter);


        textView = (TextView) findViewById(R.id.tv_username);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                session = result.data;
                UserName = session.getUserName();
                Long  userid = session.getUserId();
                textView.setText("Hi " + UserName);
                launch_class(TwitterTimeline.class);
//                launch_class(UserTweetActivity.class);
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    private void launch_class(Class to_launch) {
        Intent e = new Intent(this, to_launch);
        startActivity(e);
    }
}
