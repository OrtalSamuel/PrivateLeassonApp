package com.example.privateleassonapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Objects;

@IgnoreExtraProperties
public class Lesson {
    String key;
    String day; // 13/04/2003
    String hour; // 13:03-14:04
    String tutorUid;
    String tutorName;
    String studentUid;
    String studentName;
    int price;
    String tutorPhone;
    public Lesson() {

    }

    public Lesson(HashMap<String, Object> lesson) {
        this.key = String.valueOf(lesson.get("key"));
        this.day = String.valueOf(lesson.get("day"));
        this.hour = String.valueOf(lesson.get("hour"));
        this.tutorUid = String.valueOf(lesson.get("tutorUid"));
        this.tutorName = String.valueOf(lesson.get("tutorName"));
        this.studentUid = String.valueOf(lesson.get("studentUid"));
        this.studentName = String.valueOf(lesson.get("studentName"));
        this.price = Integer.parseInt(Objects.requireNonNull(lesson.get("price")).toString());
        this.tutorPhone = String.valueOf(lesson.get("tutorPhone"));
    }

    public Lesson(String key, String day, String hour, String tutorUid, String tutorName, String studentUid, String studentName, int price, String tutorPhone) {
        this.key = key;
        this.day = day;
        this.hour = hour;
        this.tutorUid = tutorUid;
        this.tutorName = tutorName;
        this.studentUid = studentUid;
        this.studentName = studentName;
        this.price = price;
        this.tutorPhone = tutorPhone;
    }

    public HashMap<String, Object> getMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("day", day);
        map.put("hour", hour);
        map.put("tutorUid", tutorUid);
        map.put("tutorName", tutorName);
        map.put("studentUid", studentUid);
        map.put("studentName", studentName);
        map.put("price", price);
        map.put("tutorPhone", tutorPhone);
        return map;
    }

    public boolean isBadDate(Lesson other) {
        return this.day.equals(other.day) && this.hour.equals(other.hour);
    }
}
