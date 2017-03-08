package gridwatch.kplc.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
    private TextView cbTv;
    private TextView last_cbTv;
    private TextView mpTv;
    private TextView last_mpTv;
    private TextView welcomeTv;
    private TextView dateTv;
    private TextView payDueTv;
    private String[] welcomeText = {"Good Morning", "Good Afternoon", "Good Evening"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String application_host_server = prefs.getString("setting_key_application_host_server", "141.212.11.206");
        final String application_host_port = prefs.getString("setting_key_application_host_port", "3100");
        final String SERVER = "http://" + application_host_server + ":" + application_host_port;
        final String ACCOUNT = prefs.getString("setting_key_account_number", "3202667");
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cbTv = (TextView) findViewById(R.id.balance);
        last_cbTv = (TextView) findViewById(R.id.balance_last_time);
        mpTv = (TextView) findViewById(R.id.payment);
        last_mpTv = (TextView) findViewById(R.id.last_payment_time);
        welcomeTv = (TextView) findViewById(R.id.welcome);
        dateTv = (TextView) findViewById(R.id.date);
        payDueTv = (TextView) findViewById(R.id.payment_due);
        showWelcome();

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
                check_balance(SERVER, ACCOUNT);
            }
        });
        Button btn_pay = (Button) findViewById(R.id.make_payment);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MakePaymentActivity.class);
                startActivity(intent);
            }
        });
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        check_last_statement(SERVER, ACCOUNT);
        check_balance(SERVER, ACCOUNT);


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

    private void check_last_statement(String SERVER, String ACCOUNT) {
        Log.e("check last statement", "hit");
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
                            mpTv.setText(String.valueOf(oj.get("balance")));
                            payDueTv.setText(String.valueOf(oj.get("dueDate")));
                            last_mpTv.setText(timeStamp);
                            Log.e("check_last_statement", timeStamp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("buy_tokens", error.toString());
                mpTv.setText("");
                Postpaid pay = realm.where(Postpaid.class).isNull("payDate").findFirst();
                if (pay != null) {
                    Log.i("mylogpay", pay.toString());
                    mpTv.setText(String.valueOf(pay.getBalance()));
                    payDueTv.setText(getStringFromDate(pay.getDueDate()));
                    Log.e("check_last_statement", pay.toString());
                    last_mpTv.setText(String.valueOf(pay.getMonth()));
                }
            }
        });
        Singletons.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
    }

    private void check_balance(String SERVER, String ACCOUNT) {
        Log.e("check balance", "hit");

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
                            cbTv.setText(String.valueOf(oj.get("balance")));
                            Log.e("check balance", timeStamp);
                            last_cbTv.setText(timeStamp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "Not Connected to the Network! Many functionalities will not work!", Toast.LENGTH_SHORT);
                Log.e("buy_tokens", error.toString());
                cbTv.setText("");
                Date max = realm.where(Postpaid.class).maximumDate("month");
                if (max != null) {
                    Log.i("mylogmax", max.toString());
                    Postpaid balance = realm.where(Postpaid.class).equalTo("month", max).findFirst();
                    cbTv.setText(String.valueOf(balance.getBalance()));
                    last_cbTv.setText(String.valueOf(balance.getMonth()));
                }
            }
        });
        Singletons.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
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


        if (id == R.id.nav_balance) {
            launch_class(BalanceHistoryActivity.class);
        }
        /*else if (id == R.id.nav_statement) {
            launch_class(StatementHistoryActivity.class);
        }
        */
        else if (id == R.id.nav_payment) {
            launch_class(MakePaymentActivity.class);
        } else if (id == R.id.nav_token) {
            launch_class(BuyTokensActivity.class);
        } else if (id == R.id.nav_usage) {
            launch_class(UsageChartsActivity.class);
        } else if (id == R.id.nav_outage_map) {
            launch_class(OutageMapActivity.class);
        } else if (id == R.id.nav_newsfeed) {
            launch_class(NewsfeedActivity.class);
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

}
