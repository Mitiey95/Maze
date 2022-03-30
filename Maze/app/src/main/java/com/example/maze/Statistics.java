package com.example.maze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;

public class Statistics extends AppCompatActivity {

    ListView listView;
    ImageButton imageButton;

    User user;

    ArrayList<Result> results = new ArrayList<>();

    DBHelper sql;
    SQLiteDatabase database;

    Dialog resultDialog, shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        listView = findViewById(R.id.listView);
        imageButton = findViewById(R.id.imageButton);

        Intent takeUser = getIntent();
        user = (User) takeUser.getSerializableExtra("user");

        resultDialog = new Dialog(this);

        try {
            readDataBase();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(results.size() != 0) {
            StatisticAdapter statisticAdapter = new StatisticAdapter(Statistics.this, results);
            listView.setAdapter(statisticAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    openResult(position);
                }
            });
        }

    }

    private void openResult(int index) {
        resultDialog.setContentView(R.layout.result_dialog);
        resultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        resultDialog.setCanceledOnTouchOutside(true);

        TextView textView1 = resultDialog.findViewById(R.id.text_view1);
        TextView textView2 = resultDialog.findViewById(R.id.text_view2);
        TextView textView3 = resultDialog.findViewById(R.id.text_view3);
        TextView textView4 = resultDialog.findViewById(R.id.text_view4);
        TextView textView5 = resultDialog.findViewById(R.id.text_view5);

        textView1.setText("" + results.get(index).getTime());
        textView2.setText("" + results.get(index).getRows());
        textView3.setText("" + results.get(index).getColumns());
        textView4.setText("" + results.get(index).getSpeed());

        if(results.get(index).getWatch() >= results.get(index).getRows() * results.get(index).getColumns()){
            textView5.setText("all");
        }
        else {
            textView5.setText(""+  results.get(index).getWatch());
        }

        Button bt_ok = resultDialog.findViewById(R.id.bt_share);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShareDialog(index);
            }
        });

        Button bt_delete = resultDialog.findViewById(R.id.bt_delete);
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sql=new DBHelper(Statistics.this);
                database=sql.getWritableDatabase();
                database.delete(DBHelper.STATISTIC_TABLE,
                        DBHelper.OWNER_ID+"=? AND "
                                + DBHelper.DATE+"=? AND "
                                + DBHelper.MAZE_ROWS+"=? AND "
                                + DBHelper.MAZE_COLUMNS+"=? AND "
                                + DBHelper.PLAYER_SPEED+"=? AND "
                                + DBHelper.PLAYER_WATCH+"=? AND "
                                + DBHelper.TIME_PASS+"=?",
                        new String[]{String.valueOf(
                                user.getId()),
                                String.valueOf(results.get(index).getDate()),
                                String.valueOf(results.get(index).getRows()),
                                String.valueOf(results.get(index).getColumns()),
                                String.valueOf(results.get(index).getSpeed()),
                                String.valueOf(results.get(index).getWatch()),
                                String.valueOf(results.get(index).getTime())});

                database.close();

                Intent send = new Intent(v.getContext(), Statistics.class);
                send.putExtra("user", user);
                startActivity(send);
            }
        });

        resultDialog.show();
    }

    private void openShareDialog(int index) {
        shareDialog = new Dialog(this);

        shareDialog.setContentView(R.layout.share_dialog);
        shareDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        shareDialog.setCanceledOnTouchOutside(true);

        EditText inputNumber = shareDialog.findViewById(R.id.inputNumber);
        EditText inputText = shareDialog.findViewById(R.id.inputText);

        Result result = results.get(index);

        String text = "Game Date: " + result.getDate() + "\n" + "Columns - " + result.getColumns() + " " + "Rows" + result.getRows() + "\n" + "Speed - " + result.getSpeed() + " " + "Overview - " + result.getWatch() + "\n" + "Time Pass - " + result.getTime();

        inputText.setText(text);

        Button bt_ok = shareDialog.findViewById(R.id.button);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = String.valueOf(inputNumber.getText());
                String text = String.valueOf(inputText.getText());

                if(number.equals("")){
                    inputNumber.setError("This field must be filled");
                }
                else if(text.equals("")){
                    inputText.setError("This field must be filled");
                }
                else{
                    if(ContextCompat.checkSelfPermission(Statistics.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(number, null, text, null, null);
                        Toast.makeText(Statistics.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        ActivityCompat.requestPermissions(Statistics.this, new String[]{Manifest.permission.SEND_SMS}, 100);
                        Toast.makeText(Statistics.this, "Permission gotten", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        shareDialog.show();
    }

    private void readDataBase() throws ParseException {
        sql=new DBHelper(this);
        database=sql.getWritableDatabase();
        sql.close();

        database = sql.getReadableDatabase();

        Cursor cursor = database.query(
                DBHelper.STATISTIC_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.OWNER_ID)));
            String date = cursor.getString(cursor.getColumnIndex(DBHelper.DATE));
            int rows = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.MAZE_ROWS)));
            int cols = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.MAZE_COLUMNS)));
            int speed = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.PLAYER_SPEED)));
            int watch = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.PLAYER_WATCH)));
            String time = cursor.getString(cursor.getColumnIndex(DBHelper.TIME_PASS));

            if(user.getId() == id){
                results.add(new Result(id, date, rows, cols, speed, watch, time));
            }
            cursor.moveToNext();
        }
        sql.close();
    }

    public void back(View view) {
        Intent send = new Intent(this, MainActivity.class);
        send.putExtra("user", user);
        startActivity(send);
    }
}