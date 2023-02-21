package com.example.privateleassonapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FirebaseTutor  {
    public String uid;
    public String email;
    public String userName;
    public String phone;
    public String category;
    public int price;
    public String uri;
    public String url; // for delete the image from firebase
    public ArrayList<Lesson> lessons;
    public int score;

    public FirebaseTutor() {

    }

    public FirebaseTutor(String uid, String email, String userName, String phone, String category, int price, String uri, String url, ArrayList<Lesson> lessons, int score) {
        this.uid = uid;
        this.email = email;
        this.userName = userName;
        this.phone = phone;
        this.category = category;
        this.price = price;
        this.uri = uri;
        this.url = url;
        this.lessons = lessons;
        this.score = score;
    }

    public FirebaseTutor(String uid, String email, String userName, String phone, String category, int price, String uri, String url, int score) {
        this.uid = uid;
        this.email = email;
        this.userName = userName;
        this.phone = phone;
        this.category = category;
        this.price = price;
        this.uri = uri;
        this.url = url;
        this.lessons = new ArrayList<>();
        this.score = score;
    }



    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", this.uid);
        map.put("email", email);
        map.put("userName", userName);
        map.put("phone", phone);
        map.put("category", category);
        map.put("price", price);
        map.put("uri", uri);
        map.put("url", url);
        map.put("score", score);
        if (lessons != null)
            for (int i = 0; i < lessons.size(); i++)
                map.put("l" + i, lessons.get(i).getMap());
        return map;
    }

    public FirebaseTutor(HashMap<String, Object> map) { //get map from firebase and create tutor
        this.uid = Objects.requireNonNull(map.get("uid")).toString();
        this.email = Objects.requireNonNull(map.get("email")).toString();
        this.userName = Objects.requireNonNull(map.get("userName")).toString();
        this.phone = Objects.requireNonNull(map.get("phone")).toString();
        this.category = Objects.requireNonNull(map.get("category")).toString();
        this.price = Integer.parseInt(Objects.requireNonNull(map.get("price")).toString());
        this.uri = Objects.requireNonNull(map.get("uri")).toString();
        this.url = Objects.requireNonNull(map.get("url")).toString();
        this.score =Integer.parseInt(Objects.requireNonNull(map.get("score")).toString());
        this.lessons = new ArrayList<>();
        int i = 0;
        while (map.get("l" + i) != null) {
            lessons.add(new Lesson((HashMap<String, Object>) map.get("l" + i)));
            i++;
        }
        this.uid = Objects.requireNonNull(map.get("uid")).toString();
        this.email = Objects.requireNonNull(map.get("email")).toString();
        this.userName = Objects.requireNonNull(map.get("userName")).toString();
        this.phone = Objects.requireNonNull(map.get("phone")).toString();
        this.category = Objects.requireNonNull(map.get("category")).toString();
        this.price = Integer.parseInt(Objects.requireNonNull(map.get("price")).toString());
        this.uri = Objects.requireNonNull(map.get("uri")).toString();
        this.url = Objects.requireNonNull(map.get("url")).toString();
        this.score = Integer.parseInt(Objects.requireNonNull(map.get("score")).toString());
    }

    public boolean isBadLesson(Lesson other) {
        for (int i = 0; i < this.lessons.size(); i++) {
            if (lessons.get(i).isBadDate(other)) return false;
        }
        return true;
    }

}



