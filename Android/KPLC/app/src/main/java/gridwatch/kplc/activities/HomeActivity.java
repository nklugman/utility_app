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

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import gridwatch.kplc.R;
import gridwatch.kplc.activities.billing.PostPaidActivity;
import gridwatch.kplc.activities.billing.PrepaidActivity;
import gridwatch.kplc.activities.outage.OutageMapActivity;
import gridwatch.kplc.activities.outage.ReportOutageActivity;
import gridwatch.kplc.activities.outage.ReportRestorationActivity;
import gridwatch.kplc.activities.social_media.FacebookActivity;
import gridwatch.kplc.activities.social_media.GooglePlusActivity;
import gridwatch.kplc.activities.social_media.LinkedInActivity;
import gridwatch.kplc.activities.social_media.Twitter.TwitterActivity;
import io.fabric.sdk.android.Fabric;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "DyAJEKsgPQ6L0is9rOlSzpWQb";
    private static final String TWITTER_SECRET = "hsP2rm9qHhBhoXBJmQ23gQwjdFaafulPAzlPm84atc99cYP3KM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
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


        if (id == R.id.nav_prepaid) {
            launch_class(PrepaidActivity.class);
        } else if (id == R.id.nav_postpaid) {
            launch_class(PostPaidActivity.class);
        } else if (id == R.id.nav_report_outage) {
            launch_class(ReportOutageActivity.class);
        } else if (id == R.id.nav_report_restoration) {
            launch_class(ReportRestorationActivity.class);
        } else if (id == R.id.nav_map) {
            launch_class(OutageMapActivity.class);
        } else if (id == R.id.nav_facebook) {
            launch_class(FacebookActivity.class);
        } else if (id == R.id.nav_twitter) {
            launch_class(TwitterActivity.class);
        } else if (id == R.id.nav_google_plus) {
            launch_class(GooglePlusActivity.class);
        } else if (id == R.id.nav_linkedin) {
            launch_class(LinkedInActivity.class);
        } else if (id == R.id.nav_share) {
            launch_class(ShareActivity.class);
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
