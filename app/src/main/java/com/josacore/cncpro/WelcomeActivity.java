package com.josacore.cncpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private Button btn_acept;
    private TextView tv_terms;
    private TextView tv_policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btn_acept = findViewById(R.id.btn_acetp);
        tv_terms = findViewById(R.id.tv_terms);
        tv_policy = findViewById(R.id.tv_policy);

        SpannableString terms = new SpannableString(tv_terms.getText().toString());
        terms.setSpan(new UnderlineSpan(), 0, terms.length(), 0);
        tv_terms.setText(terms);

        SpannableString policy = new SpannableString(tv_policy.getText().toString());
        policy.setSpan(new UnderlineSpan(), 0, policy.length(), 0);
        tv_policy.setText(terms);

        btn_acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, PhoneAuthActivity.class);
                startActivity(intent);
                changeAceptingTerms();
                finish();
            }
        });
    }
    public void changeAceptingTerms(){
        SharedPreferences mPreferences = this.getSharedPreferences("first_time", getBaseContext().MODE_PRIVATE);
        Boolean acceptTerm = mPreferences.getBoolean("firstTime", true);
        if (acceptTerm) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
        }
    }
}
