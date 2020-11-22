package com.josacore.cncpro;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.josacore.cncpro.utils.PicassoCircleTransformation;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends BaseActivity {

    private TextView tv_profile_name;
    private TextView tv_profile_email;
    private TextView tv_profile_identity_card;
    private TextView tv_profile_phone_number;
    private TextView tv_profile_nationality;
    private ImageView iv_image_nav;

    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private String profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        tv_profile_name = findViewById(R.id.tv_profile_name);
        tv_profile_email = findViewById(R.id.tv_profile_email);
        tv_profile_identity_card = findViewById(R.id.tv_profile_identity_card);
        tv_profile_phone_number = findViewById(R.id.tv_profile_phone_number);
        tv_profile_nationality = findViewById(R.id.tv_profile_nationality);
        iv_image_nav = findViewById(R.id.iv_profile_image);
        tv_profile_email.setText(user.getEmail());

        mDatabase.child("profiles").child(getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profileId = dataSnapshot.child("uid").getValue(String.class);
                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String identityCard = dataSnapshot.child("identityCard").getValue(String.class);
                String phone = dataSnapshot.child("phone").getValue(String.class);
                String photo = dataSnapshot.child("photo").getValue(String.class);
                String nationality = dataSnapshot.child("nationality").getValue(String.class);

                tv_profile_name.setText(firstName+" "+lastName);
                tv_profile_identity_card.setText(identityCard);
                tv_profile_phone_number.setText(phone);
                tv_profile_phone_number.setText(phone);
                tv_profile_nationality.setText(nationality);

                try {
                    Picasso.with(ProfileActivity.this)
                            .load(photo)
                            .placeholder(R.drawable.ic_person_white)
                            .transform(new PicassoCircleTransformation())
                            .into(iv_image_nav);
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
        getMenuInflater().inflate(R.menu.menu_activity_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {

            case R.id.action_profile_edit: {
                Intent intent = new Intent(ProfileActivity.this,ProfileEditActivity.class);
                Bundle mBundle = new Bundle();
                if(profileId == null) profileId ="0";
                Log.e("VVVVVVVVVVV","********: "+profileId);
                mBundle.putString("profileId", profileId);
                intent.putExtras(mBundle);
                startActivity(intent);
                return true;
            }
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}
