package com.sober.drive;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuccessfulyBookedActivity extends AppCompatActivity {

    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successfuly_booked);

        //animation
        LottieAnimationView animationView = findViewById(R.id.sucessAnim);
        animationView.setScale(1.0f);
        animationView.setAnimation(R.raw.driver_anim);
        animationView.setRepeatCount(LottieDrawable.INFINITE);
        animationView.playAnimation();

        //Intializing Views
        TextView tv_pick = findViewById(R.id.tv_pick);
        TextView tv_des = findViewById(R.id.tv_des);
        TextView tv_fare = findViewById(R.id.fare);


        int id = getIntent().getIntExtra("key", 0);
        String pickName = getIntent().getStringExtra("pickName");
        String destName = getIntent().getStringExtra("destName");
        String fare = getIntent().getStringExtra("fare");
        tv_pick.setText(pickName);
        tv_des.setText(destName);
        tv_fare.setText(fare);
        //Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();

        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("drivers");

// Assuming userKey is the unique key for the user you want to retrieve
        String userKey = String.valueOf(id);  // Replace with the actual key for the user you want

        DatabaseReference userRef = usersRef.child(userKey);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Ensure the dataSnapshot exists and has children
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    // Retrieve user data
                    String name = dataSnapshot.child("nm").getValue().toString();
                    number = dataSnapshot.child("num").getValue().toString();
                    String profilePicUrl = dataSnapshot.child("pic").getValue().toString();

                    //Toast.makeText(SuccessfulyBookedActivity.this, name+number+profilePicUrl, Toast.LENGTH_LONG).show();

                    CircleImageView ivDriver = findViewById(R.id.iv_driver);
                    Glide.with(SuccessfulyBookedActivity.this)
                            .load(profilePicUrl)
                            .into(ivDriver);
                    TextView tvName = findViewById(R.id.tv_name);
                    tvName.setText(name);
                    // Now you have the name, number, and profile picture URL
                    Log.d(TAG, "Name: " + name);
                    //Log.d(TAG, "Number: " + number);
                    //Log.d(TAG, "Profile Picture URL: " + profilePicUrl);

                    // TODO: Do something with the retrieved data
                } else {
                    Log.e(TAG, "User data not found for key: " + userKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
                Log.w(TAG, "Failed to read user data.", error.toException());
            }
        });


        RelativeLayout rv_call = findViewById(R.id.rv_call);
        rv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + number));
                startActivity(i);

            }
        });

        RelativeLayout sos = findViewById(R.id.btn_sos);
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("tel:" + 100));
                startActivity(i);
            }
        });

        RelativeLayout rv_message = findViewById(R.id.rv_message);
        rv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("sms:" + number));
                startActivity(i);
            }
        });

    }
}