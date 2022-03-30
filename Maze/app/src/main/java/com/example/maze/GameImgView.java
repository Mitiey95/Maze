package com.example.maze;

import android.content.Context;
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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class GameImgView extends View {

    private Cell[][] cells;
    int COLS = 10 , ROWS = 10;
    float WALL_THICKNESS = 10;

    private float cellSize, hMargin, vMargin;

    private Random random;

    private Paint wallPaint;

    int countSteps = 0;

    public GameImgView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);

        wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wallPaint.setColor(ContextCompat.getColor(context, R.color.black));
        wallPaint.setStrokeWidth(WALL_THICKNESS);

        random = new Random();

        createMaze();
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
        current. visited = true;

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

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        cellSize = width + height;
        while(cellSize * COLS >= width || cellSize * ROWS >= height)
        {
            cellSize--;
        }

        hMargin = (width - COLS * cellSize) / 2;
        vMargin = (height - ROWS * cellSize) / 2;

        canvas.translate(hMargin, vMargin);

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
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
        }
        canvas.translate(hMargin * -1, vMargin * -1);

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

}
