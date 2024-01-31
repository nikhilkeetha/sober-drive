package com.sober.drive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class OtpActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NM = "myPref";
    private static final String KEY_NM = "name";
    private static final String KEY_PHONENO = "number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        EditText otp = findViewById(R.id.input_otp);
        CardView cvVerify = findViewById(R.id.verify);

        String Name = getIntent().getStringExtra("Name");
        String PhoneNo = getIntent().getStringExtra("PhnNo");

        sharedPreferences = getSharedPreferences(SHARED_PREF_NM,MODE_PRIVATE);

        cvVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().trim().isEmpty()) {
                    Toast.makeText(OtpActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (otp.getText().toString().length() != 6) {
                    Toast.makeText(OtpActivity.this, "Please enter a valid OTP.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = otp.getText().toString().trim();
                if(code.equals("123456"))
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_NM, Name);
                    editor.putString(KEY_PHONENO, PhoneNo);
                    editor.apply();

                    Intent i = new Intent(OtpActivity.this, MainActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(OtpActivity.this, "Please enter a valid OTP.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}