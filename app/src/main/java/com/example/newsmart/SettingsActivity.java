package com.example.newsmart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean("dark_mode", false)) {

            setTheme(com.google.android.material.R.style.Base_Theme_Material3_Dark);

        }

        listner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                boolean toggled = sharedPreferences.getBoolean("dark_mode", false);

                Log.e("TAG", "onSharedPreferenceChanged:;");

                if (toggled) {

                    setTheme(com.google.android.material.R.style.Base_Theme_Material3_Dark);
                    recreate();
//                    new AlertDialog.Builder(MainActivity.this)
//                            .setMessage("Please restart the app to apply changes")
//                            .setPositiveButton("OK", null)
//                            .show();

                } else {

                    setTheme(com.google.android.material.R.style.Theme_AppCompat_DayNight_DarkActionBar);
                    recreate();

                }
            }
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsMartFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {


        SwitchPreference switchPreference = (SwitchPreference) findPreference("dark_mode");

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                Log.e("TAG", "onPreferenceChangesssssssssssss: " );
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                Log.e("TAG", "onPreferenceChange: " );
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}