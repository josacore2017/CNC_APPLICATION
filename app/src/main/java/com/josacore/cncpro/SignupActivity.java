package com.josacore.cncpro;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josacore.cncpro.classes.Profile;
import com.josacore.cncpro.classes.User;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "SignUpActivity";

    private int REQUEST_PHOTO_CAPTURE = 1;
    private int REQUEST_PHOTO_PICK = 2;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private StorageReference userImageRef;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mIdentityCardField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;

    private ImageView iv_user_image;

    private Button mSignUpButton;

    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        // Views
        mFirstNameField = findViewById(R.id.et_first_name);
        mLastNameField = findViewById(R.id.et_last_name);
        mIdentityCardField = findViewById(R.id.et_identity_card);
        mEmailField = findViewById(R.id.et_email);
        mPasswordField = findViewById(R.id.et_password);
        mConfirmPasswordField = findViewById(R.id.et_confirm_password);
        mSignUpButton = findViewById(R.id.btn_sign_up);

        iv_user_image = findViewById(R.id.iv_signup_imagen);
        iv_user_image.setOnClickListener(this);

        // Click listeners
        mSignUpButton.setOnClickListener(this);

    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }
        showProgressBar();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressBar();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignupActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());
        writeNewProfile(user.getUid(), mIdentityCardField.getText().toString(), mFirstNameField.getText().toString(),mLastNameField.getText().toString(),"","","");
        try {
            updateUserPhoto(user.getUid(),mPhotoUri);
        }catch (Exception e){
            Log.e(TAG,"error");
        }


        // Go to MainActivity
        startActivity(new Intent(SignupActivity.this, MainActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(mFirstNameField.getText().toString())) {
            mFirstNameField.setError("Required");
            result = false;
        } else {
            mFirstNameField.setError(null);
        }

        if (TextUtils.isEmpty(mLastNameField.getText().toString())) {
            mLastNameField.setError("Required");
            result = false;
        } else {
            mLastNameField.setError(null);
        }

        if (TextUtils.isEmpty(mIdentityCardField.getText().toString())) {
            mIdentityCardField.setError("Required");
            result = false;
        } else {
            mIdentityCardField.setError(null);
        }

        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        if (TextUtils.isEmpty(mConfirmPasswordField.getText().toString())) {
            mConfirmPasswordField.setError("Required");
            result = false;
        } else {
            mConfirmPasswordField.setError(null);
        }

        if (!TextUtils.equals(mConfirmPasswordField.getText().toString(),mPasswordField.getText().toString())) {
            mPasswordField.setError("Password may match.");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private void writeNewProfile(String uid, String identityCard, String firstName,String lastName,String photo,String nationality,String phone){
        Profile profile =new Profile(uid,identityCard,firstName,lastName,photo,nationality,phone);
        mDatabase.child("profiles").child(uid).setValue(profile);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if( i == R.id.btn_sign_up) {
            signUp();
        }
        if( i == R.id.iv_signup_imagen){
            uploadImage();
        }
    }
    private void uploadImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setMessage("Como quieres subir tu foto?");
        builder.setPositiveButton("Tomar Foto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dispatchTakePhotoIntent();
            }
        });
        builder.setNegativeButton("Elegir Foto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dispatchChoosePhotoIntent();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
    private void updateUserPhoto(final String uid, Uri photoUri) throws Exception{

        userImageRef = mStorage.child("UserImages").child(uid).child(photoUri.getLastPathSegment());
        UploadTask uploadTask = userImageRef.putFile(photoUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return userImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.e("SASDASDA: ",downloadUri.toString());
                    Map<String,Object> updatePhotoMap = new HashMap<>();
                    updatePhotoMap.put("photo",downloadUri.toString());
                    mDatabase.child("profiles").child(uid).updateChildren(updatePhotoMap).addOnSuccessListener(new OnSuccessListener<Void>(){
                        @Override
                        public void onSuccess(Void aVoid){
                            Toast.makeText(getApplicationContext(),"Success updating photo",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"Unsuccess updating photo",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void dispatchTakePhotoIntent(){
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(getPackageManager()) !=null){
            startActivityForResult(takePhotoIntent,REQUEST_PHOTO_CAPTURE);
        }
    }
    private void dispatchChoosePhotoIntent(){
        Intent choosePhotoIntent = new Intent(Intent.ACTION_PICK);
        choosePhotoIntent.setType("image/*");
        startActivityForResult(choosePhotoIntent,REQUEST_PHOTO_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PHOTO_CAPTURE && resultCode == RESULT_OK){
            mPhotoUri = data.getData();
            iv_user_image.setImageURI(mPhotoUri);
        }else if(requestCode == REQUEST_PHOTO_PICK && resultCode == RESULT_OK){
            mPhotoUri = data.getData();
            iv_user_image.setImageURI(mPhotoUri);
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
    }
}
