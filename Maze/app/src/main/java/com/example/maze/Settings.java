package com.example.maze;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    User user;

    ImageButton musicStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        musicStatus = findViewById(R.id.musicStatus);

        Intent takeUser = getIntent();
        user = (User) takeUser.getSerializableExtra("user");

        if (isMyServiceRunning(MusicService.class)) {
            musicStatus.setImageResource(R.drawable.volume);
        } else {
            musicStatus.setImageResource(R.drawable.mute);
        }
    }

    public void back(View view) {
        Intent send = new Intent(this, MainActivity.class);
        send.putExtra("user", user);
        startActivity(send);
    }

    public void changeMusicStatus(View view) {
        if (isMyServiceRunning(MusicService.class)) {
            musicStatus.setImageResource(R.drawable.mute);
            stopService(new Intent(Settings.this, MusicService.class));
        } else {
            musicStatus.setImageResource(R.drawable.volume);
            startService(new Intent(Settings.this, MusicService.class));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}