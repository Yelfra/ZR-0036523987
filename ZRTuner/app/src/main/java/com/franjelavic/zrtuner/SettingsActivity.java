package com.franjelavic.zrtuner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.franjelavic.zrtuner.MainActivity.REFERENCE_PITCH;
import static com.franjelavic.zrtuner.MainActivity.SHARED_PREFS_FILE;

public class SettingsActivity extends AppCompatActivity {

    SeekBar refPitchSeekBar;
    TextView refPitchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        final SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
        int referencePitch = preferences.getInt(REFERENCE_PITCH, 440);

        refPitchSeekBar = (SeekBar) findViewById(R.id.pitchSeekbar);
        refPitchView = (TextView) findViewById(R.id.pitchView);

        refPitchSeekBar.setProgress(referencePitch);
        refPitchView.setText(referencePitch + " Hz");

        refPitchSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                refPitchView.setText(progress + " Hz");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(REFERENCE_PITCH, refPitchSeekBar.getProgress());
                editor.apply();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24));
        toolbar.setNavigationOnClickListener(button -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_from_left);
    }
}


