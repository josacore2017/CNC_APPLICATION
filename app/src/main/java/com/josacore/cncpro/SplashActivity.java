package com.josacore.cncpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends BaseActivity {

    private final static String TAG = "SplashActivity";

    private static int SPLASH_SCREEN = 4000;

    private ImageView iv_splash_logo;
    private TextView et_splash_app_name;
    private TextView et_splash_desc;

    private Animation topAnim;
    private Animation bottomAnim;

    private Boolean acceptTerm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        iv_splash_logo = findViewById(R.id.iv_splash_logo);
        et_splash_app_name =findViewById(R.id.et_splash_app_name);
        et_splash_desc =findViewById(R.id.et_splash_desc);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        iv_splash_logo.setAnimation(topAnim);
        et_splash_app_name.setAnimation(bottomAnim);
        et_splash_app_name.setAnimation(bottomAnim);

        final int[] i = {0};

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isAccepTerms()) {
                    Intent intent = new Intent(SplashActivity.this, PhoneAuthActivity.class);
                    startActivity(intent);
                    finish();

                }else{
                    Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },SPLASH_SCREEN);
    }

    private boolean isAccepTerms() {
        if (acceptTerm == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", this.MODE_PRIVATE);
            acceptTerm = mPreferences.getBoolean("firstTime", true);
        }
        return acceptTerm;
    }

}
