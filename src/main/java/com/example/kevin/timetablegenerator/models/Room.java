package com.example.kevin.timetablegenerator.models;

public class Room {
    public int id;
    public int subject_id;
    public String name;

    public Subject subject;

    public Room(int id, int subject_id, String name, Subject subject) {
        this.id = id;
        this.subject_id = subject_id;
        this.name = name;
        this.subject = subject;
    }
}
