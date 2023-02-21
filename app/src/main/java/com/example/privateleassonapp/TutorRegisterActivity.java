package com.example.privateleassonapp;
import static com.example.privateleassonapp.Constant.TUTOR_REF;
import static com.example.privateleassonapp.MultiSpinner.NOT_CHOSE_CATEGORY;
import static com.example.privateleassonapp.MultiSpinner.getClassCategoryList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class TutorRegisterActivity extends AppCompatActivity {
    EditText tutorRegister_TXT_Email;
    EditText tutorRegister_TXT_UserName;
    EditText tutorRegister_TXT_password;
    EditText tutorRegister_TXT_phone;
    EditText tutorRegister_TXT_price;
    MultiSpinner tutorRegister_SP_category;

    ImageView tutorRegister_Image;

    LinearLayout tutorRegister_AddImage;
    LinearLayout tutorRegister_Register;


    private final int CAMERA_REQUEST = 0;
    private final int MY_CAMERA_PERMISSION_CODE = 1;
    private final int MY_GALLERY_PERMISSION_CODE = 2;

    private Uri mImageUri;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_register);

        findViews();

        tutorRegister_AddImage.setOnClickListener(v -> addImage());
        tutorRegister_Register.setOnClickListener(v -> register());

        tutorRegister_SP_category.setItems(getClassCategoryList(), "" + NOT_CHOSE_CATEGORY, selected -> {});
        progressDialog = new ProgressDialog(this);

    }

    private void register() {
        if(!isThereEmptyData()) {
            String email = tutorRegister_TXT_Email.getText().toString().trim(),
                    password = tutorRegister_TXT_password.getText().toString().trim(),
                    userName = tutorRegister_TXT_UserName.getText().toString().trim();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.e("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName)
                                    .build();
                            assert user != null;
                            user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    setDataBaseTutor();
                                    Toast.makeText(TutorRegisterActivity.this, "The user was successfully created", Toast.LENGTH_LONG).show();
                                    finish();
                                } else
                                    Toast.makeText(TutorRegisterActivity.this, "User creation failed", Toast.LENGTH_LONG).show();
                            });
                        } else {
                            Log.e("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(TutorRegisterActivity.this, "User creation failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(this,"Enter all Data!",Toast.LENGTH_LONG).show();

    }

    public void setDataBaseTutor() {
        if(!progressDialog.isShowing()){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Upload your post...");
            progressDialog.setCancelable(false); // אי אפשר לסגור בלחיצה מבחוץ
            progressDialog.show();
        }

        final DatabaseReference root = FirebaseDatabase.getInstance().getReference("" + TUTOR_REF);
        final StorageReference reference = FirebaseStorage.getInstance().getReference();

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
        fileRef.putFile(mImageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(url -> { // todo its was uri
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(TUTOR_REF);
            // FirebaseTutor(String uid, String email, String userName, String phone,String category, String price, String uri, String url)
            FirebaseTutor s = new FirebaseTutor(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), tutorRegister_TXT_Email.getText().toString().trim(),tutorRegister_TXT_UserName.getText().toString().trim(),
                    tutorRegister_TXT_phone.getText().toString().trim(),tutorRegister_SP_category.spinnerText,Integer.parseInt(tutorRegister_TXT_price.getText().toString().trim()),mImageUri.toString(),url.toString(),0);
            myRef.child("" + s.uid).setValue(s.toMap());

            assert s != null;
            root.child(s.uid).setValue(s.toMap());


            Toast.makeText(this, "uploaded", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, TutorMainActivity.class));
            finish();
        })).addOnProgressListener(snapshot -> {
            if (!progressDialog.isShowing())
                progressDialog.show();
        }).addOnFailureListener(e -> {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        });
    }


    private void addImage() {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.add_image_dialog);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.setCancelable(true);

        LinearLayout dialog_camera = d.findViewById(R.id.dialog_camera);
        LinearLayout dialog_gallery= d.findViewById(R.id.dialog_gallery);


        dialog_gallery.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent,MY_GALLERY_PERMISSION_CODE);
            d.dismiss();
                });

        dialog_camera.setOnClickListener(v -> {
            if(checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
              requestPermissions(new String[]{Manifest.permission.CAMERA},MY_CAMERA_PERMISSION_CODE);
            }else{
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,CAMERA_REQUEST);
                d.dismiss();
             }
        });
        d.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MY_GALLERY_PERMISSION_CODE && resultCode == RESULT_OK &&data !=null){
            mImageUri=data.getData();
            tutorRegister_Image.setImageURI(mImageUri);
        }
        if(requestCode==CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            assert data !=null ;
            Bitmap photo =(Bitmap)data.getExtras().get("data");
            mImageUri = getImageUri(TutorRegisterActivity.this,photo);
            tutorRegister_Image.setImageURI(mImageUri);
        }

        }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path =MediaStore.Images.Media.insertImage(inContext.getContentResolver(),inImage,"Title",null);
        return Uri.parse(path);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
    


    private void findViews() {
        tutorRegister_TXT_Email=findViewById(R.id.tutorRegister_TXT_Email);
        tutorRegister_TXT_UserName=findViewById(R.id.tutorRegister_TXT_UserName);
        tutorRegister_TXT_password=findViewById(R.id.tutorRegister_TXT_password);
        tutorRegister_TXT_phone=findViewById(R.id.tutorRegister_TXT_phone);
        tutorRegister_TXT_price=findViewById(R.id.tutorRegister_TXT_price);
        tutorRegister_AddImage=findViewById(R.id.tutorRegister_AddImage);
        tutorRegister_Register=findViewById(R.id.tutorRegister_Register);
        tutorRegister_Image=findViewById(R.id.tutorRegister_Image);
        tutorRegister_SP_category=findViewById(R.id.tutorRegister_SP_category);

    }
    public boolean isThereEmptyData(){
        return tutorRegister_TXT_Email.getText().toString().trim().isEmpty() ||
                tutorRegister_TXT_UserName.getText().toString().trim().isEmpty() ||
                tutorRegister_TXT_password.getText().toString().trim().isEmpty() ||
                tutorRegister_TXT_phone.getText().toString().trim().isEmpty() ||
                tutorRegister_TXT_price.getText().toString().trim().isEmpty()||
                tutorRegister_SP_category.spinnerText.equals(NOT_CHOSE_CATEGORY); // todo spinner;


    }


}