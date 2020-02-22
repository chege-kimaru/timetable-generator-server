package com.example.kevin.timetablegenerator.models;

public class Subject {
    public int id;
    public int subject_group_id;
    public String name;
    public boolean hasLab;

    public SubjectGroup group;

    public Subject(int id, int subject_group_id, SubjectGroup group, String name, boolean hasLab) {
        this.id = id;
        this.subject_group_id = subject_group_id;
        this.group = group;
        this.name = name;
        this.hasLab = hasLab;
    }
}