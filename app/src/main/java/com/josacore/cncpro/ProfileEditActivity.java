package com.josacore.cncpro;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josacore.cncpro.classes.Profile;
import com.josacore.cncpro.utils.PicassoCircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ProfileEditActivity extends BaseActivity {

    private String TAG = "ProfileEditActivity";

    private int REQUEST_PHOTO_CAPTURE = 1;
    private int REQUEST_PHOTO_PICK = 2;

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private StorageReference mStorage;
    private StorageReference userImageRef;

    private EditText et_profile_first_name;
    private EditText et_profile_last_name;
    private EditText et_profile_identity_card;
    private EditText et_profile_phone;
    private ImageView iv_profile_image;
    private Spinner s_profile_nationality;

    private ArrayAdapter<String> adapter;
    private String photo;

    private Uri mPhotoUri;

    private String profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        Bundle extras = getIntent().getExtras();
        profileId = extras.getString("profileId");
        Log.e(TAG,"profileID: "+profileId);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        s_profile_nationality = (Spinner) findViewById(R.id.s_profile_country);
        et_profile_first_name = (EditText) findViewById(R.id.et_profile_first_name);
        et_profile_last_name = (EditText) findViewById(R.id.et_profile_last_name);
        et_profile_identity_card = (EditText) findViewById(R.id.et_profile_identity_card);
        et_profile_phone = (EditText) findViewById(R.id.et_profile_phone);
        iv_profile_image = (ImageView) findViewById(R.id.iv_profile_edit_image);

        iv_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        //All to add countries
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }


        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        adapter = new ArrayAdapter<String>(this,R.layout.spinner_layout, countries);
        s_profile_nationality.setAdapter(adapter);
        String DEFAULT_LOCAL = "Bolivia";
        s_profile_nationality.setSelection(adapter.getPosition(DEFAULT_LOCAL));

        if(!profileId.equals("0"))
            mDatabase.child("profiles").child(profileId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String identityCard = dataSnapshot.child("identityCard").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    photo = dataSnapshot.child("photo").getValue(String.class);
                    String nationality = dataSnapshot.child("nationality").getValue(String.class);

                    et_profile_first_name.setText(firstName);
                    et_profile_last_name.setText(lastName);
                    et_profile_identity_card.setText(identityCard);
                    et_profile_phone.setText(phone);
                    s_profile_nationality.setSelection(adapter.getPosition(nationality));

                    try {
                        Picasso.with(ProfileEditActivity.this)
                                .load(photo)
                                .placeholder(R.drawable.ic_person_white)
                                .transform(new PicassoCircleTransformation())
                                .into(iv_profile_image);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_correct) {
            updateProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {
        Log.d(TAG, "UpdatingProfile");
        if (!validateForm()) {
            return;
        }
        showProgressBar();

        String firstName = et_profile_first_name.getText().toString();
        String lastname = et_profile_last_name.getText().toString();
        String identity_card = et_profile_identity_card.getText().toString();
        String phone = et_profile_phone.getText().toString();
        String nationality = s_profile_nationality.getSelectedItem().toString();

        writeNewProfile(profileId, identity_card, firstName, lastname,photo, nationality, phone);

        if( mPhotoUri !=null)
            updateUserPhoto(user.getUid(),mPhotoUri);
        else
            hideProgressBar();
        this.finish();
    }

    private String writeNewProfile(String uid, String identityCard, String firstName,String lastName,String photo,String nationality,String phone){

        String key = uid;
        if(key.equals("0"))
            key = getUid();
        Profile profile =new Profile(key,identityCard,firstName,lastName,photo,nationality,phone);
        Map<String, Object> postValues = profile.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/profiles/"+key, postValues);

        mDatabase.updateChildren(childUpdates);
        return key;
    }

    private void uploadImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditActivity.this);
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

    private void updateUserPhoto(final String uid, Uri photoUri){

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
            try {
                Picasso.with(ProfileEditActivity.this)
                        .load(mPhotoUri)
                        .placeholder(R.drawable.ic_person_white)
                        .transform(new PicassoCircleTransformation())
                        .into(iv_profile_image);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(requestCode == REQUEST_PHOTO_PICK && resultCode == RESULT_OK){
            mPhotoUri = data.getData();
            try {
                Picasso.with(ProfileEditActivity.this)
                        .load(mPhotoUri)
                        .placeholder(R.drawable.ic_person_white)
                        .transform(new PicassoCircleTransformation())
                        .into(iv_profile_image);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(et_profile_identity_card.getText().toString())) {
            et_profile_identity_card.setError("Required");
            result = false;
        } else {
            et_profile_identity_card.setError(null);
        }

        if (TextUtils.isEmpty(et_profile_first_name.getText().toString())) {
            et_profile_first_name.setError("Required");
            result = false;
        } else {
            et_profile_first_name.setError(null);
        }

        if (TextUtils.isEmpty(et_profile_last_name.getText().toString())) {
            et_profile_last_name.setError("Required");
            result = false;
        } else {
            et_profile_last_name.setError(null);
        }

        return result;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
    }
}