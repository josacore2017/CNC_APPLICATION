package com.josacore.cncpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.josacore.cncpro.classes.Profile;
import com.josacore.cncpro.classes.User;

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends BaseActivity {

    private final static String TAG = "VerifyActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private TextView tv_veryfy_phone_numer;
    private TextView tv_verify_resend_sms;
    private TextView tv_verify_wrong_number;
    private EditText et_verify_sms_code;
    private Button btn_verify_verify;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private boolean mVerificationInProgress = false;

    private String numberPhone="";
    private String mVerificationId;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        context = getBaseContext();

        Bundle bunble = getIntent().getExtras();
        numberPhone = bunble.getString("phone");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        tv_veryfy_phone_numer = findViewById(R.id.tv_verify_phone_number);
        tv_verify_resend_sms = findViewById(R.id.tv_verify_resend_sms);
        tv_verify_wrong_number = findViewById(R.id.tv_verify_wrong_number);
        et_verify_sms_code = findViewById(R.id.et_verify_sms_code);
        btn_verify_verify = findViewById(R.id.btn_verify_verify);

        tv_veryfy_phone_numer.setText("Verificar + (591) "+numberPhone);
        tv_verify_resend_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(mResendToken);
            }
        });
        tv_verify_wrong_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_verify_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = et_verify_sms_code.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    et_verify_sms_code.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.e(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;

                signInWithPhoneAuthCredential(credential);

                //updateUI(STATE_VERIFY_SUCCESS, credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    /*new AlertDialog.Builder(context)
                            .setMessage("Numero de Telefono Invalido?")
                            .setCancelable(false)
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    onBackPressed();
                                }
                            })
                            .show();*/
                    Log.e(TAG,"invalido");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Se ha excedido la cuota de SMS para el proyecto.", Snackbar.LENGTH_SHORT).show();
                    Log.e(TAG,"Se ha excedido la cuota de SMS para el proyecto.");
                    Log.e(TAG,e.getMessage());

                }
                //updateUI(STATE_VERIFY_FAILED);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                //updateUI(STATE_CODE_SENT);
            }
        };

        startPhoneNumberVerification();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) FirebaseAuth.getInstance().signOut();//existe ussuario
        else Log.e(TAG,"no existe usuario");
        //updateUI(currentUser);
        if (mVerificationInProgress) {
            startPhoneNumberVerification();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    FirebaseUser user = task.getResult().getUser();
                    onAuthSuccess(user);
                    /*Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                    intent.putExtra("login_with","phone");
                    startActivity(intent);
                    finish();*/
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        /*new AlertDialog.Builder(context)
                                .setMessage(task.getException().getMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        onBackPressed();
                                    }
                                })
                                .show();
                        */
                        Log.e(TAG,task.getException().getMessage());
                    }
                    //updateUI(STATE_SIGNIN_FAILED);
                }
            }
        });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());
        writeNewProfile(user.getUid(), "", "","","","","","");
        // Go to MainActivity
        startActivity(new Intent(VerifyActivity.this, MainActivity.class));
        finish();
    }
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private void writeNewProfile(String uid, String identityCard, String firstName,String lastName,String photo,String dateBirthday,String nationality,String phone){
        Profile profile =new Profile(uid,identityCard,firstName,lastName,photo,nationality,phone);
        mDatabase.child("profiles").child(uid).setValue(profile);
        Log.e(TAG,"XXXXXXXXXXXXXXX profile login: "+uid);
    }
    private void startPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+591"+numberPhone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        mVerificationInProgress = true;
    }

    private void resendVerificationCode(PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+591"+numberPhone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
}
