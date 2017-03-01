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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.HomeActivity;
import gridwatch.kplc.activities.Singletons;

/**
 * Created by guoxinyi on 1/19/17.
 */

public class BuyTokensActivity extends AppCompatActivity {
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
        String url ="http://10.0.0.46:3000/api/v1/token_balance";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        buyTokenBalanceTextView.setText(response.toString());
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
}
