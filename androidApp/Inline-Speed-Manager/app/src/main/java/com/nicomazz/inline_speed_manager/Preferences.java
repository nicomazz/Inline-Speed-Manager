package com.nicomazz.inline_speed_manager;
/**
 * Created by Nicolò Mazzucato (nicomazz97) on 30/11/16 16.50.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;


public class Preferences extends AppCompatActivity {
    static final private String TAG = "Preferences";

    static public void startSettings(Context context) {
        Intent i = new Intent(context, Preferences.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        getFragmentManager().beginTransaction().replace(R.id.content, new FilterPreferencesFragment()).commit();
    }


    public static class FilterPreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
            sharedPref.registerOnSharedPreferenceChangeListener(this);

        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.contains("low"))
                RunDetector.bestTime = Integer.parseInt(sharedPreferences.getString(key, "-1"));
            else if (key.contains("high"))
                RunDetector.worseTime = Integer.parseInt(sharedPreferences.getString(key, "-1"));
        }


    }

}