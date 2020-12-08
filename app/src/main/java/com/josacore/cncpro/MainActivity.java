package com.josacore.cncpro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.josacore.cncpro.utils.BaseFragment;
import com.josacore.cncpro.utils.DrawerLocker;
import com.josacore.cncpro.utils.PicassoCircleTransformation;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends BaseActivity implements BaseFragment.BaseFragmentCallbacks, DrawerLocker {

    private final String TAG = "MainActivity";

    private Context context;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView tv_name_nav;
    private TextView tv_email_nav;
    private ImageView iv_image_nav;

    private View headerView;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private Toolbar mToolbar;

    private String type_user;
    private String login_with = "";
    private String cncId = "";
    private String profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context =this;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                login_with= "";
            } else {
                login_with= extras.getString("login_with");
            }
        } else {
            login_with= (String) savedInstanceState.getSerializable("login_with");
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView = findViewById(R.id.nav_view);

        headerView = navigationView.getHeaderView(0);

        tv_name_nav = (TextView) headerView.findViewById(R.id.tv_drawer_profile_name);
        tv_email_nav = (TextView) headerView.findViewById(R.id.tv_drawer_profile_mail);
        iv_image_nav = (ImageView) headerView.findViewById(R.id.iv_drawer_profile_photo);

        Log.e(TAG,mAuth.getCurrentUser().getUid());
        if(mAuth.getCurrentUser().getEmail()!=null)
            if (mAuth.getCurrentUser().getEmail().equals(""))
                if(mAuth.getCurrentUser().getPhoneNumber()!=null)
                    tv_email_nav.setText(mAuth.getCurrentUser().getPhoneNumber());
        if(mAuth.getCurrentUser().getPhoneNumber()!=null)
            if (mAuth.getCurrentUser().getPhoneNumber().equals(""))
                if(mAuth.getCurrentUser().getEmail()!=null)
                    tv_email_nav.setText(mAuth.getCurrentUser().getEmail());
        if(mAuth.getCurrentUser().getDisplayName()!=null)
            tv_name_nav.setText(mAuth.getCurrentUser().getDisplayName());


        iv_image_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);

                Pair<View,String> pairImage = Pair.create(findViewById(R.id.iv_drawer_profile_photo),"pairImage");
                Pair<View,String> pairName = Pair.create(findViewById(R.id.tv_drawer_profile_name),"pairName");
                Pair<View,String> pairEmail = Pair.create(findViewById(R.id.tv_drawer_profile_mail),"pairEmail");

                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(MainActivity.this, pairImage,pairName,pairEmail);
                startActivity(intent,optionsCompat.toBundle());

            }
        });

        if(mAuth.getCurrentUser().getUid() != null) Log.e(TAG,"getUid: "+mAuth.getCurrentUser().getUid());
        if(mAuth.getCurrentUser().getEmail() != null) Log.e(TAG,"getEmail: "+mAuth.getCurrentUser().getEmail());
        if(mAuth.getCurrentUser().getDisplayName() != null) Log.e(TAG,"getDisplayName: "+mAuth.getCurrentUser().getDisplayName());
        if(mAuth.getCurrentUser().getPhoneNumber() != null) Log.e(TAG,"getPhoneNumber: "+mAuth.getCurrentUser().getPhoneNumber());
        if(mAuth.getCurrentUser().getProviderId() != null) Log.e(TAG,"getProviderId: "+mAuth.getCurrentUser().getProviderId());


        mDatabase.child("profiles").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profileId = dataSnapshot.child("uid").getValue(String.class);
                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String photo = dataSnapshot.child("photo").getValue(String.class);

                tv_name_nav.setText(firstName+" "+lastName);

                Log.e(TAG,firstName+" "+lastName);

                try {
                    Log.e(TAG, photo);
                    Picasso.with(MainActivity.this)
                            .load(photo)
                            .placeholder(R.drawable.ic_person_white)
                            .transform(new PicassoCircleTransformation())
                            .fit()
                            .into(iv_image_nav);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_fragment_control, R.id.nav_fragment_adding, R.id.nav_fragment_listing)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                String action ="";
                switch (menuItem.getItemId()) {
                    case R.id.nav_menu_drawer_listing:
                        action ="listing";
                        if(isValidDestination(R.id.nav_fragment_listing)) {
                            navController.navigate(R.id.nav_fragment_listing);
                            setCheckMenuDrawer("listing");
                        }
                        break;
                    case R.id.nav_menu_drawer_control:

                        if(isValidDestination(R.id.nav_fragment_control)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("deviceId", cncId);
                            navController.navigate(R.id.nav_fragment_control,bundle);
                            setCheckMenuDrawer("control");
                        }
                        break;
                    case R.id.nav_menu_drawer_adding:
                        if(isValidDestination(R.id.nav_fragment_adding)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("deviceId", cncId);
                            navController.navigate(R.id.nav_fragment_adding,bundle);
                            setCheckMenuDrawer("control");
                        }
                        break;
                    case R.id.nav_menu_drawer_logout: {
                        action ="logout";
                        new AlertDialog.Builder(context)
                                .setMessage("Salir de su cuenta?")
                                .setCancelable(false)
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(getApplicationContext(), PhoneAuthActivity.class));
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                }
                setCheckMenuDrawer(action);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        Toast.makeText(this, "login_with: "+login_with, Toast.LENGTH_LONG).show();
        Log.e(TAG,"Login desde: "+login_with);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void setCNCId(String cncId){
        this.cncId = cncId;
    }
    public boolean isValidDestination(int destiantion){
        return destiantion != navController.getCurrentDestination().getId();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(context)
                    .setMessage("Seguro que deseas Salir?")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            //super.onBackPressed();

        }
    }
    public void setTitleSubtitle(String title, String Subtitle){
        mToolbar.setTitle(title);
        mToolbar.setSubtitle(Subtitle);
    }
    public void setCheckMenuDrawer(String action){
        Menu menuNav = navigationView.getMenu();
        MenuItem nav_menu_drawer_listing = (MenuItem) menuNav.findItem(R.id.nav_menu_drawer_listing);
        nav_menu_drawer_listing.setChecked(false);
        MenuItem nav_menu_drawer_control = (MenuItem) menuNav.findItem(R.id.nav_menu_drawer_control);
        nav_menu_drawer_control.setChecked(false);
        MenuItem nav_menu_drawer_adding = (MenuItem) menuNav.findItem(R.id.nav_menu_drawer_adding);
        nav_menu_drawer_adding.setChecked(false);
        MenuItem nav_menu_drawer_logout = (MenuItem) menuNav.findItem(R.id.nav_menu_drawer_logout);
        nav_menu_drawer_logout.setChecked(false);

        switch (action) {
            case "listing":
                nav_menu_drawer_listing.setChecked(true);
                break;
            case "control":
                nav_menu_drawer_control.setChecked(true);
                break;
            case "adding":
                nav_menu_drawer_adding.setChecked(true);
                break;
            case "logout":
                nav_menu_drawer_logout.setChecked(true);
                break;
        }
    }
    @Override
    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        mDrawerLayout.setDrawerLockMode(lockMode);
        //toggle.setDrawerIndicatorEnabled(enabled);
    }
    public void enableFunctionsMenu(){
        Menu menuNav = navigationView.getMenu();
        MenuItem nav_menu_drawer_listing = (MenuItem) menuNav.findItem(R.id.nav_menu_drawer_funtions);
        nav_menu_drawer_listing.setVisible(true);
    }
    public void disableFunctionsMenu(){
        Menu menuNav = navigationView.getMenu();
        MenuItem nav_menu_drawer_listing = (MenuItem) menuNav.findItem(R.id.nav_menu_drawer_funtions);
        nav_menu_drawer_listing.setVisible(false);
    }
}