package gridwatch.kplc.activities.outage;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;


/**
 * Created by nklugman on 2/27/17.
 */

public class JSInterface {
    protected OutageMapActivity parentActivity;
    protected WebView mWebView;

    public JSInterface(OutageMapActivity _activity, WebView _webView)  {
        parentActivity = _activity;
        mWebView = _webView;

    }

    @JavascriptInterface
    public void setResult(int val){
        Log.v("mylog","JavaScriptHandler.setResult is called : " + val);
        this.parentActivity.javascriptCallFinished(val);
    }

    @JavascriptInterface
    public void calcSomething(int x, int y){
        this.parentActivity.changeText("Result is : " + (x * y));
    }

    @JavascriptInterface
    public String modifyString(String inputString) {
        return inputString + " from Java side";
    }
}