package gridwatch.kplc.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.config.SettingsConfig;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by guoxinyi on 1/18/17.
 */

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());;
        prefs.edit().putBoolean(SettingsConfig.ANNON, false).apply();


        boolean am_online = isNetworkAvailable();
        if (!am_online) {
            prefs.edit().putBoolean(SettingsConfig.ANNON, true).apply();
        }

        // if we are skipping, we set a field ANNON to true so that the text can reflect we don't have necessary information
        final Button skip_btn = (Button) findViewById(R.id.skip_btn);
        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_skip_warning();
            }
        });


        // Check stored preferences to see if we already have logged in, if so jump to home
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
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);



        loginSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = prefs.edit();
                if (loginPrePayRadioButton.isChecked()) {
                    check_if_valid_account(loginPrePayEditText.getText().toString());
                    editor.putString("setting_key_meter_number", loginPrePayEditText.getText().toString());
                }
                if (loginPostPayRadioButton.isChecked()) {
                    check_if_valid_account(loginPrePayEditText.getText().toString());
                    editor.putString("setting_key_account_number", loginPostPayEditText.getText().toString());
                }
                editor.apply();
                replaceActivityWithHome();
            }
        });
    }

    private void check_if_valid_account(String account_num) {

    }

    private void replaceActivityWithHome() {
        // This replaces our current activity, so the back button does the right thing
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    private void show_skip_warning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.skip_warning);
        builder.setPositiveButton(R.string.skip, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                prefs.edit().putBoolean(SettingsConfig.ANNON, true).apply();
                replaceActivityWithHome();
            }
        });
        builder.setNegativeButton(R.string.go_back, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
