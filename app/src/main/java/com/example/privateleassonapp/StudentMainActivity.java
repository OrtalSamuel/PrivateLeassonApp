package com.example.privateleassonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class StudentMainActivity extends AppCompatActivity {

    private TextView main_student_UserName;
    private FrameLayout fSearch;
    private FrameLayout fClasses ;
    private FrameLayout  fLogOut;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        
        findViews();
        fSearch.setOnClickListener(view -> search());
        fClasses.setOnClickListener(view -> classes());
        fLogOut.setOnClickListener(view -> logOut());
    }


    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,OpenActivity.class));
    }

    private void classes() {
        Intent intent = new Intent(this, LessonsActivity.class);
        intent.putExtra("IS_TUTOR",false);
        startActivity(intent);
    }

    private void search() {
        startActivity(new Intent(this,FidTutorsActivity.class));
    }

    private void findViews() {
        main_student_UserName = findViewById(R.id.main_student_UserName);
        main_student_UserName.setText("Welcome " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        fSearch = findViewById(R.id.fSearch);
        fClasses = findViewById(R.id.fClasses);
        fLogOut = findViewById(R.id.fLogOut);
       
    }
}