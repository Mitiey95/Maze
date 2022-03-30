package com.example.maze;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;

public class Registration extends AppCompatActivity {

    private final static String USER_FILE = "user_file.txt";
    private final static String GUEST = "Guest";

    TextView tv_log_in;
    Button bt_register;
    EditText inputUsername, inputEmail, inputPassword, inputConformPassword, inputAnswer;
    Dialog registerDialog;
    ImageButton bt_show1, bt_show2;

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
        setContentView(R.layout.activity_registration);

        tv_log_in = findViewById(R.id.tv_log_in);
        bt_register = findViewById(R.id.bt_register);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConformPassword = findViewById(R.id.inputConformPassword);
        inputAnswer = findViewById(R.id.inputAnswer);
        s_question = findViewById(R.id.s_question);
        bt_show1 = findViewById(R.id.bt_show1);
        bt_show2 = findViewById(R.id.bt_show2);

        registerDialog = new Dialog(this);

        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputConformPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        tv_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Login.class));
            }
        });

        dbHelper=new DBHelper(this);
        database=dbHelper.getWritableDatabase();
        dbHelper.close();

        readDataBase();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, questions);
        s_question.setAdapter(adapter);

        s_question.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                question_index = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    public void sign_up(View view) {
        username = String.valueOf(inputUsername.getText());
        email = String.valueOf(inputEmail.getText());
        password = String.valueOf(inputPassword.getText());
        conform_password = String.valueOf(inputConformPassword.getText());
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
        else if(!password.equals(conform_password)){
            inputConformPassword.setError("The passwords must be equal");
        }
        else if(answer.equals("")){
            inputAnswer.setError("This field must be filled");
        }
        else{
            writeUsersDB(getLastUserId() + 1, username, email, password, question_index, answer);

            user = new User(getLastUserId() + 1, username, email, password, question_index, answer);

            openRegisterDialog();
        }

    }

    private void openRegisterDialog() {
        registerDialog.setContentView(R.layout.register_dialog);
        registerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        registerDialog.setCanceledOnTouchOutside(false);

        Button bt_guest = registerDialog.findViewById(R.id.bt_accept);
        bt_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser(user);

                Intent send = new Intent(v.getContext(), MainActivity.class);
                send.putExtra("user", user);
                startActivity(send);
            }
        });

        Button bt_log_in = registerDialog.findViewById(R.id.bt_refuse);
        bt_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send = new Intent(v.getContext(), MainActivity.class);
                send.putExtra("user", user);
                startActivity(send);
            }
        });

        registerDialog.show();
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

    public void writeUsersDB(int id, String username, String email, String password, int question, String answer){
        database = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBHelper.USERS_ID, id);
        cv.put(DBHelper.USERS_USERNAME, username);
        cv.put(DBHelper.USERS_EMAIL, email);
        cv.put(DBHelper.USERS_PASSWORD, password);
        cv.put(DBHelper.USERS_QUESTION_ID, question);
        cv.put(DBHelper.USERS_ANSWER, answer);
        database.insert(DBHelper.USERS_TABLE, null, cv);

        database.close();
    }

    public boolean checkUserExist(String username) {
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    public void back(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void showPass(View view) {
        if(inputPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
            inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            bt_show1.setImageResource(R.drawable.eye);
        }
        else{
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            bt_show1.setImageResource(R.drawable.no_eye);
        }
    }

    public void showConPass(View view) {
        if(inputConformPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
            inputConformPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            bt_show2.setImageResource(R.drawable.eye);
        }
        else{
            inputConformPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            bt_show2.setImageResource(R.drawable.no_eye);
        }
    }

    public int getLastUserId(){
        if(users.size() == 0){
            return 0;
        }
        return users.get(users.size() - 1).getId();
    }
}