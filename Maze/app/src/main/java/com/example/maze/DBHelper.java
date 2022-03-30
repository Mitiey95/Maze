package com.example.maze;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String SQL_FILE = "users_database.db";

    public static final String USERS_TABLE="Users";

    public static final String USERS_ID="Id";
    public static final String USERS_USERNAME="Username";
    public static final String USERS_EMAIL="Email";
    public static final String USERS_PASSWORD="Password";
    public static final String USERS_QUESTION_ID="Question";
    public static final String USERS_ANSWER="Answer";

    public static final String STATISTIC_TABLE="Statistic";

    public static final String OWNER_ID="Id";
    public static final String DATE="Date";
    public static final String MAZE_ROWS="Rows";
    public static final String MAZE_COLUMNS="Columns";
    public static final String PLAYER_SPEED="Speed";
    public static final String PLAYER_WATCH="Watch";
    public static final String TIME_PASS="Time";

    public DBHelper(Context context) {
        super(context, SQL_FILE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String st="CREATE TABLE "+USERS_TABLE;
        st+=" ( "+USERS_ID+" TEXT, ";
        st+=USERS_USERNAME+" TEXT, ";
        st+=USERS_EMAIL+" TEXT, ";
        st+=USERS_PASSWORD+" TEXT, ";
        st+=USERS_QUESTION_ID+" TEXT, ";
        st+=USERS_ANSWER+" TEXT);";

        db.execSQL(st);

        st="CREATE TABLE "+STATISTIC_TABLE;
        st+=" ( "+OWNER_ID+" TEXT, ";
        st+=DATE+" TEXT, ";
        st+=MAZE_ROWS+" TEXT, ";
        st+=MAZE_COLUMNS+" TEXT, ";
        st+=PLAYER_SPEED+" TEXT, ";
        st+=PLAYER_WATCH+" TEXT, ";
        st+=TIME_PASS+" TEXT);";

        db.execSQL(st);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
