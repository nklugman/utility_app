package gridwatch.kplc.activities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Created by ppannuto on 3/1/17.
 */

// https://developer.android.com/training/volley/requestqueue.html#singleton
public class Singletons {
    private static Singletons mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private Singletons(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized Singletons getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Singletons(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
