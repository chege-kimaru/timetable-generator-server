package com.example.kevin.timetablegenerator.models;

import java.time.LocalTime;

public class Period {
    public int id;
    public LocalTime from;
    public LocalTime to;
    public boolean toBreak;

    public Period(int id, LocalTime from, LocalTime to, boolean toBreak) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.toBreak = toBreak;
    }

    public Period(int id, String from, String to, boolean toBreak) {
        this.id = id;
        String[] fromArr = from.split(":");
        String[] toArr = to.split(":");
        this.from = LocalTime.of(Integer.parseInt(fromArr[0]), Integer.parseInt(fromArr[1]));
        this.to = LocalTime.of(Integer.parseInt(toArr[0]), Integer.parseInt(toArr[1]));
    }
}
