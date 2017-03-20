package gridwatch.kplc.activities.outage_reporting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.HomeActivity;
import gridwatch.kplc.activities.config.IntentConfig;
import gridwatch.kplc.activities.config.SettingsConfig;
import gridwatch.kplc.activities.database.OutageElsewhere;
import gridwatch.kplc.activities.dialogs.ReportDialog;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static gridwatch.kplc.R.id.location_text;
import static gridwatch.kplc.R.id.meter_num;

public class OutageReportOtherLocationActivity extends AppCompatActivity implements ReportDialog.ReportDialogListener {

    private ListView listView;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String, Object>> list;
    EditText inputSearch;
    private Realm realm;

    String lat = "-1";
    String lng = "-1";
    String other_account = "-1";
    String notes = "-1";
    String address = "-1";
    String name = "-1";
    String account_number = "-1";
    String meter_number = "-1";
    boolean is_sms;

    private Button create_new_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outage_report_other_location);

        listView = (ListView) findViewById(R.id.listview_locations);
        list = new ArrayList<HashMap<String, Object>>();
        inputSearch = (EditText) findViewById(R.id.location_search);
        create_new_btn = (Button) findViewById(R.id.create_btn);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        adapter = new SimpleAdapter(getBaseContext(), list, R.layout.outage_location_listitem,
                new String[] {"other_account_num", "name_text", "location_text", "lat", "lng", "notes", "meter_num", "account_num"},
                new int[] {R.id.other_account_num, R.id.name_text, location_text, R.id.lat, R.id.lng, R.id.notes, R.id.meter_num, R.id.account_num});
        listView.setAdapter(adapter);

        int num_past_location = requestFromRealm();
        if (num_past_location == 0) {
            go_to_new_activity();
        }

        create_new_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go_to_new_activity();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                HashMap<String, Object> current= (HashMap) adapter.getItemAtPosition(position);
                other_account = (String) current.get("other_account_num");
                name = (String) current.get("name_text");
                lat = (String) current.get("lat");
                lng = (String) current.get("lng");
                notes = (String) current.get("notes");
                address = (String) current.get("location_text");
                account_number = (String) current.get("account_num");
                meter_number = (String) current.get("meter_num");
                Log.e("click", other_account + " " + name + " " + lat + " " + lng + " " + notes + " " + location_text + " " + account_number + " " + meter_num);
                report_outage();
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() { //TODO
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Log.i("list", cs.toString());
                ArrayList<HashMap<String, Object>> tempList= new ArrayList<>();
                for(int i=0 ;i< list.size();i++) {
                    if (list.get(i).get("name_text").toString().toLowerCase().contains(cs.toString().toLowerCase())) {
                        tempList.add(list.get(i));
                        Log.i("good", list.get(i).get("name_text").toString());
                    }
                }
                adapter = new SimpleAdapter(getBaseContext(), tempList, R.layout.outage_location_listitem,
                        new String[] {"other_account_num", "name_text", "location_text", "lat", "lng", "notes", "meter_num", "account_num"},
                        new int[] {R.id.other_account_num, R.id.name_text, location_text, R.id.lat, R.id.lng, R.id.notes, R.id.meter_num, R.id.account_num});
                listView.setAdapter(adapter);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void go_to_new_activity() {
        Intent a = new Intent(this, OutageReportNewActivity.class);
        startActivity(a);
    }

    private int requestFromRealm() {
        RealmResults<OutageElsewhere> items;
        items = realm.where(OutageElsewhere.class).findAllSorted("mTime", Sort.DESCENDING);
        for(OutageElsewhere item : items) {
            HashMap<String, Object> map = new HashMap<>();
            String name = item.getName();
            String other_account = item.getOtherAccount();
            String location = item.getAddress();
            String lat = item.getLat();
            String lng = item.getLng();
            String notes = item.getNotes();
            String meter_num = item.getMeter();
            String account_num = item.getAccount();
            map.put("other_account_num", other_account);
            map.put("name_text", name);
            map.put("location_text", location);
            map.put("lat",lat);
            map.put("lng",lng);
            map.put("notes",notes);
            map.put("meter_num", meter_num);
            map.put("account_num", account_num);
            list.add(map);
            adapter = new SimpleAdapter(getBaseContext(), list, R.layout.outage_location_listitem,
                    new String[] {"other_account_num", "name_text", "location_text", "lat", "lng", "notes", "meter_num", "account_num"},
                    new int[] {R.id.other_account_num, R.id.name_text, location_text, R.id.lat, R.id.lng, R.id.notes, R.id.meter_num, R.id.account_num});
            listView.setAdapter(adapter);
        }
        return items.size();
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
        return isAvailable;
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
