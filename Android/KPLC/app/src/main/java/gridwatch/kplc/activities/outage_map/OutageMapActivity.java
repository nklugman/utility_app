package gridwatch.kplc.activities.outage_map;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import gridwatch.kplc.R;

public class OutageMapActivity extends AppCompatActivity {

    WebView webview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        //webview = (WebView) findViewById(R.id.outage_map_webview);
        webview = new WebView(this);

        setContentView(R.layout.activity_outage_map); // only for testing
        //setContentView(webview); // for actual app

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setGeolocationEnabled(true);
        webview.getSettings().setBuiltInZoomControls(false);
        webview.getSettings().setSupportZoom(false);
        webview.setVerticalScrollBarEnabled(true);
        webview.setHorizontalScrollBarEnabled(true);
        webview.loadUrl("http://141.212.11.206/map/map.html");

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
                //Your code to do
                Log.e("webview", error.toString());
            }


        });

        /*
        webview.loadUrl("file:///android_asset/map.html");


        //setContentView(R.layout.activity_outage_map);

        webview.addJavascriptInterface(new JSInterface(this, webview), "MyHandler");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        */


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); //ERROR<-----------
        inflater.inflate(R.menu.web_refresh, menu);
        return super.onCreateOptionsMenu(menu); // in Fragment cannot be applied <------------
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:

                // Complete with your code
                webview.reload();
                Log.e("webview", "reloading");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
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
    */



}
