package com.example.privateleassonapp;
import static com.example.privateleassonapp.Constant.STUDENT_REF;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class OpenActivity extends AppCompatActivity {

    private Button open_BTN_login;
    private Button open_BTN_Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        findViews();

        open_BTN_login.setOnClickListener(view -> clicked(1));
        open_BTN_Register.setOnClickListener(view -> clicked(0));

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            goToMainByUserType();
        }

   }

    private void goToMainByUserType() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(STUDENT_REF);

        myRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().getValue()!=null) {
                        startActivity(new Intent(OpenActivity.this,StudentMainActivity.class));
                        finish();
                    }
                    else {
                        startActivity(new Intent(OpenActivity.this,TutorMainActivity.class));
                        finish();
                    }
                } else {
                    Log.e("firebase - false", task.getResult().toString());
                }
            }
        });
    }

    public void register(){
        Dialog d =new Dialog(this);
        d.setContentView(R.layout.register_dialog);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent); //without background
        d.setCancelable(true); //if you click outside the dialog closed

        LinearLayout dialog_tutor=d.findViewById(R.id.dialog_tutor);
        LinearLayout dialog_student=d.findViewById(R.id.dialog_student);

        dialog_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OpenActivity.this,StudentRegisterActivity.class));
                finish();
            }
        });

        dialog_tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OpenActivity.this,TutorRegisterActivity.class));
                finish();

            }
        });

        d.show();
    }

    private void clicked(int i) {
        if( i ==1){
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        else {
            register();
        }
    }

    private void findViews() {
        open_BTN_login=findViewById(R.id.open_BTN_login);
        open_BTN_Register=findViewById(R.id.open_BTN_Register);

    }

}
