package gridwatch.kplc.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.billing.BalanceHistoryActivity;
import gridwatch.kplc.activities.billing.Postpaid;
import gridwatch.kplc.activities.billing.UsageChartsActivity;
import gridwatch.kplc.activities.config.SettingsConfig;
import gridwatch.kplc.activities.news_feed.NewsfeedActivity;
import gridwatch.kplc.activities.outage_map.OutageMapActivity;
import gridwatch.kplc.activities.payment.BuyTokensActivity;
import gridwatch.kplc.activities.payment.MakePaymentActivity;
import gridwatch.kplc.activities.settings.SettingsActivity;
import gridwatch.kplc.activities.settings.SettingsAdvancedActivity;
import gridwatch.kplc.activities.settings.SettingsDeveloperActivity;
import io.realm.Realm;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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

    private boolean am_online;

    private TextView welcomeTv;
    private TextView dateTv;
    private TextView payDueTv;
    private String[] welcomeText = {"Good Morning", "Good Afternoon", "Good Evening"};
    private boolean annon;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String application_host_server = prefs.getString("setting_key_application_host_server", "graphs.grid.watch");
        final String application_host_port = prefs.getString("setting_key_application_host_port", "3100");
        final String kplc_host_port = prefs.getString("setting_key_kplc_host_port", "3331");
        final String SERVER = "http://" + application_host_server + ":" + application_host_port;
        final String KPLC_SERVER = "http://" + application_host_server + ":" + kplc_host_port;
        final String ACCOUNT = prefs.getString("setting_key_account_number", "3202667");
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        annon = prefs.getBoolean(String.valueOf(SettingsConfig.ANNON), false);

        cur_balance_label_text = (TextView) findViewById(R.id.cur_balance_label_text);
        cur_payment_label_text = (TextView) findViewById(R.id.cur_payment_date_label_text);


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
        //cbTv = (TextView) findViewById(R.id.balance);
        //last_cbTv = (TextView) findViewById(R.id.balance_last_time);
        //mpTv = (TextView) findViewById(R.id.payment);
        //last_mpTv = (TextView) findViewById(R.id.last_payment_time);
        welcomeTv = (TextView) findViewById(R.id.welcome);
        dateTv = (TextView) findViewById(R.id.date_text);
        payDueTv = (TextView) findViewById(R.id.cur_payment_text);

        /*
        btn_report.setOnClickListener(new View.OnClickListener() {
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button btn_balance = (Button) findViewById(R.id.check_balance);
        btn_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_balance(SERVER, ACCOUNT, false);
            }
        });

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
                boolean logic_good = accout_logic_check(false);
                if (logic_good) {
                    Intent intent = new Intent(HomeActivity.this, MakePaymentActivity.class);
                    startActivity(intent);
                }
            }
        });
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        check_kplc(KPLC_SERVER, ACCOUNT, true);
        check_last_statement(SERVER, ACCOUNT, true);
        check_balance(SERVER, ACCOUNT, true);

        showWelcome();

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

    private boolean accout_logic_check(boolean headless) {
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

    private void check_kplc(String SERVER, String ACCOUNT, boolean headless) {
        boolean logic_good = accout_logic_check(headless);
        if (logic_good) {
        Log.e("check last statement", "hit");
            String url = SERVER + "/?account=" + ACCOUNT;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String result = response.toString();
                            JSONArray json = null;
                            try {
                                String timeStamp = new SimpleDateFormat("yyyy/MM").format(Calendar.getInstance().getTime());
                                json = new JSONArray(result);
                                JSONObject oj = json.getJSONObject(0);
                                Log.e("resp", json.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("resp", error.toString());
                }
            });
            Singletons.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
        }
    }

    private void check_last_statement(String SERVER, String ACCOUNT, boolean headless) {
        Log.e("check last statement", "hit");

        boolean logic_good = accout_logic_check(headless);
        if (logic_good) {
            String url = SERVER + "/payment?account=" + ACCOUNT;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String result = response.toString();
                            JSONArray json = null;
                            try {
                                String timeStamp = new SimpleDateFormat("yyyy/MM").format(Calendar.getInstance().getTime());
                                json = new JSONArray(result);
                                JSONObject oj = json.getJSONObject(0);
                                //mpTv.setText(String.valueOf(oj.get("balance")));
                                payDueTv.setText(String.valueOf(oj.get("dueDate")));
                                //last_mpTv.setText(timeStamp);
                                Log.e("check_last_statement", timeStamp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("buy_tokens", error.toString());
                    //mpTv.setText("");
                    Postpaid pay = realm.where(Postpaid.class).isNull("payDate").findFirst();
                    if (pay != null) {
                        Log.i("mylogpay", pay.toString());
                        //mpTv.setText(String.valueOf(pay.getBalance()));
                        payDueTv.setText(getStringFromDate(pay.getDueDate()));
                        Log.e("check_last_statement", pay.toString());
                        //last_mpTv.setText(String.valueOf(pay.getMonth()));
                    }
                }
            });
            Singletons.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
        }

    }

    private void report_outage() {
        if (am_online) {
            do_gridwatch();
        } else {
            show_sms_warning(SettingsConfig.GRIDWATCH);
        }
    }

    private void check_balance(String SERVER, String ACCOUNT, boolean headless) {
        boolean logic_good = accout_logic_check(headless);
        if (logic_good) {
        String url = SERVER + "/balance?account=" + ACCOUNT;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String result = response.toString();
                            JSONArray json = null;
                            String timeStamp = new SimpleDateFormat("yyyy/MM").format(Calendar.getInstance().getTime());
                            try {
                                json = new JSONArray(result);
                                JSONObject oj = json.getJSONObject(0);
                                //cbTv.setText(String.valueOf(oj.get("balance")));
                                Log.e("check balance", timeStamp);
                                //last_cbTv.setText(timeStamp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(), "Not Connected to the Network! Many functionalities will not work!", Toast.LENGTH_SHORT);
                    Log.e("buy_tokens", error.toString());
//                cbTv.setText("");
                    Date max = realm.where(Postpaid.class).maximumDate("month");
                    if (max != null) {
                        Log.i("mylogmax", max.toString());
                        Postpaid balance = realm.where(Postpaid.class).equalTo("month", max).findFirst();
                        //cbTv.setText(String.valueOf(balance.getBalance()));
                        //last_cbTv.setText(String.valueOf(balance.getMonth()));
                    }
                }
            });
            Singletons.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
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

        if (annon) {
            cur_name.setText("");
            cur_name.setVisibility(View.GONE);
            //cur_due_date.setText("Must log in to view");
            //cur_balance.setText("Must log in to view");
            cur_due_date.setVisibility(View.GONE);
            cur_balance.setVisibility(View.GONE);
            cur_balance_label_text.setVisibility(View.GONE);
            cur_payment_label_text.setVisibility(View.GONE);
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


        boolean logic_good = accout_logic_check(false);

        if (id == R.id.nav_balance) {
            if (logic_good) {
                launch_class(BalanceHistoryActivity.class);
            }
        }
        /*else if (id == R.id.nav_statement) {
            launch_class(StatementHistoryActivity.class);
        }
        */
        else if (id == R.id.nav_payment) {
            if (logic_good) {
                launch_class(MakePaymentActivity.class);
            }
        } else if (id == R.id.nav_token) {
            if (logic_good) {
                launch_class(BuyTokensActivity.class);
            }
        } else if (id == R.id.nav_usage) {
            if (logic_good) {
                launch_class(UsageChartsActivity.class);
            }
        } else if (id == R.id.nav_outage_map) {
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
        } else if (id == R.id.nav_settings) {
            launch_class(SettingsActivity.class);
        } else if (id == R.id.nav_advanced_settings) {
            launch_class(SettingsAdvancedActivity.class);
        } else if (id == R.id.nav_developer_settings) {
            launch_class(SettingsDeveloperActivity.class);
        } else if (id == R.id.nav_contact) {
            launch_class(ContactActivity.class);
        } else if (id == R.id.logout) {
            do_logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void do_logout() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.edit().putString("setting_key_account_number", "").putString("setting_key_meter_number", "").apply();
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
    }

    private void do_gridwatch() {

    }
}

