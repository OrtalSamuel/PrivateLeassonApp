package com.example.privateleassonapp;

import static com.example.privateleassonapp.Constant.TUTOR_REF;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FidTutorsActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    EditText fid_tutors_TXT_search;
    Spinner fid_tutors_SP_category;
    RecyclerView fid_tutors_recycleView;
    FidTutorsAdapter fidTutorsAdapter;
    ArrayList<FirebaseTutor> tutors = new ArrayList<>();
    String wordSearch = "";
    String modeOfSearch = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fid_tutors);

        findViews();
        showTutors();

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this, R.array.TUTORS_SEARCH, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fid_tutors_SP_category.setAdapter(adapterSpinner);
        fid_tutors_SP_category.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        fid_tutors_TXT_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wordSearch =s.toString();
                setList();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void findViews() {
        fid_tutors_TXT_search = findViewById(R.id.fid_tutors_TXT_search);
        fid_tutors_SP_category = findViewById(R.id.fid_tutors_SP_category);
        fid_tutors_recycleView = findViewById(R.id.fid_tutors_recycleView);
        fid_tutors_recycleView.setHasFixedSize(false);
        fid_tutors_recycleView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void showTutors() {
        wordSearch = "";
        tutors = new ArrayList<>();
       fidTutorsAdapter = new FidTutorsAdapter(this, tutors);
        fid_tutors_recycleView.setAdapter(fidTutorsAdapter);

        setList();
    }

    private int setStringToInt(String str) {
        // we remove also the letter from str
        String strNumber = "";
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) >= '0' && str.charAt(i) <= '9')
                strNumber += str.charAt(i);
        }
        return Integer.parseInt(strNumber);
    }


    private void setList() {
         DatabaseReference root = FirebaseDatabase.getInstance().getReference(TUTOR_REF);
        root.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tutors.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FirebaseTutor model = new FirebaseTutor((HashMap<String, Object>) (dataSnapshot.getValue()));
                    boolean create = true;
                    if (modeOfSearch.equals("Name")) {
                        if (!model.userName.contains(wordSearch.trim()))
                            create = false;
                    } else if (modeOfSearch.equals("Class")) {
                        if (!model.category.contains(wordSearch.trim()))
                            create = false;
                    } else if (modeOfSearch.equals("Price")) {
                        if (model.price > setStringToInt(wordSearch.trim()))
                            create = false;
                    }
                    if (create)
                        tutors.add(model);
                }
                fidTutorsAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        modeOfSearch=parent.getItemAtPosition(position).toString();
        setList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}