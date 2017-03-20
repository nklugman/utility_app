package gridwatch.kplc.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.billing.BalanceHistoryActivity;
import gridwatch.kplc.activities.billing.Postpaid;
import gridwatch.kplc.activities.config.IntentConfig;
import gridwatch.kplc.activities.config.SettingsConfig;
import gridwatch.kplc.activities.dialogs.ReportDialog;
import gridwatch.kplc.activities.gridwatch.GridWatch;
import gridwatch.kplc.activities.gridwatch.GridWatchService;
import gridwatch.kplc.activities.news_feed.NewsfeedActivity;
import gridwatch.kplc.activities.outage_map.OutageMapActivity;
import gridwatch.kplc.activities.outage_reporting.OutageActivity;
import gridwatch.kplc.activities.payment.BuyTokensActivity;
import gridwatch.kplc.activities.payment.MakePaymentActivity;
import gridwatch.kplc.activities.settings.SettingsActivity;
import io.realm.Realm;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,  ReportDialog.ReportDialogListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int ACCESS_COURSE_LOCATION = 1;
    private static final int ACCESS_FINE_LOCATION = 2;
    private Realm realm;
    //private TextView cbTv;
    //private TextView last_cbTv;
    //private TextView mpTv;
    //private TextView last_mpTv;
    private TextView cur_balance;
    private TextView cur_due_date;
    private TextView cur_name;
    private TextView cur_balance_label_text;
    private TextView cur_payment_label_text;
    private TextView cur_update_text;

    private int version_num = 1;
    private GoogleApiClient mGoogleApiClient;

    private boolean am_online;
    private boolean is_prepaid;

    private TextView lastUpdateTv;

    private String lat = "-1";
    private String lng = "-1";
    private String other_account = "-1";
    private String notes = "-1";
    private String address = "-1";
    private String name = "-1";
    private String account_number = "-1";
    private String meter_number = "-1";

    private TextView welcomeTv;
    private TextView dateTv;
    private TextView payDueTv;
    private String[] welcomeText = {"Good Morning", "Good Afternoon", "Good Evening"};
    private boolean annon;
    private SharedPreferences prefs;

    private String iemi;

    private void startGWService() {
        Intent intent = new Intent(this, GridWatchService.class);
        intent.putExtra(IntentConfig.IEMI, iemi);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String application_host_server = prefs.getString("setting_key_application_host_server", "http://141.212.11.206");
        final String application_host_port = prefs.getString("setting_key_application_host_port", "3100");
        final String kplc_host_port = prefs.getString("setting_key_kplc_host_port", "3331");
        final String SERVER = "http://" + application_host_server + ":" + application_host_port;
        final String KPLC_SERVER =  application_host_server + ":" + kplc_host_port;
        final String ACCOUNT = prefs.getString("setting_key_account_number", "3202667");
        setContentView(R.layout.activity_home);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();

        Intent intent = getIntent();
        parse_intent(intent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        startGWService();
        iemi = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);


        //


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                //.enableAutoManage(this, this)
                .build();

        is_prepaid = prefs.getBoolean(SettingsConfig.IS_PREPAID, false);

        if (!is_prepaid) {
            menu.findItem(R.id.nav_check_tokens).setVisible(false);
            menu.findItem(R.id.nav_token).setVisible(false);
        } else {
            menu.findItem(R.id.nav_balance).setVisible(false);
            menu.findItem(R.id.nav_payment).setVisible(false);
        }


        annon = prefs.getBoolean(SettingsConfig.ANNON, false);

        prefs.edit().putInt(SettingsConfig.VERSION_NUM, version_num).apply();

        cur_balance_label_text = (TextView) findViewById(R.id.cur_balance_label_text);
        cur_payment_label_text = (TextView) findViewById(R.id.cur_payment_date_label_text);
        cur_update_text = (TextView) findViewById(R.id.cur_update_text);

        am_online = isNetworkAvailable();
        if (!am_online) {
            show_not_online_err();
            prefs.edit().putBoolean(SettingsConfig.IS_ONLINE, false).apply();
        } else {
            prefs.edit().putBoolean(SettingsConfig.IS_ONLINE, true).apply();
        }

        cur_balance = (TextView) findViewById(R.id.cur_balance_text);
        cur_due_date = (TextView) findViewById(R.id.cur_payment_text);
        cur_name = (TextView) findViewById(R.id.name_text);
        lastUpdateTv = (TextView) findViewById(R.id.last_update_textview); //TODO

        //cbTv = (TextView) findViewById(R.id.balance);
        //last_cbTv = (TextView) findViewById(R.id.balance_last_time);
        //mpTv = (TextView) findViewById(R.id.payment);
        //last_mpTv = (TextView) findViewById(R.id.last_payment_time);
        welcomeTv = (TextView) findViewById(R.id.welcome);
        dateTv = (TextView) findViewById(R.id.date_text);
        payDueTv = (TextView) findViewById(R.id.cur_payment_text);

        /*
        btn_ort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, OutageMapActivity.class);
                //EditText editText = (EditText) findViewById(R.id.edit_message);
                //String message = editText.getText().toString();
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        /*
        Button btn_balance = (Button) findViewById(R.id.check_balance);
        btn_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_balance(SERVER, ACCOUNT, false);
            }
        });
        */

        Button btn_report_outage = (Button) findViewById(R.id.outage_btn);
        btn_report_outage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report_outage();
            }
        });

        Button btn_pay = (Button) findViewById(R.id.make_payment);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean logic_good = account_logic_check(false);
                if (logic_good) {
                    Intent intent = new Intent(HomeActivity.this, MakePaymentActivity.class);
                    startActivity(intent);
                }
            }
        });
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        check_kplc(KPLC_SERVER, ACCOUNT, true);
        //check_last_statement(SERVER, ACCOUNT, true);
        //check_balance(SERVER, ACCOUNT, true);


        showWelcome();
        update_ui();

        invalidateOptionsMenu();
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean account_logic_check(boolean headless) {
        if (annon && !headless) {
            show_not_logged_in_warning();
            return false;
        }
        if (!annon && !headless && !am_online) {
            show_sms_warning(SettingsConfig.CHECK_LAST_STATEMENT);
            return false;
        }
        if (am_online) {
            return true;
        }
        return false;
    }

    private void save_result_into_realm(JSONObject json, String account) {
        try {
            if (!json.getString("name").contains("Error")) {
                final Postpaid cur = new Postpaid();
                cur.setAccount(account);
                cur.setBalance(Double.valueOf(json.getString("amount")));
                String date_str = json.getString("due_date");
                cur.setMonth(getDateFromString(date_str));
                cur.setDueDate(getDateFromString(date_str));
                cur.setPayDate(getDateFromString(date_str));
                realm.beginTransaction();
                realm.where(Postpaid.class).equalTo("month", getDateFromString(date_str)).findAll().deleteAllFromRealm(); //hack for now...
                realm.commitTransaction();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(cur);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {

                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Date getDateFromString(String date) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void check_kplc(String SERVER, final String ACCOUNT, boolean headless) {
        boolean logic_good = account_logic_check(headless);
        if (logic_good && !is_prepaid) {
        Log.e("check kplc", "hit");
            String url = "http://"+SERVER;
            Log.e("url", url);
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String result = response.toString();
                            JSONObject json = null;
                            try {
                                String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(Calendar.getInstance().getTime());
                                json = new JSONObject(result);
                                prefs.edit().putString(SettingsConfig.BALANCE, json.getString("amount")).apply();
                                prefs.edit().putString(SettingsConfig.DUE_DATE, json.getString("due_date")).apply();
                                prefs.edit().putString(SettingsConfig.NAME, json.getString("name")).apply();
                                prefs.edit().putString(SettingsConfig.LAST_UPDATE, timeStamp).apply();
                                update_ui();
                                save_result_into_realm(json, ACCOUNT);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("resp", error.toString());
                }
            }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("account",ACCOUNT);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Singletons.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
        }
    }

    private void update_ui() {
        String name = prefs.getString(SettingsConfig.NAME, "");
        Log.e("name", name);
        if (name.length() != 0 && !name.contains("Error")) {
            cur_name.setVisibility(View.VISIBLE);
            cur_name.setText(name);
            cur_name.setTextSize(20);
        } else {
            cur_name.setVisibility(View.GONE);
        }

        String balance = prefs.getString(SettingsConfig.BALANCE, "");
        if (balance.length() != 0) {
            cur_balance.setVisibility(View.VISIBLE);
            cur_balance_label_text.setVisibility(View.VISIBLE);
            cur_balance.setText(balance);
        } else {
            cur_balance.setVisibility(View.GONE);
            cur_balance_label_text.setVisibility(View.GONE);
        }

        String due_date = prefs.getString(SettingsConfig.DUE_DATE, "");
        if (due_date.length() != 0) {
            cur_due_date.setVisibility(View.VISIBLE);
            cur_payment_label_text.setVisibility(View.VISIBLE);
            cur_due_date.setText(due_date);
        } else {
            cur_due_date.setVisibility(View.GONE);
            cur_payment_label_text.setVisibility(View.GONE);
        }

        String last_update = prefs.getString(SettingsConfig.LAST_UPDATE, "");
        if (last_update.length() != 0) {
            lastUpdateTv.setVisibility(View.VISIBLE);
            cur_update_text.setVisibility(View.VISIBLE);
            lastUpdateTv.setText(last_update);
        } else {
            lastUpdateTv.setVisibility(View.GONE);
            cur_update_text.setVisibility(View.GONE);
        }
    }


    private void report_outage() {
        if (am_online) {
            DialogFragment dialog = new ReportDialog();
            dialog.show(getSupportFragmentManager(), "ReportDialogFragment");
        } else {
            show_sms_warning(SettingsConfig.GRIDWATCH);
        }
    }

    private void showWelcome() {
        Calendar now = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("MMM d yyyy", Locale.ENGLISH);
        dateTv.setText(format.format(now.getTime()));
        int hour = now.get(Calendar.HOUR_OF_DAY);
        if (hour <= 12) {
            welcomeTv.setText((welcomeText[0]));
        } else if (hour <= 18) {
            welcomeTv.setText((welcomeText[1]));
        } else {
            welcomeTv.setText((welcomeText[2]));
        }
        lastUpdateTv.setText(format.format(now.getTime()));

        if (annon) {
            cur_name.setText("");
            cur_name.setVisibility(View.GONE);
            //cur_due_date.setText("Must log in to view");
            //cur_balance.setText("Must log in to view");
            cur_due_date.setVisibility(View.GONE);
            cur_balance.setVisibility(View.GONE);
            cur_balance_label_text.setVisibility(View.GONE);
            cur_payment_label_text.setVisibility(View.GONE);
            lastUpdateTv.setVisibility(View.GONE);
            cur_update_text.setVisibility(View.GONE);
        }
        if (!am_online) {
            cur_name.setText("");
            cur_name.setVisibility(View.GONE);
            //cur_due_date.setText("Must be online to view");
            //cur_balance.setText("Must be online to view");
            cur_due_date.setVisibility(View.GONE);
            cur_balance.setVisibility(View.GONE);
            cur_balance_label_text.setVisibility(View.GONE);
            cur_payment_label_text.setVisibility(View.GONE);
            lastUpdateTv.setVisibility(View.GONE);
            cur_update_text.setVisibility(View.GONE);
        }

    }
    private String getStringFromDate(Date date) {

        if (date == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return format.format(date);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        boolean logic_good = false;

        if (id == R.id.nav_balance) {
            logic_good = account_logic_check(false);
            if (logic_good) {
                launch_class(BalanceHistoryActivity.class);
            }
        }

        else if (id == R.id.nav_payment) {
            logic_good = account_logic_check(false);
            if (logic_good) {
                launch_class(MakePaymentActivity.class);
            }
        } else if (id == R.id.nav_token) {
            logic_good = account_logic_check(false);
            if (logic_good) {
                launch_class(BuyTokensActivity.class);
            }
        }
        /*else if (id == R.id.nav_usage) {
            logic_good = account_logic_check(false);
            if (logic_good) {
                launch_class(UsageChartsActivity.class);
            }
        } */else if (id == R.id.nav_outage_map) {
            if (am_online) {
                launch_class(OutageMapActivity.class);
            } else {
                show_not_online_err();
            }
        } else if (id == R.id.nav_newsfeed) {
            if (am_online) {
                launch_class(NewsfeedActivity.class);
            } else {
                show_not_online_err();
            }
        }  else if (id == R.id.nav_settings) {
            launch_class(SettingsActivity.class);
        }
        /*else if (id == R.id.nav_advanced_settings) {
            launch_class(SettingsAdvancedActivity.class);
        } else if (id == R.id.nav_developer_settings) {
            launch_class(SettingsDeveloperActivity.class);
        } */else if (id == R.id.nav_contact) {
            launch_class(ContactActivity.class);
        } else if (id == R.id.outage_report) {
            launch_class(OutageActivity.class);
        }
        else if (id == R.id.logout) {
            do_logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.e("onCreate", savedInstanceState.keySet().toString());
    }

    private void do_logout() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.edit().putString("setting_key_account_number", "").putString("setting_key_meter_number", "").apply();
        prefs.edit().putString(SettingsConfig.BALANCE, "").apply();
        prefs.edit().putString(SettingsConfig.DUE_DATE, "").apply();
        prefs.edit().putString(SettingsConfig.NAME, "").apply();
        prefs.edit().putString(SettingsConfig.LAST_UPDATE, "").apply();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
    }

    private void launch_class(Class to_launch) {
        Intent e = new Intent(this, to_launch);
        startActivity(e);
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


    private void show_not_logged_in_warning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.not_logged_in_warning);
        builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setNegativeButton(R.string.login, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                do_logout();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
                send_sms(sms_type);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void send_sms(String msg) {
        Log.e("send sms", msg);
    } //TODO


    private void parse_intent(Intent intent) {

        if (intent != null) {
            prefs.edit().putString(SettingsConfig.OTHER_ACCOUNT, "").apply();
            prefs.edit().putString(SettingsConfig.OTHER_ADDRESS, "").apply();
            prefs.edit().putString(SettingsConfig.OTHER_NAME, "").apply();
            prefs.edit().putString(SettingsConfig.OTHER_NOTES, "").apply();
            prefs.edit().putString(SettingsConfig.OTHER_LAT, "").apply();
            prefs.edit().putString(SettingsConfig.OTHER_LNG, "").apply();

            if (intent.getStringExtra(IntentConfig.OTHER_ACCOUNT) != null) {
                other_account = intent.getStringExtra(IntentConfig.OTHER_ACCOUNT);
                prefs.edit().putString(SettingsConfig.OTHER_ACCOUNT, other_account).apply();
                Log.e("gw: other_account", other_account);
            }
            if (intent.getStringExtra(IntentConfig.OTHER_ADDRESS) != null) {
                address = intent.getStringExtra(IntentConfig.OTHER_ADDRESS);
                prefs.edit().putString(SettingsConfig.OTHER_ADDRESS, address).apply();
                Log.e("gw: address", address);
            }
            if (intent.getStringExtra(IntentConfig.OTHER_NAME) != null) {
                name = intent.getStringExtra(IntentConfig.OTHER_NAME);
                prefs.edit().putString(SettingsConfig.OTHER_NAME, name).apply();
                Log.e("gw: name", name);
            }
            if (intent.getStringExtra(IntentConfig.OTHER_NOTES) != null) {
                notes = intent.getStringExtra(IntentConfig.OTHER_NOTES);
                prefs.edit().putString(SettingsConfig.OTHER_NOTES, notes).apply();
                Log.e("gw: notes", notes);
            }
            if (intent.getStringExtra(IntentConfig.OTHER_LAT) != null) {
                lat = intent.getStringExtra(IntentConfig.OTHER_LAT);
                prefs.edit().putString(SettingsConfig.OTHER_LAT, lat).apply();
                Log.e("gw: lat", lat);
            }
            if (intent.getStringExtra(IntentConfig.OTHER_LNG) != null) {
                lng = intent.getStringExtra(IntentConfig.OTHER_LNG);
                prefs.edit().putString(SettingsConfig.OTHER_LNG, lng).apply();
                Log.e("gw: lng", lng);
            }
        }

    };

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startGWService();
    }


    private void do_gridwatch() {
        //report_outage();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COURSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION);
        }
        GridWatch a = new GridWatch(this,iemi,String.valueOf(version_num), "MANUAL");
        a.run();
    }

    @Override
    public void onDialogReturnValue(Boolean result) {
        if (result) {
            do_gridwatch();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("google api", "connection err: " + connectionResult.getErrorMessage());
    }

    public void initPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_INTERNET);
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_WIFI_STATE);
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WAKE_LOCK)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WAKE_LOCK},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_WAKE_LOCK);
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECEIVE_BOOT_COMPLETED)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_RECEIVE_BOOT_COMPLETED);
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SYSTEM_ALERT_WINDOW)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_SYSTEM_ALERT_WINDOW);
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.VIBRATE},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_VIBRATE);
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    IntentConfig.PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case IntentConfig.PERMISSIONS_REQUEST_ACCESS_LOC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo!
                }
                return;
            }
        }
    }
}

