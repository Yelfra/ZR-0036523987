package com.franjelavic.zrtuner;

import android.Manifest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements ListenerFragment.TaskCallbacks,
        AdapterView.OnItemSelectedListener {

    public static final int RECORD_AUDIO_PERMISSION = 0;
    public static final String SHARED_PREFS_FILE = "com.franjelavic.ZRTuner.PREFERENCE_FILE_KEY";

    public static final String CURRENT_TUNING = "current_tuning";
    protected static final String REFERENCE_PITCH = "reference_pitch";
    private static int tuningPosition = 0;
    private static int referencePitch;
    public static Tuning getCurrentTuning() {
        return TuningOption.getTuning(tuningPosition);
    }

    public static int getReferencePitch() {
        return referencePitch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestRecordAudioPermission();
        } else {
            startRecording();
        }

        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        setTuningOptions();
        setReferencePitch();

        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_from_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressUpdate(PitchDifference pitchDifference) {
        PitchMeter pitchMeter = this.findViewById(R.id.pitchMeter);

        pitchMeter.setPitchDifference(pitchDifference);
        pitchMeter.invalidate();
    }

    private static final String TAG_LISTENER_FRAGMENT = "listener_fragment";

    private void startRecording() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListenerFragment listenerFragment =
                (ListenerFragment) fragmentManager.findFragmentByTag(TAG_LISTENER_FRAGMENT);

        if (listenerFragment == null) {
            listenerFragment = new ListenerFragment();
            fragmentManager.beginTransaction()
                    .add(listenerFragment, TAG_LISTENER_FRAGMENT)
                    .commit();
        }
    }

    private void setReferencePitch() {
        final SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
        /**
         * SHARED_PREFS_FILE : the app generated file which stores key:value -> reference_pitch:hertz
         * MODE_PRIVATE : only accessible through this app
         */

        referencePitch = preferences.getInt(REFERENCE_PITCH, 440);
        /*
        Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(referencePitch), Toast.LENGTH_SHORT);
        toast.show();
        */
    }

    private void requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_AUDIO_PERMISSION);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_TUNING, position);
        editor.apply();

        tuningPosition = position;
        /*
        String tuningSelected = getResources().getStringArray(R.array.tuning_array)[position];
        Toast toast = Toast.makeText(getApplicationContext(), tuningSelected, Toast.LENGTH_SHORT);
        toast.show();
        */
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setTuningOptions() {
        final SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
        tuningPosition = preferences.getInt(CURRENT_TUNING, 0);

        Spinner spinner = findViewById(R.id.tuningMenu);

        String[] tuningOptions = getResources().getStringArray(R.array.tuning_array);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tuningOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(tuningPosition);
        spinner.setOnItemSelectedListener(this);
    }
}