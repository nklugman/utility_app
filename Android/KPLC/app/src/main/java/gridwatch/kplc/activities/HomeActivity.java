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
import java.util.Random;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.billing.BalanceHistoryActivity;
import gridwatch.kplc.activities.billing.Postpaid;
import gridwatch.kplc.activities.billing.UsageChartsActivity;
import gridwatch.kplc.activities.outage.OutageMapActivity;
import gridwatch.kplc.activities.payment.BuyTokensActivity;
import gridwatch.kplc.activities.payment.MakePaymentActivity;
import io.realm.Realm;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Realm realm;
    private TextView cbTv;
    private TextView mpTv;
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
        mpTv = (TextView) findViewById(R.id.payment);
        welcomeTv = (TextView) findViewById(R.id.welcome);
        dateTv = (TextView) findViewById(R.id.date);
        payDueTv = (TextView) findViewById(R.id.payment_due);
        FloatingActionButton btn_report = (FloatingActionButton) findViewById(R.id.fab);
        showWelcome();
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
                String url = SERVER + "/balance?account=" + ACCOUNT;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String result = response.toString();
                                JSONArray json = null;
                                try {
                                    json = new JSONArray(result);
                                    JSONObject oj = json.getJSONObject(0);
                                    cbTv.setText(String.valueOf(oj.get("balance")));
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
                    }
                });
                Singletons.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
            }
        });
        Button btn_pay = (Button) findViewById(R.id.make_payment);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MakePaymentActivity.class);
                //EditText editText = (EditText) findViewById(R.id.edit_message);
                //String message = editText.getText().toString();
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });
        realm = Realm.getDefaultInstance();



        Date max = realm.where(Postpaid.class).maximumDate("month");
        if (max != null) {
            Log.i("mylogmax", max.toString());
            Postpaid balance = realm.where(Postpaid.class).equalTo("month", max).findFirst();
            cbTv.setText(String.valueOf(balance.getBalance()));
        }
        Postpaid pay = realm.where(Postpaid.class).isNull("payDate").findFirst();
        if (pay != null) {
            Log.i("mylogpay", pay.toString());
            mpTv.setText(String.valueOf(pay.getBalance()));
            payDueTv.setText(getStringFromDate(pay.getDueDate()));
        } else {
            String url = SERVER + "/payment?account=" + ACCOUNT;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String result = response.toString();
                            JSONArray json = null;
                            try {
                                json = new JSONArray(result);
                                JSONObject oj = json.getJSONObject(0);
                                mpTv.setText(String.valueOf(oj.get("balance")));
                                payDueTv.setText(String.valueOf(oj.get("dueDate")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("buy_tokens", error.toString());
                    mpTv.setText("");
                }
            });
        }

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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean randomly_fail() {
        Random r = new Random();
        return r.nextBoolean();
    }



    private void launch_class(Class to_launch) {
        Intent e = new Intent(this, to_launch);
        startActivity(e);
    }

}
