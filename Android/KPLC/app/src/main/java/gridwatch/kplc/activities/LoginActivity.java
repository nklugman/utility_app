package gridwatch.kplc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import gridwatch.kplc.R;
import io.realm.Realm;

/**
 * Created by guoxinyi on 1/18/17.
 */

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Wire up submit button
        Button submit = (Button) findViewById(R.id.login_submit);
        Realm.init(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceActivityWithHome();
            }
        });

        // Hide the specific entry fields by default
        final TextView loginPrePayTextView = (TextView) findViewById(R.id.loginPrePayTextView);
        final EditText loginPrePayEditText = (EditText) findViewById(R.id.loginPrePayEditText);
        loginPrePayTextView.setVisibility(View.GONE);
        loginPrePayEditText.setVisibility(View.GONE);

        final TextView loginPostPayTextView = (TextView) findViewById(R.id.loginPostPayTextView);
        final EditText loginPostPayEditText = (EditText) findViewById(R.id.loginPostPayEditText);
        loginPostPayTextView.setVisibility(View.GONE);
        loginPostPayEditText.setVisibility(View.GONE);

        // Wire up radio button selectors
        final RadioGroup loginPayRadioGroup = (RadioGroup) findViewById(R.id.loginPayRadioGroup);
        final RadioButton loginPrePayRadioButton = (RadioButton) findViewById(R.id.loginPrePayRadio);
        final RadioButton loginPostPayRadioButton = (RadioButton) findViewById(R.id.loginPostPayRadio);

        loginPayRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           public void onCheckedChanged(RadioGroup group, int checkedId) {
               // hide everything
               loginPrePayTextView.setVisibility(View.GONE);
               loginPrePayEditText.setVisibility(View.GONE);
               loginPostPayTextView.setVisibility(View.GONE);
               loginPostPayEditText.setVisibility(View.GONE);

               // and show what we care about now
               if (loginPrePayRadioButton.isChecked()) {
                   loginPrePayTextView.setVisibility(View.VISIBLE);
                   loginPrePayEditText.setVisibility(View.VISIBLE);
               }

               if (loginPostPayRadioButton.isChecked()) {
                   loginPostPayTextView.setVisibility(View.VISIBLE);
                   loginPostPayEditText.setVisibility(View.VISIBLE);
               }
           }
        });
    }

    private void replaceActivityWithHome() {
        // This replaces our current activity, so the back button does the right thing
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        LoginActivity.this.finish();
    }
}
