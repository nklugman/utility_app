package gridwatch.kplc.activities.payment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.HomeActivity;
import gridwatch.kplc.activities.billing.Postpaid;
import io.realm.Realm;

/**
 * Created by guoxinyi on 1/19/17.
 */

public class MakePaymentActivity extends AppCompatActivity {
    private Realm realm;
    private TextView balanceTv;
    private TextView dueOnTv;
    private static final String SERVER= "http://141.212.11.206:3100";
    private String ACCOUNT = "3202667";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Button btn_pay = (Button) findViewById(R.id.confirm_button);
        balanceTv = (TextView) findViewById(R.id.currentbalancenumber);
        dueOnTv = (TextView) findViewById(R.id.currentdate);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MakePaymentActivity.this);
                builder.setMessage("Please confirm your payment")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(MakePaymentActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        realm = Realm.getDefaultInstance();
        Postpaid pay = realm.where(Postpaid.class).isNull("payDate").findFirst();
        if (pay != null) {
            balanceTv.setText(String.valueOf(pay.getBalance()));
            dueOnTv.setText(getStringFromDate(pay.getDueDate()));
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
                                balanceTv.setText(String.valueOf(oj.get("balance")));
                                dueOnTv.setText(String.valueOf(oj.get("dueDate")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("buy_tokens", error.toString());
                    balanceTv.setText("");
                }
            });
        }
    }
    private String getStringFromDate(Date date) {

        if (date == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return format.format(date);
    }
}