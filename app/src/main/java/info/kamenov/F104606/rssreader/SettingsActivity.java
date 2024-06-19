package info.kamenov.F104606.rssreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import info.kamenov.F104606.rssreader.db.DBManager;
import info.kamenov.F104606.rssreader.model.BBCFeeds;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            DBManager dbManager = new DBManager(this.getActivity());
            dbManager.open();
            Cursor cursor = dbManager.fetch();
            int count = cursor.getCount();
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (int i = 0; i < count; i++) {
                BBCFeeds bbcFeeds = BBCFeeds.valueOf(cursor.getString(1));
                String setting;
                switch (bbcFeeds) {
                    case TOP_STORIES:
                        setting = "bbc_top_stories";
                    case WORLD:
                        setting = "bbc_world";
                    case EUROPE:
                        setting = "bbc_europe";
                    case UK:
                        setting = "bbc_uk";
                    case BUSINESS:
                        setting = "bbc_business";
                    case POLITICS:
                        setting = "bbc_politics";
                    case HEALTH:
                        setting = "bbc_health";
                    case EDUCATION_AND_FAMILY:
                        setting = "bbc_education_and_family";
                    case SCIENCE_AND_ENVIRONMENT:
                        setting = "bbc_science_and_environment";
                    case TECHNOLOGY:
                        setting = "bbc_technology";
                    case ENTERTAINMENT_AND_ARTS:
                        setting = "bbc_entertainment_and_arts";
                    default:
                        setting = "";
                }
                editor.putBoolean(setting, cursor.getInt(2) == 1);
                cursor.moveToNext();
            }
            dbManager.close();
//            Preference bbcTopStories = findPreference("bbc_top_stories");
            editor.apply();
            //starts live change listener
//            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            System.out.println("test");//Your Code
        }
    }


}