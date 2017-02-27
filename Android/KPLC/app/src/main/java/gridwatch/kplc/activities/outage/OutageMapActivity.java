package gridwatch.kplc.activities.outage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class OutageMapActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl("http://141.212.11.206/map/map.html");


        //setContentView(R.layout.activity_outage_map);


    }
}
