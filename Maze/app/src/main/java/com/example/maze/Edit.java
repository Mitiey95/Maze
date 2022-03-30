package com.example.maze;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Edit extends AppCompatActivity {

    private final static String USER_FILE = "user_file.txt";
    private final static String GUEST = "Guest";

    TextView tv_log_in;
    Button bt_register;
    EditText inputUsername, inputEmail, inputPassword, inputAnswer;
    ImageButton bt_show, bt_delete;
    CheckBox cb_remember;
    Spinner s_question;

    ArrayAdapter<String> adapter;
    String[] questions = {
            "In what city was your father born?",
            "In what city was your first job?",
            "What is your favorite food?",
            "What is your mother's middle name?",
            "What was the name of your first pet?"
    };

    int question_index = 0;
    String username, email, password, conform_password, answer;

    User user;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        tv_log_in = findViewById(R.id.tv_log_in);
        bt_register = findViewById(R.id.bt_register);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputAnswer = findViewById(R.id.inputAnswer);
        s_question = findViewById(R.id.s_question);
        bt_show = findViewById(R.id.bt_show);
        bt_delete = findViewById(R.id.bt_delete);
        cb_remember = findViewById(R.id.cb_remember);

        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        dbHelper=new DBHelper(this);
        database=dbHelper.getWritableDatabase();
        dbHelper.close();

        readDataBase();

        Intent takeUser = getIntent();
        user = (User) takeUser.getSerializableExtra("user");

        try{ readUser(); }
        catch (Exception e){ }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, questions);
        s_question.setAdapter(adapter);
        s_question.setSelection(user.getQuestion());

        s_question.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                question_index = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        inputUsername.setText(user.getUsername());
        inputEmail.setText(user.getEmail());
        inputPassword.setText(user.getPassword());
        inputAnswer.setText(user.getAnswer());

    }

    private void readUser() {
        try {
            InputStream inputStream = openFileInput(USER_FILE);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            int id = Integer.parseInt(bufferedReader.readLine());
            if(user.getId() == id){
                cb_remember.setChecked(true);
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void done(View view) {
        username = String.valueOf(inputUsername.getText());
        email = String.valueOf(inputEmail.getText());
        password = String.valueOf(inputPassword.getText());
        answer = String.valueOf(inputAnswer.getText());

        if(username.equals("")){
            inputUsername.setError("This field must be filled");
        }
        else if(checkUserExist(username) || username.equals(GUEST)){
            inputUsername.setError("This username already exist");
        }
        else if(email.equals("")){
            inputEmail.setError("This field must be filled");
        }
        else if(password.equals("")){
            inputPassword.setError("This field must be filled");
        }
        else if(answer.equals("")){
            inputAnswer.setError("This field must be filled");
        }
        else{
            database=dbHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(DBHelper.USERS_USERNAME, username);
            cv.put(DBHelper.USERS_EMAIL, email);
            cv.put(DBHelper.USERS_PASSWORD, password);
            cv.put(DBHelper.USERS_QUESTION_ID, question_index);
            cv.put(DBHelper.USERS_ANSWER, answer);

            database.update(DBHelper.USERS_TABLE, cv, DBHelper.USERS_USERNAME+"=?", new String[]{username});
            database.update(DBHelper.USERS_TABLE, cv, DBHelper.USERS_EMAIL+"=?", new String[]{email});
            database.update(DBHelper.USERS_TABLE, cv, DBHelper.USERS_PASSWORD+"=?", new String[]{password});
            database.update(DBHelper.USERS_TABLE, cv, DBHelper.USERS_QUESTION_ID+"=?", new String[]{String.valueOf(question_index)});
            database.update(DBHelper.USERS_TABLE, cv, DBHelper.USERS_ANSWER+"=?", new String[]{answer});

            database.close();

            if(cb_remember.isChecked()){
                saveUser(user);
            }

            Intent send = new Intent(this, MainActivity.class);

            user = new User(user.getId(), username, email, password, question_index, answer);
            send.putExtra("user", user);

            startActivity(send);
        }
    }

    public void readDataBase() {
        dbHelper=new DBHelper(this);
        database=dbHelper.getWritableDatabase();
        dbHelper.close();

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

    public boolean checkUserExist(String username) {
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUsername().equals(username) && !users.get(i).getUsername().equals(user.getUsername())){
                return true;
            }
        }
        return false;
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

    public void back(View view) {
        Intent send = new Intent(this, MainActivity.class);
        send.putExtra("user", user);
        startActivity(send);
    }

    public void delete(View view) {
        dbHelper=new DBHelper(Edit.this);
        database=dbHelper.getWritableDatabase();
        database.delete(DBHelper.USERS_TABLE, DBHelper.USERS_ID+"=?", new String[]{String.valueOf(user.getId())});
        database.delete(DBHelper.STATISTIC_TABLE, DBHelper.OWNER_ID+"=?", new String[]{String.valueOf(user.getId())});
        database.close();

        deleteUser();
        startActivity(new Intent(this, MainActivity.class));
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
}