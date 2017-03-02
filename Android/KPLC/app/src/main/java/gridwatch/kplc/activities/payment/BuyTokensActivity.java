package gridwatch.kplc.activities.payment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.HomeActivity;
import gridwatch.kplc.activities.Singletons;
import gridwatch.kplc.activities.billing.Postpaid;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by guoxinyi on 1/19/17.
 */

public class BuyTokensActivity extends AppCompatActivity {
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_token);

        // NOTE: cannot call getApplicationContext() until _after_ super.onCreate(...)
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String application_host_server = prefs.getString("setting_key_application_host_server", "141.212.11.206");
        final String application_host_port = prefs.getString("setting_key_application_host_port", "3100");
        final String SERVER = "http://" + application_host_server + ":" + application_host_port;
        final String ACCOUNT = prefs.getString("setting_key_account_number", "3202667");
        final TextView buyTokenBalanceTextView = (TextView) findViewById(R.id.buyTokenBalanceTextView);
        final EditText buyTokenPurchaseEditText = (EditText) findViewById(R.id.buyTokenPurchaseEditText);
        final EditText buyTokenPinEditText = (EditText) findViewById(R.id.buyTokenPinEditText);
        final Button buyTokenConfirmButton = (Button) findViewById(R.id.buyTokenConfirmButton);

        // Look up the current token balance
        buyTokenBalanceTextView.setText("Loading...");
        // FIXME: Request a string response from the provided URL.
        String url = SERVER + "/token?account=" + ACCOUNT;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response.toString();
                        storeToRealm(result);
                        buyTokenBalanceTextView.setText(String.valueOf(requestFromRealm(ACCOUNT)));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("buy_tokens", error.toString());
                buyTokenBalanceTextView.setText("Error!");
            }
        });
        Singletons.getInstance(this).addToRequestQueue(stringRequest);

        buyTokenConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BuyTokensActivity.this);
                builder.setMessage("Please confirm your payment.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(BuyTokensActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    private double requestFromRealm(String account) {
        Date max = realm.where(Token.class).equalTo("account", account).maximumDate("time");
        Token item = realm.where(Token.class).equalTo("time", max).equalTo("account", account).findFirst();
        if (item == null) {
            return 0;
        }
        return item.getToken();

    }
    private void storeToRealm(String result) {
        if (result == null) {
            return;
        }
        try {
            JSONArray json = new JSONArray(result);
            int length = json.length();
            realm.beginTransaction();
            for(int i = 0; i < length; i++){//遍历JSONArray
                JSONObject oj = json.getJSONObject(i);
                String account = oj.getString("account");
                String time = oj.getString("date");
                String token = oj.getString("token");
                Token item = realm.createObject(Token.class); // Create a new object
                item.setAccount(account);
                item.setTime(getDateFromString(time));
                item.setToken(Double.parseDouble(token));

            }
            realm.commitTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private String getStringFromDate(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return format.format(date);
    }
    private Date getDateFromString(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
