package gridwatch.kplc.activities.gridwatch;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import gridwatch.kplc.activities.config.IntentConfig;
import gridwatch.kplc.activities.config.SettingsConfig;

public class GridWatchService extends Service {

    private SharedPreferences prefs;

    private String iemi;

    public GridWatchService() {
    }



    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerPowerCallbacks();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
            iemi = intent.getStringExtra(IntentConfig.IEMI);
        } catch (Exception e) {
            Log.e("null intent", "iemi");
        }

        return START_STICKY;
    }

    public void registerPowerCallbacks() {
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(Intent.ACTION_POWER_CONNECTED);
        ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        ifilter.addAction(Intent.ACTION_DOCK_EVENT);
        this.registerReceiver(mPowerActionReceiver, ifilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Handles the call back for when various power actions occur
    private BroadcastReceiver mPowerActionReceiver = new BroadcastReceiver() {
        GridWatch a;

        @Override
        public void onReceive(Context context, Intent intent) {
            String version_num = String.valueOf(prefs.getInt(SettingsConfig.VERSION_NUM, -1));
            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                 a = new GridWatch(getApplicationContext(), iemi, version_num, "CONNECTED");
            } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                a = new GridWatch(getApplicationContext(),iemi, version_num,"DISCONNECTED");
            }  else {
                a = new GridWatch(getApplicationContext(),iemi, version_num,"UNKNOWN");
            }
            a.run();
        }
    };
}
