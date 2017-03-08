package gridwatch.kplc.activities.settings;

import gridwatch.kplc.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by ppannuto on 3/1/17.
 */

public class SettingsAdvancedActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_advanced);
        PreferenceCategory fakeHeader = new PreferenceCategory(this);

        /*
        // Watch for the developer checkbox and change visibility of that settings tab
        final CheckBoxPreference developerEnabled = (CheckBoxPreference) findPreference("EnableDeveloperMode");
        final NavigationView thisCantBeRight = (NavigationView) findViewById(R.id.nav_view);
        final Menu activityHomeDrawer = thisCantBeRight.getMenu();
        final MenuItem developerSettings = (MenuItem) activityHomeDrawer.findItem(R.id.nav_developer_settings);
        developerEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("foo", "HELLLOOOO");
                boolean enabled = (Boolean) newValue;
                if (enabled) {
                    developerSettings.setVisible(true);
                } else {
                    developerSettings.setVisible(false);
                }
                return true;
            }
        });
        */
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
