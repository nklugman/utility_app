package gridwatch.kplc.activities.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;

import gridwatch.kplc.R;


public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        PreferenceCategory fakeHeader = new PreferenceCategory(this);


        try {
            bindPreferenceSummaryToValue(findPreference("setting_key_account_number"));
            bindPreferenceSummaryToValue(findPreference("setting_key_meter_number"));
            bindPreferenceSummaryToValue(findPreference("Contact"));
            bindPreferenceSummaryToValue(findPreference("Phone"));
            bindPreferenceSummaryToValue(findPreference("Address"));
            bindPreferenceSummaryToValue(findPreference("power_outage_alerts"));



        }
        catch (java.lang.NullPointerException e) {

        }

    }
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            pref.setSummary(etp.getText());
        }
    }


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            try {
                String stringValue = value.toString();
                if (preference instanceof ListPreference) {
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);
                    preference.setSummary(
                            index >= 0
                                    ? listPreference.getEntries()[index]
                                    : null);

                } if (preference instanceof android.preference.EditTextPreference)  {
                    preference.setSummary(stringValue);
                }
                return true;
            } catch (java.lang.ClassCastException e) {
                Boolean boolValue = (Boolean) value;
                Log.e("ERROR", String.valueOf(boolValue));
                return true;
            }

            //Log.d(onPreferenceChangedTag, preference.toString());
            //Log.d(onPreferenceChangedTag, value.toString());


        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        if (preference instanceof android.preference.SwitchPreference)
        {

            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager.getDefaultSharedPreferences(
                            preference.getContext()).getBoolean(preference.getKey(),false));
        } else {

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

}



