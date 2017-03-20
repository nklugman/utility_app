package gridwatch.kplc.activities.outage_reporting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.HomeActivity;
import gridwatch.kplc.activities.config.IntentConfig;
import gridwatch.kplc.activities.config.SettingsConfig;
import gridwatch.kplc.activities.database.OutageElsewhere;
import gridwatch.kplc.activities.dialogs.ReportDialog;
import io.realm.Realm;

public class OutageReportNewActivity extends AppCompatActivity implements ReportDialog.ReportDialogListener {

    EditText account_et;
    EditText notes_et;
    EditText location_et;
    EditText name_et;
    TextView location_label;
    TextView location_text;
    TextView location_gp_label;
    Button choose_address_btn;
    Button submit_btn;
    int PLACE_PICKER_REQUEST = 1;
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

    boolean is_resolved = false;
    boolean submit_pushed = false;
    String locationAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outage_report_other);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Realm.init(this);

        location_et = (EditText) findViewById(R.id.location_edit_text); //set this without google service
        location_label = (TextView) findViewById(R.id.location_label);

        location_text = (TextView) findViewById(R.id.location_text); //set this with google service update
        choose_address_btn = (Button) findViewById(R.id.enter_location_btn);
        location_gp_label = (TextView) findViewById(R.id.location_gp_label);

        account_et = (EditText) findViewById(R.id.account_num_edit_text);
        notes_et = (EditText) findViewById(R.id.notes_edit_text);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        name_et = (EditText) findViewById(R.id.name_text);


        boolean gp_available = isGooglePlayServicesAvailable(this);
        if (gp_available) {
            location_et.setVisibility(View.GONE);
            location_label.setVisibility(View.GONE);
        } else {
            location_text.setVisibility(View.GONE);
            choose_address_btn.setVisibility(View.GONE);
            location_gp_label.setVisibility(View.GONE);
        }

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_logic()) {
                    other_account = account_et.getText().toString();
                    notes = notes_et.getText().toString();
                    address = location_text.getText().toString();
                    name = name_et.getText().toString();
                    account_number = prefs.getString("setting_key_account_number", "-1");
                    meter_number = prefs.getString("setting_key_meter_number", "-1");
                    save_to_realm();
                    if (is_resolved) {
                        report_outage();
                    } else {
                        submit_pushed = true;
                        display_waiting();
                    }
                }
            }
        });

        choose_address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_place_picker();
            }
        });

        location_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.e("focus", String.valueOf(b));
                if (!b) {
                    lookup_location(location_et.getText().toString());
                }
            }
        });
    }

    private void display_waiting() {
        Toast.makeText(this, "Submitting", Toast.LENGTH_LONG);

    }


    private void lookup_location(String text) {
        Log.e("looking up location", text);
        GeocodingLocation locationAddress = new GeocodingLocation();
        locationAddress.getAddressFromLocation(text,
                getApplicationContext(), new GeocoderHandler());
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    Log.e("resolved", message.getData().keySet().toString());
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            is_resolved = true;
            if (submit_pushed) {
                report_outage();
            }
            Log.e("resolved address", locationAddress);
        }
    }

    private void save_to_realm() {
        long time = System.currentTimeMillis();
        String account_number = prefs.getString("setting_key_account_number", "-1");
        String meter_number = prefs.getString("setting_key_meter_number", "-1");
        String other_account = account_et.getText().toString();
        String address = location_et.getText().toString();
        String notes = notes_et.getText().toString();
        boolean isOnline = isNetworkAvailable();
        String name = name_et.getText().toString();

        OutageElsewhere cur = new OutageElsewhere(time, account_number, meter_number, other_account, address, notes, isOnline, name, lat, lng);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(cur);
        realm.commitTransaction();
    }

    private void show_place_picker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public boolean isGooglePlayServicesAvailable(Context context){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                lat = String.valueOf(place.getLatLng().latitude);
                lng = String.valueOf(place.getLatLng().longitude);
            }
        }
    }

    private boolean check_logic() { //TODO
        boolean am_online = isNetworkAvailable();
        if (!am_online) {
            show_not_online_err();
            prefs.edit().putBoolean(SettingsConfig.IS_ONLINE, false).apply();
        } else {
            prefs.edit().putBoolean(SettingsConfig.IS_ONLINE, true).apply();
        }
        String other_account = account_et.getText().toString();
        String address = location_et.getText().toString();

        if (other_account.equals("") && address.equals("")) {
            show_need_account_number_or_address();
            return false;
        }
        if (other_account.equals("")) {
            show_needs_account();
            return false;
        }
        if (address.equals("")) {
            show_need_location();
            return false;
        }
        return true; //TODO

    }

    private void show_need_account_number_or_address() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.show_need_account_numer_or_address);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void show_needs_account() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.show_need_account);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void show_need_location() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.show_need_location);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
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
