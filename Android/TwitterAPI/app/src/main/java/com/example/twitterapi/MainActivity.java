package com.example.twitterapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "DyAJEKsgPQ6L0is9rOlSzpWQb";
    private static final String TWITTER_SECRET = "hsP2rm9qHhBhoXBJmQ23gQwjdFaafulPAzlPm84atc99cYP3KM";

    private TwitterLoginButton loginButton;
    TextView textView;
    TwitterSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);


        textView = (TextView) findViewById(R.id.tv_username);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {



                session = result.data;

                String username = session.getUserName();
                Long  userid = session.getUserId();


                textView.setText("Hi " + username);





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

//    void getUserData() {
//        retrofit2.Call<User> call= Twitter.getApiClient(session).getAccountService()
//                .verifyCredentials(true, false);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void failure(TwitterException e) {
//                //If any error occurs handle it here
//            }
//            @Override
//            public void success(Result<User> userResult) {
//                //If it succeeds creating a User object from userResult.data
//                User user = userResult.data;
//                String twitterImage = user.profileImageUrl;
//
//                try {
//                    Log.d("imageurl", user.profileImageUrl);
//                    Log.d("name", user.name);
//                    Log.d("email",user.email);
//                    Log.d("des", user.description);
//                    Log.d("followers ", String.valueOf(user.followersCount));
//                    Log.d("createdAt", user.createdAt);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }


}
