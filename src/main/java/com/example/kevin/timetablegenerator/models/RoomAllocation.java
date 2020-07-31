package com.example.kevin.timetablegenerator.models;

public class RoomAllocation {
    public Room room;
    public int day;
    public Period period1;
    public Period period2;

    public RoomAllocation(Room room, int day, Period period1, Period period2) {
        this.room = room;
        this.day = day;
        this.period1 = period1;
        this.period2 = period2;
    }
}