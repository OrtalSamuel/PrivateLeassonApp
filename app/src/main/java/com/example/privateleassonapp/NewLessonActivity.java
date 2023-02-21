package com.example.privateleassonapp;

import static com.example.privateleassonapp.Constant.STUDENT_REF;
import static com.example.privateleassonapp.Constant.TUTOR_REF;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NewLessonActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    DatePicker new_lesson_dpDay;
    Spinner spHour;
    Button new_lesson_BTN_ok;
    FirebaseTutor firebaseTutor;
    TextView new_lesson_TXT_tutorName;

    String chosenHour = "00:00";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lesson);

        new_lesson_dpDay = findViewById(R.id.new_lesson_dpDay);

        new_lesson_BTN_ok = findViewById(R.id.new_lesson_BTN_ok);
        new_lesson_BTN_ok.setOnClickListener(this);

        if (getIntent().getExtras().getSerializable("TUTOR") != null) {
            firebaseTutor = new FirebaseTutor((HashMap<String, Object>) getIntent().getExtras().getSerializable("TUTOR"));
            new_lesson_TXT_tutorName = findViewById(R.id.new_lesson_TXT_tutorName);
            Log.e("firebaseTutor", firebaseTutor.toMap().toString());
            new_lesson_TXT_tutorName.setText("" + firebaseTutor.userName);
        }

        spHour = findViewById(R.id.spHour);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this, R.array.TUTORS_TIMES, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHour.setAdapter(adapterSpinner);
        spHour.setOnItemSelectedListener(this);
    }

    public String generateDay() {
        if (isGoodDate())
            return CompleteDate(String.valueOf(new_lesson_dpDay.getDayOfMonth())) + "/" + CompleteDate(String.valueOf(new_lesson_dpDay.getMonth())) + "/" + new_lesson_dpDay.getYear();
        return "wrong day";
    }

    public String generateHour() {
        if (isGoodDate())
            return chosenHour;
        else return "Wrong day!";
    }

    public String CompleteDate(String dm) {
        if (dm.length() == 1) return "0" + dm;
        return dm;
    }

    public boolean isGoodDate() { // todo change to good
        String temp = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date()).toString();
        Log.e("temp", temp);
        String currentYear = temp.substring(0, 4);
        Log.e("currentYear", currentYear);
        String currentMonth = temp.substring(6, 7);
        Log.e("currentMonth", currentMonth);
        String currentDay = temp.substring(9, 10);
        Log.e("currentDay", currentDay);
        Log.e("dpDay", new_lesson_dpDay.getYear() + " - " + new_lesson_dpDay.getMonth() + " - " + new_lesson_dpDay.getDayOfMonth());
        if (Integer.parseInt(currentYear) > new_lesson_dpDay.getYear()) return false;
        if (Integer.parseInt(currentYear) == new_lesson_dpDay.getYear())
            return Integer.parseInt(currentMonth) > new_lesson_dpDay.getMonth();
        if (Integer.parseInt(currentMonth) == new_lesson_dpDay.getMonth())
            return Integer.parseInt(currentDay) <= new_lesson_dpDay.getDayOfMonth();
        return true;
    }

    public void uploadLesson() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Upload your post...");
        progressDialog.setCancelable(false); // אי אפשר לסגור בלחיצה מבחוץ
        progressDialog.show();

        DatabaseReference rootTutor = FirebaseDatabase.getInstance().getReference(TUTOR_REF);

        Lesson l = new Lesson("", generateDay(), generateHour(), firebaseTutor.uid, firebaseTutor.userName, FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), firebaseTutor.price, firebaseTutor.phone);

        final DatabaseReference rootStudent = FirebaseDatabase.getInstance().getReference(STUDENT_REF);

        rootStudent.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                final FirebaseStudent f = new FirebaseStudent((HashMap<String, Object>) dataSnapshot.getValue());
                rootTutor.child("" + firebaseTutor.uid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        FirebaseTutor t = new FirebaseTutor((HashMap<String, Object>) dataSnapshot.getValue());
                        Log.e("tutor", t.toMap().toString());
                        if (t.isBadLesson(l) && f.isBadLesson(l)) {
                            f.lessons.add(l);
                            t.lessons.add(l);
                            rootStudent.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(f.getBaseData());
                            rootTutor.child(firebaseTutor.uid).setValue(t.toMap());
                            Toast.makeText(NewLessonActivity.this, "The lesson uploaded", Toast.LENGTH_SHORT).show();
                            finish();
                        } else
                            Toast.makeText(NewLessonActivity.this, "The date has been taken", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        progressDialog.dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_lesson_BTN_ok:
                uploadLesson();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == spHour.getId()) chosenHour = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}