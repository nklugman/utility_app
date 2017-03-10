package gridwatch.kplc.activities.outage_map;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.widget.Toast;

import gridwatch.kplc.R;

public class OutageMapActivity extends AppCompatActivity {

    WebView webview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //webview = (WebView) findViewById(R.id.outage_map_webview);
        webview = new WebView(this);
        setContentView(webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setGeolocationEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setSupportZoom(true);
        webview.setVerticalScrollBarEnabled(true);
        webview.setHorizontalScrollBarEnabled(true);
        //webview.loadUrl("http://141.212.11.206/map/map.html");
        webview.loadUrl("file:///android_asset/map.html");


        //setContentView(R.layout.activity_outage_map);

        webview.addJavascriptInterface(new JSInterface(this, webview), "MyHandler");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }


    }

    public void changeText(String someText){
        Log.v("mylog","changeText is called");
        webview.loadUrl("javascript:document.getElementById('test1').innerHTML = '<strong>"+someText+"</strong>'");
    }

    public void javascriptCallFinished(final int val){
        Log.v("mylog","MyActivity.javascriptCallFinished is called : " + val);
        Toast.makeText(this, "Callback got val: " + val, Toast.LENGTH_SHORT).show();

        // I need to run set operation of UI on the main thread.
        // therefore, the above parameter "val" must be final
        runOnUiThread(new Runnable() {
            public void run() {
                Log.e("js_callback","Callback got val: " + val);
            }
        });
    }



}
