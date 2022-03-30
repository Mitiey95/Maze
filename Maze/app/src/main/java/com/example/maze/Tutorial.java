package com.example.maze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Tutorial extends AppCompatActivity {

    int[] images={R.drawable.t_width,
            R.drawable.t_length,
            R.drawable.t_maze_img,
            R.drawable.t_speed,
            R.drawable.t_visibale_maze,
            R.drawable.t_invisibale_maze,
            R.drawable.t_back,
            R.drawable.t_stopwatch,
            R.drawable.t_player,
            R.drawable.t_exit,
            R.drawable.t_time_pass,
            R.drawable.t_again,
            R.drawable.t_accept,
            R.drawable.t_save,
            R.drawable.t_statistics,
            R.drawable.t_result
    };

    String[] explanation={"The width bar set the number of columns in your maze.",
            "The length bar set the number of rows in your maze.",
            "Here  you can see the visual size of maze.",
            "The speed bar set the player speed in your maze.",
            "This option enable you see all the maze.",
            "Active this option will hide hlf of maze. You will see only cells in set overview.",
            "This button return you to the main menu.",
            "Here you see the time spent in the maze. The stopwatch starts when you touch screen.",
            "The red circle is player. It moves to point where you touch the screen.",
            "The blue square is the exit. When player touch it you win.",
            "In victory window you see time you take to pass maze.",
            "This button start new maze with same settings.",
            "This button return you to main menu.",
            "This button save you result to statistics.",
            "The icons show the settings of you maze. Rows, Columns, Speed and Overview.",
            "When you touch the result you can see time of passing and delete achievements."
    };

    User user;
    int page = 0;

    ImageView imageView;
    TextView textView;

    Button button_prev, button_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        button_prev = findViewById(R.id.button_prev);
        button_next = findViewById(R.id.button_next);

        Intent takeUser = getIntent();
        user = (User) takeUser.getSerializableExtra("user");

        imageView.setImageResource(images[page]);
        textView.setText(explanation[page]);

        button_prev.setVisibility(View.INVISIBLE);
    }

    public void back(View view) {
        Intent send = new Intent(this, MainActivity.class);
        send.putExtra("user", user);
        startActivity(send);
    }

    public void prev(View view) {
        if(page > 0){
            page--;
            button_next.setVisibility(View.VISIBLE);
            if(page == 0){
                button_prev.setVisibility(View.INVISIBLE);
            }
        }

        imageView.setImageResource(images[page]);
        textView.setText(explanation[page]);
    }

    public void next(View view) {
        if(page < images.length - 1){
            page++;
            button_prev.setVisibility(View.VISIBLE);
            if(page == images.length - 1){
                button_next.setVisibility(View.INVISIBLE);
            }
        }

        imageView.setImageResource(images[page]);
        textView.setText(explanation[page]);
    }
}