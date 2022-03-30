package com.example.maze;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Stack;

public class GameView extends View {

    private Cell[][] cells;
    int COLS = 10, ROWS = 10;
    float WALL_THICKNESS = 10;
    float EXIT_MARGIN = 0;
    float PLAYER_SIZE = 0;
    float PLAYER_STEPS;
    int PLAYER_WATCH;

    private float cellSize, hMargin, vMargin, speed;
    boolean start = true, startStopwatch = false;

    private Random random;
    private Handler handler = new Handler();

    private Paint wallPaint, playerPaint, visitCell, exit;

    float x1 = 0;
    float y1 = 0;

    float playerPosX;
    float playerPosY;

    int countSteps = 0;
    int maxSteps = 0;
    Cell farestCell = new Cell(0,0 ,0);
    Cell playerCell = new Cell( 0, 0, 0);

    int width;
    int height;

    Context finalContext;

    Calendar start_time;

    public GameView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);

        finalContext = context;

        wallPaint = new Paint();
        wallPaint.setColor(ContextCompat.getColor(context, R.color.black));
        wallPaint.setStrokeWidth(WALL_THICKNESS);

        playerPaint = new Paint();
        playerPaint.setColor(ContextCompat.getColor(context, R.color.red));
        playerPaint.setStrokeWidth(WALL_THICKNESS);

        visitCell = new Paint();
        visitCell.setColor(ContextCompat.getColor(context, R.color.green));
        visitCell.setStrokeWidth(WALL_THICKNESS);

        exit = new Paint();
        exit.setColor(Color.BLUE);
        exit.setStrokeWidth(20);
        exit.setStyle(Paint.Style.STROKE);

        random = new Random();

        createMaze();
    }

    private class Cell{
        boolean
                topWall = true, leftWall = true, bottomWall = true, rightWall = true, visited = false, visible = false;
        int col, row, stepsTo;

        public Cell(int col, int row, int stepsTo)
        {
            this.col = col;
            this.row = row;
            this.stepsTo = stepsTo;
        }
    }

    private void createMaze() {
        Stack<Cell> stack = new Stack<>();
        Cell current, next;

        cells = new Cell[COLS][ROWS];

        for (int x=0; x<COLS; x++)
        {
            for (int y=0; y<ROWS; y++)
            {
                cells[x][y] = new Cell(x, y ,0);
            }
        }

        current = cells[0][0];
        current.visited = true;

        do {
            next = getNeighbour(current);
            if (next != null) {
                removeWall(current, next);
                stack.push(current);
                current = next;
                current.visited = true;
                countSteps++;
                current.stepsTo = countSteps;
            } else {
                current = stack.pop();
                countSteps--;
            }
        }while (!stack.empty());

    }

    private Cell getNeighbour(Cell cell){
        ArrayList<Cell> neighbours = new ArrayList<>();

        // left neighbour
        if(cell.col > 0)
        {
            if(!cells[cell.col-1][cell.row].visited){
                neighbours.add(cells[cell.col-1][cell.row]);
            }
        }

        // top neighbour
        if(cell.row > 0)
        {
            if(!cells[cell.col][cell.row-1].visited){
                neighbours.add(cells[cell.col][cell.row-1]);
            }
        }

        // right neighbour
        if(cell.col < COLS-1)
        {
            if(!cells[cell.col+1][cell.row].visited){
                neighbours.add(cells[cell.col+1][cell.row]);
            }
        }

        // bottom neighbour
        if(cell.row < ROWS-1)
        {
            if(!cells[cell.col][cell.row+1].visited){
                neighbours.add(cells[cell.col][cell.row+1]);
            }
        }

        if(neighbours.size() > 0)
        {
            int index = random.nextInt(neighbours.size());
            return neighbours.get(index);
        }
        return  null;

    }

    private void removeWall(Cell current, Cell next) {
        // next in top
        if(current.col == next.col && current.row == next.row+1)
        {
            current.topWall = false;
            next.bottomWall = false;
        }

        // next in left
        if(current.col == next.col+1 && current.row == next.row)
        {
            current.leftWall = false;
            next.rightWall = false;
        }

        // next in right
        if(current.col == next.col-1 && current.row == next.row)
        {
            current.rightWall = false;
            next.leftWall = false;
        }

        // next in bottom
        if(current.col == next.col && current.row == next.row-1)
        {
            current.bottomWall = false;
            next.topWall = false;
        }
    }

    private boolean checkPlace(float x, float y) {
        int col = (int) (x / cellSize);
        int row = (int) (y / cellSize);

        try {
            if (cells[col][row].leftWall && x - PLAYER_SIZE <= col * cellSize) {
                return false;
            }
            if (cells[col][row].topWall && y - PLAYER_SIZE <= row * cellSize) {
                return false;
            }
            if (cells[col][row].rightWall && x + PLAYER_SIZE >= (col + 1) * cellSize) {
                return false;
            }
            if (cells[col][row].bottomWall && y + PLAYER_SIZE >= (row + 1) * cellSize) {
                return false;
            }
        }
        catch (Exception e){
            return false;
        }

        return true;
    }

    private void visibleCell(){
        cells[playerCell.col][playerCell.row].visible = true;

        Cell[][] tempCells = new Cell[COLS][ROWS];

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                tempCells[x][y] = new Cell(x , y, 0);
                tempCells[x][y].visible = cells[x][y].visible;
            }
        }

        for (int i = 0; i < PLAYER_WATCH; i++) {
            for (int x = 0; x < COLS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    if(tempCells[x][y].visible){
                        checkVisibleNeighbour(cells[x][y]);
                    }
                }
            }
            for (int x = 0; x < COLS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    tempCells[x][y].visible = cells[x][y].visible;
                }
            }

        }
    }

    private void checkVisibleNeighbour(Cell cell){
        try {
            if(!cell.topWall){
                cells[cell.col][cell.row - 1].visible = true;
            }
        }
        catch (Exception e){}

        try {
            if(!cell.rightWall){
                cells[cell.col + 1][cell.row].visible = true;
            }
        }
        catch (Exception e){}

        try {
            if(!cell.bottomWall){
                cells[cell.col][cell.row + 1].visible = true;
            }
        }
        catch (Exception e){}

        try {
            if(!cell.leftWall){
                cells[cell.col - 1][cell.row].visible = true;
            }
        }
        catch (Exception e){}
    }

    @Override
    protected void onDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();

        cellSize = width + height;
        while(cellSize * COLS >= width || cellSize * ROWS >= height - 350)
        {
            cellSize--;
        }

        EXIT_MARGIN = cellSize * 3 / 4;
        PLAYER_SIZE = cellSize / 4;
        speed = cellSize * PLAYER_STEPS / 10;

        hMargin = (width - COLS * cellSize) / 2;
        vMargin = (height - ROWS * cellSize) / 2;

        //Draw stop watch
        canvas.translate(0, 150);
        if(!startStopwatch){
            drawStopWatch(canvas, 0);
        }
        else {
            drawStopWatch(canvas, (int) (Calendar.getInstance().getTimeInMillis() - start_time.getTimeInMillis()));
        }

        //Draw back button
        canvas.translate(0, -120);
        Drawable d = getResources().getDrawable(R.drawable.back, null);
        d.setBounds(width * 11 / 12, 0, width, width / 12);
        d.draw(canvas);

        canvas.translate(hMargin, vMargin);

        if(start) {
            playerPosX = cellSize / 2;
            playerPosY = cellSize / 2;
            start = false;
        }

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                cells[x][y].visible = false;
            }
        }

        visibleCell();

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                if(cells[x][y].visible) {
                    if (cells[x][y].topWall) {
                        canvas.drawLine(
                                x * cellSize,
                                y * cellSize,
                                (x + 1) * cellSize,
                                y * cellSize,
                                wallPaint);
                    }

                    if (cells[x][y].leftWall) {
                        canvas.drawLine(
                                x * cellSize,
                                y * cellSize,
                                x * cellSize,
                                (y + 1) * cellSize,
                                wallPaint);
                    }

                    if (cells[x][y].bottomWall) {
                        canvas.drawLine(
                                x * cellSize,
                                (y + 1) * cellSize,
                                (x + 1) * cellSize,
                                (y + 1) * cellSize,
                                wallPaint);
                    }

                    if (cells[x][y].rightWall) {
                        canvas.drawLine(
                                (x + 1) * cellSize,
                                y * cellSize,
                                (x + 1) * cellSize,
                                (y + 1) * cellSize,
                                wallPaint);
                    }
                }

                if (cells[x][y].stepsTo > maxSteps) {
                    maxSteps = cells[x][y].stepsTo;
                    farestCell = cells[x][y];
                }
            }
        }

        // Draw Exit
        if(farestCell.visible){
            canvas.drawRect(farestCell.col * cellSize + EXIT_MARGIN,
                    farestCell.row * cellSize + EXIT_MARGIN,
                    (farestCell.col + 1) * cellSize - EXIT_MARGIN,
                    (farestCell.row + 1) * cellSize - EXIT_MARGIN,
                    exit);
        }

        // Draw Player
        drawPlayer(canvas, playerPosX, playerPosY, PLAYER_SIZE);

        playerCell.col = (int) (playerPosX / cellSize);
        playerCell.row = (int) (playerPosY / cellSize);

        if (cells[(int) (playerPosX / cellSize)][(int) (playerPosY / cellSize)] == farestCell) {
            Game.openWinDialog((int) (Calendar.getInstance().getTimeInMillis() - start_time.getTimeInMillis()));
            handler.removeCallbacks(runnableStopwatch);
        }

        canvas.translate(hMargin * -1, vMargin * -1);

    }

    public void drawPlayer(Canvas canvas, float x, float y, float width) {
        canvas.drawCircle(x ,
                y ,
                width, playerPaint);
    }

    public void drawStopWatch(Canvas canvas, int milliseconds){
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setTextSize(150);

        String time = "";
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) - (minutes * 60);
        int hours = minutes / 60;

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
        time += seconds;

        canvas.drawText(time, 0, 0, blackPaint);
    }

    private Runnable runnableMove = new Runnable() {
        @Override
        public void run() {
            float diff = (float) Math.sqrt(Math.pow(x1 - playerPosX, 2) + Math.pow(y1 - playerPosY, 2));
            float diffX = (x1 - playerPosX) / diff;
            float diffY = (y1 - playerPosY) / diff;

            if(checkPlace(playerPosX + speed * diffX, playerPosY)){
                playerPosX += speed * diffX;
            }

            if(checkPlace(playerPosX, playerPosY + speed * diffY)){
                playerPosY += speed * diffY;
            }

            handler.postDelayed(this, 1);
            invalidate();
        }
    };

    private Runnable runnableStopwatch = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 1000);

            invalidate();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x1 = event.getX();
        y1 = event.getY();

        if(x1 > width * 11 / 12 && y1 < width / 12){
            invalidate();
            Intent send = new Intent(finalContext, MainActivity.class);
            send.putExtra("user", Game.user);
            finalContext.startActivity(send);
        }

        if(!startStopwatch){
            startStopwatch = true;
            runnableStopwatch.run();
            start_time = Calendar.getInstance();
        }

        x1 = x1 - hMargin;
        y1 = y1 - vMargin;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                runnableMove.run();
                break;

            case MotionEvent.ACTION_MOVE:
                x1 = event.getX();
                y1 = event.getY();

                x1 = x1 - hMargin;
                y1 = y1 - vMargin;

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(runnableMove);
                break;
        }

        invalidate();
        return true;
    }

    public void setCOLS(int col){
        COLS = col;
        random = new Random();
        createMaze();
        invalidate();
    }

    public void setROWS(int row){
        ROWS = row;
        random = new Random();
        createMaze();
        invalidate();
    }

    public void setPLAYER_STEPS(int steps){
        PLAYER_STEPS = steps;
        invalidate();
    }

    public void setPLAYER_WATCH(int watch){
        PLAYER_WATCH = watch;
        invalidate();
    }

    public void stopStopwatch(){
        startStopwatch = false;
        invalidate();
    }
}
