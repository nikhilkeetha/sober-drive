package com.sober.drive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText PhnNo,etName;
    CardView signIn;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NM = "myPref";
    private static final String KEY_NM = "name";
    private static final String KEY_PHONENO = "number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing
        PhnNo = findViewById(R.id.input_phn_no);
        etName=findViewById(R.id.input_nm);
        signIn = findViewById(R.id.signIn);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NM,MODE_PRIVATE);

        //when Sign-in button clicked
        signIn.setOnClickListener(v -> {

            if(PhnNo.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if(PhnNo.getText().toString().length() < 10) {
                Toast.makeText(LoginActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etName.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            }
            Intent i = new Intent(LoginActivity.this, OtpActivity.class);
            i.putExtra("PhnNo", PhnNo.getText().toString());
            i.putExtra("Name", etName.getText().toString());
            startActivity(i);
        });


    }

    private void sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + PhnNo.getText().toString().trim(),
                60,
                TimeUnit.SECONDS,
                LoginActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {  // if verified with out OTP then opens home activity & saves preferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_NM, etName.getText().toString());
                        editor.putString(KEY_PHONENO, PhnNo.getText().toString());
                        editor.apply();

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.d("TAG", e.toString());
                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Intent intent = new Intent(getApplicationContext(),OtpActivity.class);
                        intent.putExtra("verificationId",verificationId);
                        intent.putExtra("PhnNo", PhnNo.getText().toString());
                        intent.putExtra("Name", etName.getText().toString());
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}