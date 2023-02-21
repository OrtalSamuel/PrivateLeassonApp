package com.example.privateleassonapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@IgnoreExtraProperties
public class FirebaseStudent {
    public String uid;
    public String userName;
    public String email;
    public ArrayList<Lesson> lessons; //can be null

    public FirebaseStudent(){

    }
    public FirebaseStudent(String uid,String email, String userName, ArrayList<Lesson> lessons) {
        this.uid = uid;
        this.email=email;
        this.userName = userName;
        this.lessons = lessons;
    }
    public FirebaseStudent(HashMap<String, Object> map) {
        this.uid = String.valueOf(map.get("uid"));
        this.userName = Objects.requireNonNull(map.get("userName")).toString();
        this.email = Objects.requireNonNull(map.get("email")).toString();
        this.lessons = new ArrayList<>();
        int i=0;
        while (map.get("l"+i)!=null) {
            lessons.add(new Lesson((HashMap<String, Object>) map.get("l"+i)));
            i++;
        }
    }

    public FirebaseStudent(String uid,String email, String userName ){
        this.uid = uid;
        this.email = email;
        this.userName = userName;
        this.lessons=new ArrayList<>();

    }


    public HashMap<String, Object> getBaseData(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid",this.uid);
        map.put("userName", userName);
        map.put("email",email);
        if(lessons!=null)
            for(int i=0;i< lessons.size();i++)
                map.put("l"+i,lessons.get(i).getMap());
        return map;
    }

    public boolean isBadLesson(Lesson other) {
        for (int i = 0; i < this.lessons.size(); i++) {
            if (lessons.get(i).isBadDate(other)) return false;
        }
        return true;
    }
}
