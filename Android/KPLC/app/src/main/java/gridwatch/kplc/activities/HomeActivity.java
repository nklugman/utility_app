package gridwatch.kplc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Random;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.billing.BalanceHistoryActivity;
import gridwatch.kplc.activities.billing.StatementHistoryActivity;
import gridwatch.kplc.activities.outage.OutageMapActivity;
import gridwatch.kplc.activities.payment.BuyTokensActivity;
import gridwatch.kplc.activities.payment.MakePaymentActivity;
import gridwatch.kplc.activities.social_media.SettingsActivity;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
