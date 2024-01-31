package com.sober.drive;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class SearchingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        String pickName = getIntent().getStringExtra("pickName");
        String destName = getIntent().getStringExtra("destName");
        String fare = getIntent().getStringExtra("fare");
        Random random = new Random();
        int randomNumber = random.nextInt(3) + 1;
        //Toast.makeText(this, String.valueOf(randomNumber), Toast.LENGTH_SHORT).show();





        //create a 3 seconds delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, SuccessfulyBookedActivity.class);
            intent.putExtra("key", randomNumber);
            intent.putExtra("pickName", pickName);
            intent.putExtra("destName",destName);
            intent.putExtra("fare",fare);
            startActivity(intent);
        }, 5000);

/*
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("drivers");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Toast.makeText(SearchingActivity.this, "hi", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

 */




        LottieAnimationView animationView = findViewById(R.id.lottieAnimationView);
        animationView.setAnimation(R.raw.searching_animation);
        animationView.setRepeatCount(LottieDrawable.INFINITE);
        animationView.setSpeed(2.0f);
        animationView.playAnimation();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "You can't go back while searching", Toast.LENGTH_LONG).show();
    }
}