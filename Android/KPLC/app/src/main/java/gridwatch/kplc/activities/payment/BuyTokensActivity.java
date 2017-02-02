package gridwatch.kplc.activities.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.HomeActivity;

/**
 * Created by guoxinyi on 1/19/17.
 */

public class BuyTokensActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_token);
        Button btn_pay = (Button) findViewById(R.id.confirm_button);
        btn_pay.setOnClickListener(new View.OnClickListener() {
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
