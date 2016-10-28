package gridwatch.kplc;

import android.app.Activity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.junit.Test;
import io.fabric.sdk.android.Fabric;



public class TwitterUnitTest extends Activity {
    private static final String TWITTER_KEY = "DyAJEKsgPQ6L0is9rOlSzpWQb";
    private static final String TWITTER_SECRET = "hsP2rm9qHhBhoXBJmQ23gQwjdFaafulPAzlPm84atc99cYP3KM";
    TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);

    @Test
    public void TwitterConnected() throws Exception {
        try{
//            Fabric.with(this, new Twitter(authConfig));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}


