package gridwatch.kplc.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Map;

import gridwatch.kplc.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by guoxinyi on 1/18/17.
 */

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
<<<<<<< HEAD
        Button submit = (Button) findViewById(R.id.login_submit);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        submit.setOnClickListener(new View.OnClickListener() {
=======

        // Check stored preferences to see if we already have logged in, if so jump to home
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String account_number = prefs.getString("setting_key_account_number", "");
        String meter_number = prefs.getString("setting_key_meter_number", "");
        if (!account_number.equals("") || !meter_number.equals("")) {
            replaceActivityWithHome();
        }

        // Hide the specific entry fields by default
        final TextView loginPrePayTextView = (TextView) findViewById(R.id.loginPrePayTextView);
        final EditText loginPrePayEditText = (EditText) findViewById(R.id.loginPrePayEditText);
        loginPrePayTextView.setVisibility(View.GONE);
        loginPrePayEditText.setVisibility(View.GONE);

        final TextView loginPostPayTextView = (TextView) findViewById(R.id.loginPostPayTextView);
        final EditText loginPostPayEditText = (EditText) findViewById(R.id.loginPostPayEditText);
        loginPostPayTextView.setVisibility(View.GONE);
        loginPostPayEditText.setVisibility(View.GONE);

        // Set the button to be unclickable until there's something worth submitting
        final Button loginSubmitButton = (Button) findViewById(R.id.login_submit);
        loginSubmitButton.setEnabled(false);

        // Wire up radio button selectors
        final RadioGroup loginPayRadioGroup = (RadioGroup) findViewById(R.id.loginPayRadioGroup);
        final RadioButton loginPrePayRadioButton = (RadioButton) findViewById(R.id.loginPrePayRadio);
        final RadioButton loginPostPayRadioButton = (RadioButton) findViewById(R.id.loginPostPayRadio);

        loginPayRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           public void onCheckedChanged(RadioGroup group, int checkedId) {
               // hide the other and clear its text entry
               if (loginPrePayRadioButton.isChecked()) {
                   loginPostPayTextView.setVisibility(View.GONE);
                   loginPostPayEditText.setVisibility(View.GONE);
                   loginPostPayEditText.setText("");
                   loginPrePayTextView.setVisibility(View.VISIBLE);
                   loginPrePayEditText.setVisibility(View.VISIBLE);
               }

               if (loginPostPayRadioButton.isChecked()) {
                   loginPrePayTextView.setVisibility(View.GONE);
                   loginPrePayEditText.setVisibility(View.GONE);
                   loginPrePayEditText.setText("");
                   loginPostPayTextView.setVisibility(View.VISIBLE);
                   loginPostPayEditText.setVisibility(View.VISIBLE);
               }
           }
        });


        // Listen to text entry to validate meter/account numbers
        loginPrePayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Assume content invalid until proven valid. Start by disabling the button.
                loginSubmitButton.setEnabled(false);

                String s = editable.toString();
                if (s.length() != 0) {
                    try {
                        int meter = Integer.parseInt(s);

                        // TODO: Replace with real validation that this is a sane meter ID
                        if (meter > 0) {
                            loginSubmitButton.setEnabled(true);
                        }
                    } catch (NumberFormatException e) {
                        ;
                    }
                }
            }
        });

        loginPostPayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Assume content invalid until proven valid. Start by disabling the button.
                loginSubmitButton.setEnabled(false);

                String s = editable.toString();
                if (s.length() != 0) {
                    try {
                        int account = Integer.parseInt(s);

                        // TODO: Replace with real validation that this is a sane account ID
                        if (account > 0) {
                            loginSubmitButton.setEnabled(true);
                        }
                    } catch (NumberFormatException e) {
                        ;
                    }
                }
            }
        });


        // Wire up submit button
        Realm.init(this);
        loginSubmitButton.setOnClickListener(new View.OnClickListener() {
>>>>>>> 8979bd8be5e049660493fc97c5f36650d51ee394
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = prefs.edit();
                if (loginPrePayRadioButton.isChecked()) {
                    editor.putString("setting_key_meter_number", loginPrePayEditText.getText().toString());
                }
                if (loginPostPayRadioButton.isChecked()) {
                    editor.putString("setting_key_account_number", loginPostPayEditText.getText().toString());
                }
                editor.apply();
                replaceActivityWithHome();
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
