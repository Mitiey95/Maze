
package com.example.maze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static String USER_FILE = "user_file.txt";
    private final static String GUEST = "Guest";
    private final static String GUEST_MAIL = "Guest.mail";

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    Dialog dialogSignIn, dialogCreator;

    TextView tv_width, tv_length, tv_visibility, tv_speed;
    SeekBar sb_width, sb_length, sb_visibility, sb_speed;
    Button bt_start;
    CheckBox cb_visibility;

    GameImgView gameImgView;

    User user;

    int cols = 10, rows = 10, speed = 1, watch = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        tv_width = findViewById(R.id.tv_width);
        tv_length = findViewById(R.id.tv_length);
        tv_visibility = findViewById(R.id.tv_visibility);
        tv_speed = findViewById(R.id.tv_speed);
        sb_width = findViewById(R.id.sb_width);
        sb_length = findViewById(R.id.sb_length);
        sb_visibility = findViewById(R.id.sb_visibility);
        sb_speed = findViewById(R.id.sb_speed);
        bt_start = findViewById(R.id.bt_start);
        cb_visibility = findViewById(R.id.cb_visibility);

        gameImgView = (GameImgView) findViewById(R.id.gameImgView);

        dialogSignIn = new Dialog(this);
        dialogCreator = new Dialog(this);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Intent takeUser = getIntent();
        user = (User) takeUser.getSerializableExtra("user");

        try{ readUser(); }
        catch (Exception e){ }


        if(user == null){
            openSignInDialog();
        }
        else {
            View headerView = navigationView.getHeaderView(0);

            TextView tw_user_name = headerView.findViewById(R.id.tv_user_name);
            tw_user_name.setText(user.getUsername());

            TextView tv_user_email = headerView.findViewById(R.id.tv_user_email);
            tv_user_email.setText(""+user.getEmail());

            if(user.getUsername().equals(GUEST)){
                navigationView.getMenu().findItem(R.id.menu_edit).setVisible(false);
                navigationView.getMenu().findItem(R.id.menu_statistics).setVisible(false);
            }
        }

        setSeekBarListenerWidth();
        setSeekBarListenerLength();
        setSeekBarListenerVisibility();
        setSeekBarListenerSpeed();

        cb_visibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_visibility.isChecked()){
                    cb_visibility.setChecked(true);
                    cb_visibility.setText("Invisible maze");
                    tv_visibility.setVisibility(View.VISIBLE);
                    sb_visibility.setVisibility(View.VISIBLE);
                }
                else{
                    cb_visibility.setChecked(false);
                    cb_visibility.setText("Visible maze");
                    tv_visibility.setVisibility(View.INVISIBLE);
                    sb_visibility.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setSeekBarListenerWidth() {
        sb_width.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cols = progress * (20 - 2) / 100 + 2;
                tv_width.setText("Maze width: " + String.valueOf(cols));
                gameImgView.setCOLS(cols);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setSeekBarListenerLength() {
        sb_length.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rows = progress * (30 - 2) / 100 + 2;
                tv_length.setText("Maze length: " + String.valueOf(rows));
                gameImgView.setROWS(rows);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setSeekBarListenerVisibility() {
        sb_visibility.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                watch = (progress * (15 - 1)) / 100 + 1;
                tv_visibility.setText("Range overview: " + String.valueOf(watch));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setSeekBarListenerSpeed() {
        sb_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = (progress * (5 - 1)) / 100 + 1;
                tv_speed.setText("Player speed: " + String.valueOf(speed));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_statistics:
                Intent send_statistics = new Intent(this, Statistics.class);
                send_statistics.putExtra("user", user);
                startActivity(send_statistics);
                break;

            case R.id.menu_edit:
                Intent send_edit = new Intent(this, Edit.class);
                send_edit.putExtra("user", user);
                startActivity(send_edit);
                break;

            case R.id.menu_sign_out:
                deleteUser();
                openSignInDialog();
                break;

            case R.id.menu_tutorial:
                Intent send_tutorial = new Intent(this, Tutorial.class);
                send_tutorial.putExtra("user", user);
                startActivity(send_tutorial);
                break;

            case R.id.menu_settings:
                Intent send_settings = new Intent(this, Settings.class);
                send_settings.putExtra("user", user);
                startActivity(send_settings);
                break;

            case R.id.menu_creator:
                openCreatorDialog();
                break;

            case R.id.menu_exit:
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void deleteUser() {
        try {
            OutputStream outputStream = openFileOutput(USER_FILE,0);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write("");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openSignInDialog() {
        dialogSignIn.setContentView(R.layout.sign_in_dialog);
        dialogSignIn.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogSignIn.setCanceledOnTouchOutside(false);

        Button bt_guest = dialogSignIn.findViewById(R.id.bt_guest);
        bt_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent guest = new Intent(v.getContext(), MainActivity.class);

                user = new User(-1, GUEST, GUEST_MAIL, null, -1, null);

                guest.putExtra("user", user);
                startActivity(guest);
            }
        });

        Button bt_log_in = dialogSignIn.findViewById(R.id.bt_log_in);
        bt_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Login.class));
            }
        });

        Button bt_register = dialogSignIn.findViewById(R.id.bt_register);
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Registration.class));
            }
        });

        dialogSignIn.show();
    }

    private void openCreatorDialog() {
        dialogCreator.setContentView(R.layout.creator_dialog);
        dialogCreator.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        dialogCreator.show();
    }

    private void readUser() {
        try {
            InputStream inputStream = openFileInput(USER_FILE);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            int id = Integer.parseInt(bufferedReader.readLine());
            String username = bufferedReader.readLine();
            String email = bufferedReader.readLine();
            String password = bufferedReader.readLine();
            int question = Integer.parseInt(bufferedReader.readLine());
            String answer = bufferedReader.readLine();

            user = new User(id, username, email, password, question, answer);

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(View view) {
        Intent send = new Intent(this, Game.class);
        send.putExtra("cols", cols);
        send.putExtra("rows", rows);
        send.putExtra("speed", speed);

        if(!cb_visibility.isChecked()){
            send.putExtra("watch", cols * rows);
        }
        else {
            send.putExtra("watch", watch);
        }

        send.putExtra("user", user);
        startActivity(send);
    }
}