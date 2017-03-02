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
import android.widget.RadioGroup;
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
    private RadioGroup paymentRg;
    private EditText amountEt;
    private final int DEFAULT_RADIOBUTTON_ID = 2131624090;
    private final int CUSTOM_RADIOBUTTON_ID = 2131624093;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String application_host_server = prefs.getString("setting_key_application_host_server", "141.212.11.206");
        final String application_host_port = prefs.getString("setting_key_application_host_port", "3100");
        final String SERVER = "http://" + application_host_server + ":" + application_host_port;
        final String ACCOUNT = prefs.getString("setting_key_account_number", "3202667");
        setContentView(R.layout.activity_payment);
        Button btn_pay = (Button) findViewById(R.id.confirm_button);
        balanceTv = (TextView) findViewById(R.id.currentbalancenumber);
        dueOnTv = (TextView) findViewById(R.id.currentdate);
        paymentRg = (RadioGroup) findViewById(R.id.paymentRadioGroup);
        amountEt = (EditText) findViewById(R.id.amount);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MakePaymentActivity.this);
                int checked = paymentRg.getCheckedRadioButtonId();
                String payment = balanceTv.getText().toString();
                Log.i("checked",""+checked);
                if (checked == CUSTOM_RADIOBUTTON_ID) {
                    payment = amountEt.getText().toString();
                }
                builder.setMessage("Please confirm your payment: KSh "+payment+" to account "+ ACCOUNT)
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