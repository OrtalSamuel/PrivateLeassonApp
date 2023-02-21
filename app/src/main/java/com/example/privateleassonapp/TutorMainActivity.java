package com.example.privateleassonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class TutorMainActivity extends AppCompatActivity {

    private TextView main_tutor_UserName;
    private FrameLayout fEdit;
    private FrameLayout fClasses ;
    private FrameLayout  fLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_main);

        findViews();
        fEdit.setOnClickListener(view -> edit());
        fClasses.setOnClickListener(view -> classes());
        fLogOut.setOnClickListener(view -> logOut());
    }
    private void edit() {
        startActivity(new Intent(this, TutorEditActivity.class));
        finish();
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,OpenActivity.class));
        finish();
    }

    private void classes() {
        Intent intent = new Intent(this, LessonsActivity.class);
        intent.putExtra("IS_TUTOR",true);
        startActivity(intent);
    }

    private void findViews() {
        main_tutor_UserName = findViewById(R.id.main_tutor_UserName);
        main_tutor_UserName.setText("Welcome "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        fEdit = findViewById(R.id.fEdit);
        fClasses = findViewById(R.id.fClasses);
        fLogOut = findViewById(R.id.fLogOut);
    }
}