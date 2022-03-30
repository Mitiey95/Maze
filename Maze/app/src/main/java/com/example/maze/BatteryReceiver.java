package com.example.maze;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class BatteryReceiver extends BroadcastReceiver {

    private Handler handler = new Handler();
    private Context _context;

    @Override
    public void onReceive(Context context, Intent intent) {
        _context = context;
        int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

        if(batteryLevel <= 20){
            runnableAlert.run();
        }
        else {
            handler.removeCallbacks(runnableAlert);
        }
    }

    private final Runnable runnableAlert = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(_context, "Your Battery is Low", Toast.LENGTH_LONG).show();
            handler.postDelayed(this, 10000);
        }
    };
}
