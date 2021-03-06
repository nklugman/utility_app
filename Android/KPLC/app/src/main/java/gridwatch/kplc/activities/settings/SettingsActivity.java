package gridwatch.kplc.activities.settings;

import android.os.Bundle;

import gridwatch.kplc.R;

import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;


public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        PreferenceCategory fakeHeader = new PreferenceCategory(this);


        try {
            bindPreferenceSummaryToValue(findPreference("Account"));
            bindPreferenceSummaryToValue(findPreference("Meter"));

        }
        catch (java.lang.NullPointerException e) {

        }

    }
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static android.preference.Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new android.preference.Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(android.preference.Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            Log.d("update", stringValue);
            return true;
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
    private static void bindPreferenceSummaryToValue(android.preference.Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
        Log.d("updatelistener", preference.getKey());
    }
}



