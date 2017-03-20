package gridwatch.kplc.activities.outage_reporting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.HomeActivity;
import gridwatch.kplc.activities.config.IntentConfig;
import gridwatch.kplc.activities.config.SettingsConfig;
import gridwatch.kplc.activities.dialogs.ReportDialog;
import gridwatch.kplc.activities.settings.SettingsActivity;

public class OutageActivity extends AppCompatActivity implements ReportDialog.ReportDialogListener {

    Button report_here_btn;
    Button report_home_btn;
    Button report_other_btn;
    SharedPreferences prefs;

    String lat = "-1";
    String lng = "-1";
    String other_account = "-1";
    String notes = "-1";
    String address = "-1";
    String name = "-1";
    String account_number = "-1";
    String meter_number = "-1";
    boolean is_sms = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        report_here_btn = (Button) findViewById(R.id.report_here_btn);
        report_home_btn = (Button) findViewById(R.id.report_home_btn);
        report_other_btn = (Button) findViewById(R.id.report_other_btn);

        report_here_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_account_set();
            }
        });
        report_home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_home_set();
            }
        });
        report_other_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go_to_report_other();
            }
        });
    }

    //String other_account, String name, String address, String notes, String lat, String lng, String account, String meter_num
    private void check_home_set() {
        check_logic();
        address = prefs.getString("Address", "-1");
        Log.e("address", address);
        if (address.equals("-1")) {
            prompt_set_address();
        } else {
            report_outage();
        }
    }

    private void check_account_set() {
        check_logic();
        account_number = prefs.getString("setting_key_account_number", "-1");
        meter_number = prefs.getString("setting_key_meter_number", "-1");
        Log.e("account number", account_number);
        Log.e("meter number", meter_number);
        if (account_number.equals("-1") && meter_number.equals("-1")) {
            prompt_set_account();
            return;
        } else {
            report_outage();
        }
    }

    private void prompt_set_address() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.prompt_set_address);
        builder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                go_to_settings_page();
            }
        });
        builder.setNegativeButton(R.string.go_back, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void prompt_set_account() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.prompt_set_account);
        builder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                go_to_settings_page();
            }
        });
        builder.setNegativeButton(R.string.go_back, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void check_logic() {
        boolean am_online = isNetworkAvailable();
        if (!am_online) {
            show_not_online_err();
            prefs.edit().putBoolean(SettingsConfig.IS_ONLINE, false).apply();
        } else {
            prefs.edit().putBoolean(SettingsConfig.IS_ONLINE, true).apply();
        }
    }

    private void go_to_report_other() {
        Intent a = new Intent(this, OutageReportOtherLocationActivity.class);
        startActivity(a);
    }

    private void go_to_settings_page() {
        Intent a = new Intent(this, SettingsActivity.class);
        startActivity(a);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        prefs.edit().putBoolean(SettingsConfig.IS_ONLINE, isAvailable).apply();
        return isAvailable;
    }

    private void show_not_online_err() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.not_online_dialog);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void go_home() {
        Intent a = new Intent(this, HomeActivity.class);
        a.putExtra(IntentConfig.INTENT_TO_HOME, IntentConfig.INTENT_TO_HOME);
        a.putExtra(IntentConfig.OTHER_ACCOUNT, other_account);
        a.putExtra(IntentConfig.OTHER_ADDRESS, address);
        a.putExtra(IntentConfig.OTHER_NAME, name);
        a.putExtra(IntentConfig.OTHER_NOTES, notes);
        a.putExtra(IntentConfig.OTHER_LAT, lat);
        a.putExtra(IntentConfig.OTHER_LNG, lng);
        a.putExtra(IntentConfig.SMS, is_sms);
        startActivity(a);
    }

    private void report_outage() {
        if (isNetworkAvailable()) {
            DialogFragment dialog = new ReportDialog();
            dialog.show(getSupportFragmentManager(), "ReportDialogFragment");
        } else {
            show_sms_warning(SettingsConfig.GRIDWATCH);
        }
    }

    @Override
    public void onDialogReturnValue(Boolean result) {
        if (result) {
            go_home();
        }
    }

    private void show_sms_warning(final String sms_type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.show_SMS_warning);
        builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                is_sms = true;
                go_home();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
