package com.example.privateleassonapp;
import static com.example.privateleassonapp.Constant.TUTOR_REF;
import static com.example.privateleassonapp.MultiSpinner.NOT_CHOSE_CATEGORY;
import static com.example.privateleassonapp.MultiSpinner.getClassCategoryList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class TutorEditActivity extends AppCompatActivity {


    EditText tutorEdit_TXT_UserName;
    EditText tutorEdit_TXT_phone;
    EditText tutorEdit_TXT_price;
    EditText tutorEdit_TXT_email;
    MultiSpinner tutorEdit_SP_category;

    ImageView tutorEdit_Image;

    LinearLayout tutorEdit_AddImage;
    LinearLayout tutorEdit_Update;


    private final int CAMERA_REQUEST = 0;
    private final int MY_CAMERA_PERMISSION_CODE = 1;
    private final int MY_GALLERY_PERMISSION_CODE = 2;

    private Uri mImageUri;

    ProgressDialog progressDialog;

    FirebaseTutor originTutor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_edit);

        findViews();

        tutorEdit_AddImage.setOnClickListener(v -> addImage());
        tutorEdit_Update.setOnClickListener(v -> updateTutor());

        tutorEdit_SP_category.setItems(getClassCategoryList(), "" + NOT_CHOSE_CATEGORY, selected -> {});

        setOriginData();
        progressDialog = new ProgressDialog(this);


    }

    private void setOriginData() {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference(TUTOR_REF);
        root.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    originTutor = new FirebaseTutor((HashMap<String, Object>) task.getResult().getValue());
                    Picasso.get().load(originTutor.url).into(tutorEdit_Image);
                    tutorEdit_SP_category.setSelected(originTutor.category);
                    tutorEdit_TXT_price.setText(String.valueOf(originTutor.price));
                    tutorEdit_TXT_phone.setText(originTutor.phone);
                    tutorEdit_TXT_email.setText(originTutor.email);
                    tutorEdit_TXT_UserName.setText(originTutor.userName);
                }
            }
        });
    }
    public FirebaseTutor generateTutor(){
        originTutor.userName=tutorEdit_TXT_UserName.getText().toString().trim();
        originTutor.phone = tutorEdit_TXT_phone.getText().toString().trim();
        originTutor.price = Integer.parseInt(tutorEdit_TXT_price.getText().toString().trim());
        return originTutor;
    }

    private void updateTutor() {
        if(!isThereEmptyData()){
            if (mImageUri == null) setDataPostWithOutImage(generateTutor());
            else {
                uploadTutorWithImage();
            }
        } else Toast.makeText(this, "Enter all Data!", Toast.LENGTH_LONG).show();
    }


    public boolean isThereEmptyData() {
        return tutorEdit_TXT_UserName.getText().toString().trim().isEmpty()
                || tutorEdit_TXT_price.getText().toString().trim().isEmpty()
                || tutorEdit_TXT_phone.getText().toString().trim().isEmpty()
                || tutorEdit_SP_category.spinnerText.equals(NOT_CHOSE_CATEGORY);
    }

    private void findViews() {
        tutorEdit_TXT_UserName =findViewById(R.id.tutorEdit_TXT_UserName);
        tutorEdit_TXT_email=findViewById(R.id.tutorEdit_TXT_email);
        tutorEdit_TXT_phone =findViewById(R.id.tutorEdit_TXT_phone);
        tutorEdit_TXT_price =findViewById(R.id.tutorEdit_TXT_price);
        tutorEdit_AddImage=findViewById(R.id.tutorEdit_AddImage);
        tutorEdit_Update=findViewById(R.id.tutorEdit_Update);
        tutorEdit_Image =findViewById(R.id.tutorEdit_Image);
        tutorEdit_SP_category =findViewById(R.id.tutorEdit_SP_category);
    }


    private void addImage() {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.add_image_dialog);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.setCancelable(true);

        LinearLayout dialog_camera =d.findViewById(R.id.dialog_camera);
        LinearLayout dialog_gallery=d.findViewById(R.id.dialog_gallery);


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
            tutorEdit_Image.setImageURI(mImageUri);
        }
        if(requestCode==CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            assert data !=null ;
            Bitmap photo =(Bitmap)data.getExtras().get("data");
            mImageUri = getImageUri(TutorEditActivity.this,photo);
            tutorEdit_Image.setImageURI(mImageUri);
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

    private void setDataPostWithOutImage(FirebaseTutor tutor){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(originTutor.userName).build();
        user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("/" + TUTOR_REF + "/" + tutor.uid);
                tempRef.setValue(tutor.toMap());
                Toast.makeText(this, "successfully updated", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(TutorEditActivity.this, "failed updated", Toast.LENGTH_LONG).show();
        });
    }

    private void uploadTutorWithImage() {
        originTutor = generateTutor();
        deleteImage();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Update your data...");
        progressDialog.setCancelable(false); // אי אפשר לסגור בלחיצה מבחוץ
        progressDialog.show();

        final StorageReference reference = FirebaseStorage.getInstance().getReference();

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
        fileRef.putFile(mImageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(url -> {
            originTutor.url = url.toString();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(originTutor.userName).build();
            user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("/" + TUTOR_REF + "/" + originTutor.uid);
                    tempRef.setValue(originTutor.toMap());
                    Toast.makeText(this, "successfully updated", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(TutorEditActivity.this, "failed updated", Toast.LENGTH_LONG).show();
            });

            progressDialog.cancel();

            Toast.makeText(this, "The post uploaded", Toast.LENGTH_SHORT).show();
//           todo maybe its problem Intent intent = new Intent(this, TutorEditActivity.class);
//            startActivity(intent);
//            finish();
        })).addOnProgressListener(snapshot -> {
            if (!progressDialog.isShowing()) progressDialog.show();
        }).addOnFailureListener(e -> {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            Toast.makeText(this, "The upload failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(originTutor.url);
        storageReference.delete().addOnSuccessListener(aVoid -> {
            // todo toast
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(TutorEditActivity.this, "connection problem - did not delete file", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
