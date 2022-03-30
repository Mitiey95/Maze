package com.example.maze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Game extends AppCompatActivity {

    GameView gameView;

    static int cols;
    static int rows;
    static int speed;
    static int watch;

    static User user;

    static Dialog winDialog;

    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        context = this;

        gameView = findViewById(R.id.gameView);

        winDialog = new Dialog(this);

        Intent taker = getIntent();
        cols = taker.getIntExtra("cols", 0);
        rows = taker.getIntExtra("rows", 0);
        speed = taker.getIntExtra("speed", 0);
        watch = taker.getIntExtra("watch", 0);

        user = (User) taker.getSerializableExtra("user");

        gameView.setCOLS(cols);
        gameView.setROWS(rows);
        gameView.setPLAYER_STEPS(speed);
        gameView.setPLAYER_WATCH(watch);

    }

    public static void openWinDialog(int milliseconds) {
        winDialog.setContentView(R.layout.win_dialog);
        winDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        winDialog.setCanceledOnTouchOutside(false);

        String time = "";
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) - (minutes * 60);
        int hours = minutes / 60;
        milliseconds = (milliseconds - ((hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000))) / 10;

        if(hours < 10){
            time += "0";
        }
        time += hours + ":";

        if(minutes < 10){
            time += "0";
        }
        time += minutes + ":";

        if(seconds < 10){
            time += "0";
        }
        time += seconds + ":";

        if(milliseconds < 10){
            time += "0";
        }
        time += milliseconds;

        TextView tv_stopwatch = winDialog.findViewById(R.id.tv_stopwatch);
        tv_stopwatch.setText(time);

        ImageButton bt_play = winDialog.findViewById(R.id.ib_play);
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send = new Intent(context, MainActivity.class);
                send.putExtra("user", Game.user);
                context.startActivity(send);
            }
        });

        ImageButton bt_again = winDialog.findViewById(R.id.ib_again);
        bt_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send = new Intent(context, Game.class);
                send.putExtra("user", Game.user);
                send.putExtra("cols", cols);
                send.putExtra("rows", rows);
                send.putExtra("speed", speed);
                send.putExtra("watch", watch);

                context.startActivity(send);
            }
        });

        ImageButton bt_upload = winDialog.findViewById(R.id.ib_upload);
        if(Game.user.getId() == -1){
            bt_upload.setVisibility(View.INVISIBLE);
        }
        String finalTime = time;
        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeResultsDB(Game.user.getId(), rows, cols, speed, watch, finalTime);
                Toast.makeText(context, "Achievement saved", Toast.LENGTH_LONG).show();
                bt_upload.setVisibility(View.INVISIBLE);
            }
        });

        winDialog.show();
    }

    public static void writeResultsDB(int id, int rows, int cols, int speed, int watch, String time){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database;

        database = dbHelper.getWritableDatabase();

        TimeZone tz = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(tz);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String date = sdf.format(calendar.getTime());

        ContentValues cv = new ContentValues();
        cv.put(DBHelper.OWNER_ID, id);
        cv.put(DBHelper.DATE, date);
        cv.put(DBHelper.MAZE_ROWS, rows);
        cv.put(DBHelper.MAZE_COLUMNS, cols);
        cv.put(DBHelper.PLAYER_SPEED, speed);
        cv.put(DBHelper.PLAYER_WATCH, watch);
        cv.put(DBHelper.TIME_PASS, time);
        database.insert(DBHelper.STATISTIC_TABLE, null, cv);

        database.close();
    }
}