package gridwatch.kplc.activities.social_media.Twitter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.ListActivity;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import gridwatch.kplc.R;

public class TwitterTimeline extends ListActivity {
    private static final String KPLCTwitter = "KenyaPower_Care";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_timeline);
        showTargetTweet();
    }
    private void showTargetTweet(){
        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(KPLCTwitter)//Kenya Power
//                .screenName("KenyaPower")//Kenya Power Limited
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();
        setListAdapter(adapter);
    }
    private boolean checkTarget(){
        return KPLCTwitter == "KenyaPower_Care";
    }
}
