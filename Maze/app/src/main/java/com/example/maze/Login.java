package com.example.maze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Login extends AppCompatActivity {

    private final static String USER_FILE = "user_file.txt";

    TextView tv_reg, tv_forgot;
    Button bt_sign_in;
    ImageButton bt_show;
    EditText inputName, inputPassword;
    CheckBox cb_remember;
    Dialog dialog;

    String name, password;

    ArrayAdapter<String> adapter;
    String[] questions = {
            "In what city was your father born?",
            "In what city was your first job?",
            "What is your favorite food?",
            "What is your mother's middle name?",
            "What was the name of your first pet?"
    };

    int question_index;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_reg = findViewById(R.id.tv_reg);
        tv_forgot = findViewById(R.id.tv_forgot);
        bt_sign_in = findViewById(R.id.bt_sign_in);
        bt_show = findViewById(R.id.bt_show);
        inputName = findViewById(R.id.inputName);
        inputPassword = findViewById(R.id.inputPassword);
        cb_remember = findViewById(R.id.cb_remember);

        dialog =  new Dialog(this);

        tv_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Registration.class));
            }
        });

        tv_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openResumePassDialog();
            }
        });

        dbHelper = new DBHelper(this);
        readDataBase();
    }

    private void openResumePassDialog() {
        dialog.setContentView(R.layout.resume_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        Spinner spinner = dialog.findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, questions);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                question_index = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        EditText inputName_d = dialog.findViewById(R.id.inputName);
        EditText inputAnswer_d = dialog.findViewById(R.id.inputAnswer);

        Button bt_ok = dialog.findViewById(R.id.button);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(inputName_d.getText()).equals("")){
                    inputName_d.setError("This field must be filled");
                }
                else if(String.valueOf(inputAnswer_d.getText()).equals("")){
                    inputAnswer_d.setError("This field must be filled");
                }
                else {
                    if(checkUserExist(String.valueOf(inputName_d.getText()))){
                        User user = getUserByName(String.valueOf(inputName_d.getText()));

                        if(user.getQuestion() != question_index || !user.getAnswer().equals(String.valueOf(inputAnswer_d.getText()))){
                            inputAnswer_d.setError("Wrong answer");
                        }
                        else {
                            inputName.setText(user.getUsername());
                            inputPassword.setText(user.getPassword());
                            dialog.cancel();
                        }
                    }
                    else{
                        inputName.setError("There is no such user");
                    }
                }
            }
        });

        dialog.show();
    }

    public void sign_in(View view) {
        name = String.valueOf(inputName.getText());
        password = String.valueOf(inputPassword.getText());

        if(name.equals("")){
            inputName.setError("This field must be filled");
        }
        else if(password.equals("")){
            inputPassword.setError("This field must be filled");
        }
        else {
            if(checkUserExist(name)){
                User user = getUserByName(name);

                if(cb_remember.isChecked()){
                    saveUser(user);
                }

                Intent send = new Intent(this, MainActivity.class);
                send.putExtra("user", user);
                startActivity(send);
            }
            else{
                inputName.setError("There is no such user");
            }
        }
    }

    private void saveUser(User user) {
        try {
            OutputStream outputStream = openFileOutput(USER_FILE,0);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            bufferedWriter.write(user.getId() + "\n");
            bufferedWriter.write(user.getUsername() + "\n");
            bufferedWriter.write(user.getEmail() + "\n");
            bufferedWriter.write(user.getPassword() + "\n");
            bufferedWriter.write(user.getQuestion() + "\n");
            bufferedWriter.write(user.getAnswer());

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readDataBase() {
        database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                DBHelper.USERS_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.USERS_ID)));
            String username = cursor.getString(cursor.getColumnIndex(DBHelper.USERS_USERNAME));
            String email = cursor.getString(cursor.getColumnIndex(DBHelper.USERS_EMAIL));
            String password = cursor.getString(cursor.getColumnIndex(DBHelper.USERS_PASSWORD));
            int question = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.USERS_QUESTION_ID)));
            String answer = cursor.getString(cursor.getColumnIndex(DBHelper.USERS_ANSWER));

            users.add(new User( id , username, email, password, question, answer));
            cursor.moveToNext();
        }

        dbHelper.close();
    }

    public boolean checkUserExist(String name) {
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUsername().equals(name) || users.get(i).getEmail().equals(name)){
                return true;
            }
        }
        return false;
    }

    private User getUserByName(String name) {
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUsername().equals(name) || users.get(i).getEmail().equals(name)){
                return users.get(i);
            }
        }
        return null;
    }

    public void back(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void showPass(View view) {
        if(inputPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
            inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            bt_show.setImageResource(R.drawable.eye);
        }
        else{
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            bt_show.setImageResource(R.drawable.no_eye);
        }
    }
}