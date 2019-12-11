package com.example.john.moviesup;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout, new MoviesPreferenceFragment())
                .commit();

    }

    public static class MoviesPreferenceFragment extends PreferenceFragmentCompat  {


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {


            setPreferencesFromResource(R.xml.settings_main, rootKey);



        }

    }
}
