package com.sober.drive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences; // initilize shared preferences to check is user signed in or not

    private static final String SHARED_PREF_NM = "myPref"; //is signed in Name
    private static final String KEY_PHONENO = "number";  //if signed in user phone number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NM,MODE_PRIVATE);

        String phoneNm = sharedPreferences.getString(KEY_PHONENO,null);

        new Handler().postDelayed(() -> {
            if (phoneNm != null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }, 2000);

    }
}