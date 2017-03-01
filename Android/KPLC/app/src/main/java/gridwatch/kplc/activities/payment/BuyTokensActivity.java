package gridwatch.kplc.activities.payment;

import android.content.Intent;
import android.os.Bundle;
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
    private static final String SERVER= "http://141.212.11.206:3100";
    private String ACCOUNT = "3202667";
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_token);

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
                        buyTokenBalanceTextView.setText(String.valueOf(requestFromRealm()));

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
                Intent intent = new Intent(BuyTokensActivity.this, HomeActivity.class);
                //EditText editText = (EditText) findViewById(R.id.edit_message);
                //String message = editText.getText().toString();
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });
    }
    private double requestFromRealm() {
        Date max = realm.where(Token.class).equalTo("account", ACCOUNT).maximumDate("time");
        Token item = realm.where(Token.class).equalTo("time", max).equalTo("account", ACCOUNT).findFirst();
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
