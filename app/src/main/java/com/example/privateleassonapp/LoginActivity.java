package com.example.privateleassonapp;

import static com.example.privateleassonapp.Constant.STUDENT_REF;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText login_TXT_Email;
    EditText login_TXT_password;
    Button login_BTN_Login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();

        login_BTN_Login.setOnClickListener(view -> login());
    }

    public void goToMainByUserType() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(STUDENT_REF);

        myRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getValue() != null) {
                        startActivity(new Intent(LoginActivity.this, StudentMainActivity.class));
                        finish();
                    }
                    else {
                        startActivity(new Intent(LoginActivity.this, TutorMainActivity.class));
                        finish();
                    }
                } else {
                    Log.e("firebase -false", task.getResult().toString());
                }

            }
        });

    }


    private void login() {
        if(!isThereEmptyData()){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(login_TXT_Email.getText().toString(),login_TXT_password.getText().toString())
                    .addOnCompleteListener(this,task->{
                        if(task.isSuccessful()){
                            goToMainByUserType();
                        }else{
                            Toast.makeText(LoginActivity.this,"the data Incorrect!",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        Toast.makeText(LoginActivity.this,"Enter All Data ",Toast.LENGTH_SHORT).show();

    }

    public boolean isThereEmptyData(){
        return login_TXT_Email.toString().trim().isEmpty() ||
                login_TXT_password.toString().trim().isEmpty() ;


    }

    private void findViews() {
        login_TXT_Email=findViewById(R.id.login_TXT_Email);
        login_TXT_password=findViewById(R.id.login_TXT_password);
        login_BTN_Login=findViewById(R.id.login_BTN_Login);

    }
}