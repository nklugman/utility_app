package gridwatch.kplc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.billing.BalanceHistoryActivity;
import gridwatch.kplc.activities.billing.Postpaid;
import gridwatch.kplc.activities.billing.StatementHistoryActivity;
import gridwatch.kplc.activities.billing.UsageChartsActivity;
import gridwatch.kplc.activities.outage.OutageMapActivity;
import gridwatch.kplc.activities.payment.BuyTokensActivity;
import gridwatch.kplc.activities.payment.MakePaymentActivity;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Realm realm;
    private TextView cb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cb = (TextView) findViewById(R.id.balance);

        FloatingActionButton btn_report = (FloatingActionButton) findViewById(R.id.fab);
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
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button btn_balance = (Button) findViewById(R.id.check_balance);

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
        RealmResults<Postpaid> items = realm.where(Postpaid.class).equalTo("month", max).findAll();
        cb.setText(String.valueOf(items.first().getBalance()));
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



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_balance) {
            launch_class(BalanceHistoryActivity.class);
        } else if (id == R.id.nav_statement) {
            launch_class(StatementHistoryActivity.class);
        } else if (id == R.id.nav_payment) {
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

    public void test() {

    }

    private void launch_class(Class to_launch) {
        Intent e = new Intent(this, to_launch);
        startActivity(e);
    }

}
