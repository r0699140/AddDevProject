package com.example.timerapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

import static com.example.timerapp.TimingContract.TimingEntry.CONTENT_URI;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final TimingDBHelper dbHelper = new TimingDBHelper(this.getContext());
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            Preference fillBtn = findPreference(getString(R.string.fill_db_key));
            if(fillBtn != null){
                final Context con = this.getContext();

                fillBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(con);
                        builder.setCancelable(true);
                        builder.setTitle("Are you sure?");
                        builder.setMessage("Are you sure you want to fill the database?");
                        builder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Calendar date = Calendar.getInstance();
                                        Calendar prevDate = Calendar.getInstance();
                                        int duration = 0;

                                        //Add records for today
                                        for(int j = 0; j < 4; j++){
                                            prevDate = (Calendar)date.clone();

                                            duration = new Random().nextInt(240)+20;

                                            date.add(Calendar.HOUR_OF_DAY, duration/60);
                                            date.add(Calendar.MINUTE, duration%60);

                                            // Get start of end date
                                            Calendar c = (Calendar) date.clone();

                                            c.set(Calendar.HOUR_OF_DAY, 0);
                                            c.set(Calendar.MINUTE, 0);
                                            c.set(Calendar.SECOND, 0);
                                            c.set(Calendar.MILLISECOND, 0);

                                            //new InsertAsync(timingDao).execute(new Timing(duration, prevDate.getTime(), date.getTime()));
                                            ContentValues newValues = new ContentValues();
                                            newValues.put(TimingContract.TimingEntry.COLUMN_NAME_DURATION, duration);
                                            newValues.put(TimingContract.TimingEntry.COLUMN_NAME_START, prevDate.getTime().getTime());
                                            newValues.put(TimingContract.TimingEntry.COLUMN_NAME_END, date.getTime().getTime());
                                            newValues.put(TimingContract.TimingEntry.COLUMN_NAME_DATE, c.getTime().getTime());

                                            db.insert(TimingContract.TimingEntry.TABLE_NAME, null, newValues);

                                            duration = new Random().nextInt(30)+10;
                                            date.add(Calendar.HOUR_OF_DAY, duration/60);
                                            date.add(Calendar.MINUTE, duration%60);
                                        }

                                        date = Calendar.getInstance();
                                        date.set(Calendar.MONTH, 1);
                                        date.set(Calendar.DAY_OF_MONTH, 0);
                                        date.add(Calendar.HOUR_OF_DAY, 0);
                                        date.add(Calendar.MINUTE, 0);

                                        //Add records on random days
                                        for(int i = 0; i < 6; i++){
                                            date.add(Calendar.MONTH, new Random().nextInt(2));
                                            date.add(Calendar.DAY_OF_MONTH, new Random().nextInt(15));

                                            for(int j = 0; j < 4; j++){
                                                prevDate = (Calendar)date.clone();

                                                duration = new Random().nextInt(240)+20;

                                                date.add(Calendar.HOUR_OF_DAY, duration/60);
                                                date.add(Calendar.MINUTE, duration%60);

                                                // Get start of end date
                                                Calendar c = (Calendar) date.clone();

                                                c.set(Calendar.HOUR_OF_DAY, 0);
                                                c.set(Calendar.MINUTE, 0);
                                                c.set(Calendar.SECOND, 0);
                                                c.set(Calendar.MILLISECOND, 0);

                                                ContentValues newValues = new ContentValues();
                                                newValues.put(TimingContract.TimingEntry.COLUMN_NAME_DURATION, duration);
                                                newValues.put(TimingContract.TimingEntry.COLUMN_NAME_START, prevDate.getTime().getTime());
                                                newValues.put(TimingContract.TimingEntry.COLUMN_NAME_END, date.getTime().getTime());
                                                newValues.put(TimingContract.TimingEntry.COLUMN_NAME_DATE, c.getTime().getTime());

                                                getContext().getContentResolver().insert(CONTENT_URI, newValues);

                                                duration = new Random().nextInt(30)+10;
                                                date.add(Calendar.HOUR_OF_DAY, duration/60);
                                                date.add(Calendar.MINUTE, duration%60);
                                            }
                                        }
                                    }
                                });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }
                });
            }

            Preference deleteBtn = findPreference(getString(R.string.delete_db_key));
            if(deleteBtn != null){
                final Context con = this.getContext();
                deleteBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(con);
                        builder.setCancelable(true);
                        builder.setTitle("Are you sure?");
                        builder.setMessage("Are you sure you want to delete all the records? (this cannot be undone)");
                        builder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //db.close();
                                        //getContext().deleteDatabase(TimingDBHelper.DATABASE_NAME);
                                        getContext().getContentResolver().delete(CONTENT_URI, null, null);
                                    }
                                });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }
                });
            }

        };


        @Override
        public void onResume() {
            super.onResume();

            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);

            EditTextPreference editTextPref = (EditTextPreference) findPreference("auto_start");

            String val = sharedPreferences.getString("auto_start", "0");
            Pattern num = Pattern.compile("\\d+");

            if(!num.matcher(val).matches() && !val.isEmpty())
                val = val + " is not a valid number, will not work!";

            if(editTextPref != null)
                editTextPref.setSummary(val);
        }

        @Override
        public void onPause(){
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        public void onDestroy() {
            PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity())).unregisterOnSharedPreferenceChangeListener(this);
            super.onDestroy();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = findPreference(key);

            if (pref instanceof EditTextPreference) {
                EditTextPreference etp = (EditTextPreference) pref;

                if(key.equals("auto_start")){
                    String val = etp.getText();
                    Pattern num = Pattern.compile("\\d+");

                    if(!val.isEmpty()){
                        if(!num.matcher(val).matches()){
                            pref.setSummary(val + " is not a valid number, will not work!");
                        }else{
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("auto_start_min", Integer.parseInt(val));
                            editor.apply();

                            pref.setSummary(val);
                        }
                    }else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("auto_start_min", 0);
                        editor.apply();
                        pref.setSummary("");
                    }

                }else{
                    pref.setSummary(etp.getText());
                }
            }
        }
    }

}