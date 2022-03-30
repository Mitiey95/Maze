package com.example.maze;

public class Result {
    int id;
    String date;
    int rows;
    int columns;
    int speed;
    int watch;
    String time;

    public Result(int id, String date, int rows, int columns, int speed, int watch, String time) {
        this.id = id;
        this.date = date;
        this.rows = rows;
        this.columns = columns;
        this.speed = speed;
        this.watch = watch;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getWatch() {
        return watch;
    }

    public void setWatch(int watch) {
        this.watch = watch;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
