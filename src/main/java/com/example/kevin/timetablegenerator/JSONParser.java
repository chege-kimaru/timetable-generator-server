package com.example.kevin.timetablegenerator;

import com.example.kevin.timetablegenerator.models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {
    private List<SubjectGroup> subjectGroups = new ArrayList<>();
    private List<Subject> subjects = new ArrayList<>();
    private List<Stream> streams = new ArrayList<>();
    private List<Teacher> teachers = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private List<Period> periods = new ArrayList<>();
    private List<Allocation> allocations = new ArrayList<>();

    JSONParser(String json) {
        parseJSON(json);
    }

    private void parseJSON(String json) {
        JSONObject root = new JSONObject(json);
        JSONArray subjectGroups = root.getJSONArray("subjectGroups");
        this.subjectGroups = parseSubjectGroups(subjectGroups);
        JSONArray subjects = root.getJSONArray("subjects");
        this.subjects = parseSubjects(subjects);
        JSONArray streams = root.getJSONArray("streams");
        this.streams = parseStreams(streams);
        JSONArray teachers = root.getJSONArray("teachers");
        this.teachers = parseTeachers(teachers);
        JSONArray rooms = root.getJSONArray("rooms");
        this.rooms = parseRooms(rooms);
        JSONArray periods = root.getJSONArray("periods");
        this.periods = parsePeriods(periods);
        JSONArray allocations = root.getJSONArray("allocations");
        this.allocations = parseAllocations(allocations);
    }

    private List<SubjectGroup> parseSubjectGroups(JSONArray subjectGroups) {
        List<SubjectGroup> sgs = new ArrayList<>();
        for (int i = 0; i < subjectGroups.length(); i++) {
            JSONObject sgObject = subjectGroups.getJSONObject(i);
            SubjectGroup sg = new SubjectGroup(sgObject.getInt("id"), sgObject.getString("name"));
            sgs.add(sg);
        }
        return sgs;
    }

    private List<Subject> parseSubjects(JSONArray subjects) {
        List<Subject> ss = new ArrayList<>();
        for (int i = 0; i < subjects.length(); i++) {
            JSONObject sObject = subjects.getJSONObject(i);
            Subject s = new Subject(sObject.getInt("id"), sObject.getInt("subject_group_id"),
                    null, sObject.getString("name"), sObject.getBoolean("hasLab"));
            ss.add(s);
        }
        return ss;
    }

    private List<Stream> parseStreams(JSONArray streams) {
        List<Stream> ss = new ArrayList<>();
        for (int i = 0; i < streams.length(); i++) {
            JSONObject sObject = streams.getJSONObject(i);
            Stream s = new Stream(sObject.getInt("id"), sObject.getString("name"));
            ss.add(s);
        }
        return ss;
    }

    private List<Teacher> parseTeachers(JSONArray teachers) {
        List<Teacher> ts = new ArrayList<>();
        for (int i = 0; i < teachers.length(); i++) {
            JSONObject tObject = teachers.getJSONObject(i);
            Teacher t = new Teacher(tObject.getInt("id"), tObject.getString("name"));
            ts.add(t);
        }
        return ts;
    }

    private List<Room> parseRooms(JSONArray rooms) {
        List<Room> rs = new ArrayList<>();
        for (int i = 0; i < rooms.length(); i++) {
            JSONObject rObject = rooms.getJSONObject(i);
            Room r = new Room(rObject.getInt("id"), rObject.getInt("subject_id"),
                    rObject.getString("name"), null);
            rs.add(r);
        }
        return rs;
    }

    private List<Period> parsePeriods(JSONArray periods) {
        List<Period> ps = new ArrayList<>();
        for (int i = 0; i < periods.length(); i++) {
            JSONObject pObject = periods.getJSONObject(i);
            Period p = new Period(pObject.getInt("id"), pObject.getString("from"),
                    pObject.getString("to"), pObject.getBoolean("toBreak"));
            ps.add(p);
        }
        return ps;
    }

    private List<Allocation> parseAllocations(JSONArray allocations) {
        List<Allocation> as = new ArrayList<>();
        for (int i = 0; i < allocations.length(); i++) {
            JSONObject aObject = allocations.getJSONObject(i);
            Allocation a = new Allocation(aObject.getInt("id"), aObject.getInt("stream_id"),
                    aObject.getInt("subject_id"), aObject.getInt("teacher_id"), null, null, null);

            JSONObject sObject = aObject.getJSONObject("subject");
            Subject s = new Subject(sObject.getInt("id"), sObject.getInt("subject_group_id"),
                    null, sObject.getString("name"), sObject.getBoolean("hasLab"));

            JSONObject sgObject = sObject.getJSONObject("subjectGroup");
            SubjectGroup sg = new SubjectGroup(sgObject.getInt("id"), sgObject.getString("name"));
            s.group = sg;

            a.subject = s;

            JSONObject stObject = aObject.getJSONObject("stream");
            Stream st = new Stream(stObject.getInt("id"), stObject.getString("name"));
            a.stream = st;

            JSONObject tObject = aObject.getJSONObject("teacher");
            Teacher t = new Teacher(tObject.getInt("id"), tObject.getString("name"));
            a.teacher = t;

            as.add(a);
        }
        return as;
    }

    public List<SubjectGroup> getSubjectGroups() {
        return subjectGroups;
    }

    public void setSubjectGroups(List<SubjectGroup> subjectGroups) {
        this.subjectGroups = subjectGroups;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<Stream> getStreams() {
        return streams;
    }

    public void setStreams(List<Stream> streams) {
        this.streams = streams;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    public List<Allocation> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<Allocation> allocations) {
        this.allocations = allocations;
    }
}
