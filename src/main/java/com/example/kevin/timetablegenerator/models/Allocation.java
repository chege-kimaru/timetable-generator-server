package com.example.kevin.timetablegenerator.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Allocation {
    //key = day value = list of periods
    public Map<Integer, List<Period>> periods;
    public int id;
    public Stream stream;
    public Subject subject;
    public Teacher teacher;
    public RoomAllocation room;

    public int stream_id;
    public int subject_id;
    public int teacher_id;

    public Allocation(int id, int stream_id, int subject_id, int teacher_id, Stream stream, Subject subject, Teacher teacher) {
        this.id = id;
        this.stream_id = stream_id;
        this.subject_id = subject_id;
        this.teacher_id = teacher_id;
        this.stream = stream;
        this.subject = subject;
        this.teacher = teacher;
        periods = new HashMap<>();
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Map<Integer, List<Period>> getPeriods() {
        return periods;
    }

    public void setPeriods(Map<Integer, List<Period>> periods) {
        this.periods = periods;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public RoomAllocation getRoom() {
        return room;
    }

    public void setRoom(RoomAllocation room) {
        this.room = room;
    }

    public int getStream_id() {
        return stream_id;
    }

    public void setStream_id(int stream_id) {
        this.stream_id = stream_id;
    }

    public int getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(int subject_id) {
        this.subject_id = subject_id;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }
}
