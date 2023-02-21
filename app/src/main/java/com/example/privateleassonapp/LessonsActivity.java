package com.example.privateleassonapp;

import static com.example.privateleassonapp.Constant.STUDENT_REF;
import static com.example.privateleassonapp.Constant.TUTOR_REF;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LessonsActivity extends AppCompatActivity {

    RecyclerView lessons_recycleView;
    ArrayList<Lesson> lessons;
    LessonAdapter lessonAdapter;
    String ref = "";
    boolean isTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        Intent in = getIntent();
        if (in.getExtras().getBoolean("IS_TUTOR")) isTutor = true;
        else isTutor = false;

        findViews();
        showLessons();
    }

    private void showLessons() {
        lessons = new ArrayList<>();
        lessonAdapter = new LessonAdapter(this, lessons, isTutor);
        lessons_recycleView.setAdapter(lessonAdapter);

        setList();
    }

    private void findViews() {
        lessons_recycleView = findViewById(R.id.lessons_recycleView);
        lessons_recycleView.setHasFixedSize(false);
        lessons_recycleView.setLayoutManager(new LinearLayoutManager(this));

    }
    public void setList() {
        if (isTutor)
            ref = TUTOR_REF;
        else ref = STUDENT_REF;
        DatabaseReference root = FirebaseDatabase.getInstance().getInstance().getReference(ref);
        root.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                lessons.clear();
                if (!isTutor) {
                    FirebaseStudent model = new FirebaseStudent((HashMap<String, Object>) (Objects.requireNonNull(dataSnapshot.getValue())));
                    lessons.addAll(model.lessons);
                } else {
                    FirebaseTutor model = new FirebaseTutor((HashMap<String, Object>) (Objects.requireNonNull(dataSnapshot.getValue())));
                    lessons.addAll(model.lessons);
                }
                lessonAdapter.notifyDataSetChanged();
            }
        });

    }
}