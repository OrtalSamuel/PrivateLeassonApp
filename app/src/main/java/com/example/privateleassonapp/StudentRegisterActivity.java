package com.example.privateleassonapp;

import static com.example.privateleassonapp.Constant.STUDENT_REF;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentRegisterActivity extends AppCompatActivity {

    EditText studentRegister_TXT_Email;
    EditText studentRegister_TXT_UserName;
    EditText studentRegister_TXT_password;
    Button studentRegister_BTN_Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        findViews();
        studentRegister_BTN_Register.setOnClickListener(view->register());
    }

    private void setDataBasePlayer() {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference(STUDENT_REF);
        FirebaseStudent s =new FirebaseStudent(FirebaseAuth.getInstance().getUid(),studentRegister_TXT_Email.getText().toString().trim(),studentRegister_TXT_UserName.getText().toString().trim());
        myRef.child("" + s.uid).setValue(s.getBaseData());

    }

    private void register() {
        if(!isThereEmptyData()){
            String email = studentRegister_TXT_Email.getText().toString().trim(),
                    password=studentRegister_TXT_password.getText().toString().trim(),
                    userName =studentRegister_TXT_UserName.getText().toString().trim();


            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this,task->{
                if(task.isSuccessful()){
                    Log.e("TAG","createUserWithEmail:success");
                    FirebaseUser user =mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates =new UserProfileChangeRequest.Builder()
                            .setDisplayName(userName)
                            .build();

                    assert user !=null;
                    user.updateProfile(profileUpdates).addOnCompleteListener(task1 ->{
                        if(task1.isSuccessful()){
                            setDataBasePlayer();
                            Toast.makeText(StudentRegisterActivity.this,"The user was successfully created ",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(StudentRegisterActivity.this, StudentMainActivity.class));
                            finish();
                        }else
                            Toast.makeText(StudentRegisterActivity.this,"User creation failed",Toast.LENGTH_SHORT).show();
                    });
                }else{
                    //if sign in fail display a message to the user
                    Log.e("TAG","createUserWithEmail:failure",task.getException());
                    Toast.makeText(StudentRegisterActivity.this, "User creation failed", Toast.LENGTH_SHORT).show();
                }

            });

        } else
            Toast.makeText(StudentRegisterActivity.this, "Enter All Data", Toast.LENGTH_SHORT).show();

    }



    public boolean isThereEmptyData(){
        return studentRegister_TXT_Email.toString().trim().isEmpty() ||
                studentRegister_TXT_UserName.toString().trim().isEmpty() ||
                studentRegister_TXT_password.toString().trim().isEmpty();

    }

    private void findViews() {
        studentRegister_TXT_Email=findViewById(R.id.studentRegister_TXT_Email);
        studentRegister_TXT_UserName=findViewById(R.id.studentRegister_TXT_UserName);
        studentRegister_TXT_password=findViewById(R.id.studentRegister_TXT_password);
        studentRegister_BTN_Register=findViewById(R.id.studentRegister_BTN_Register);

    }


}
