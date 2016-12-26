package com.example.ashis.entertainmentguide;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by ashis on 12/25/2016.
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_main);

        bindpreferencetoSummaryValue(findPreference(getString(R.string.pref_sort_key)));
    }

    private void bindpreferencetoSummaryValue(Preference preference) {

        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(),"poopularity"));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();



        if (preference instanceof ListPreference) {

            // For list preferences, look up the correct display value in

            // the preference's 'entries' list (since they have separate labels/values).

            ListPreference listPreference = (ListPreference) preference;

            int prefIndex = listPreference.findIndexOfValue(stringValue);

            if (prefIndex >= 0) {

                preference.setSummary(listPreference.getEntries()[prefIndex]);

            }

        } else {

            // For other preferences, set the summary to the value's simple string representation.

            preference.setSummary(stringValue);

        }

        return true;    }
}
