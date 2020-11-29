package com.rgame.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.rgame.tictactoe.Tools.AppVariables;
import com.rgame.tictactoe.Tools.OfflineData;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class SettingActivity extends AppCompatActivity {
    Switch musicMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/dumb2d.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        setContentView(R.layout.activity_setting);

        musicMode = (Switch) findViewById(R.id.gameSound);
        musicMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OfflineData offlineData = new OfflineData(SettingActivity.this);
                if(offlineData.getMusicMode().equals(AppVariables.MUSIC_MODE_ON)) musicMode.setChecked(true);

                if(offlineData.getMusicMode().equals(AppVariables.MUSIC_MODE_OFF)) musicMode.setChecked(false);

                if (isChecked) {
                    offlineData.setMusicMode(AppVariables.MUSIC_MODE_ON);
                    startService(new Intent(SettingActivity.this, BackgroundSoundService.class));
                } else {
                    offlineData.setMusicMode(AppVariables.MUSIC_MODE_OFF);
                    stopService(new Intent(SettingActivity.this, BackgroundSoundService.class));
                }
            }
        });
    }
}
