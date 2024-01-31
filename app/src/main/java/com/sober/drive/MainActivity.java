package com.sober.drive;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NM = "myPref";
    private static final String KEY_NM = "name";
    private static final String KEY_PHONENO = "number";

    TextView tvPickup,tvDest;
    String pickName,destName;
    LatLng pickLatlng=null,destLatlng=null;

    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing variables
        tvDest = findViewById(R.id.tv_dest);
        tvPickup = findViewById(R.id.tv_pickup);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NM, MODE_PRIVATE);

        //Initializing MapView
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        //Logout
        ImageView ivLogout = findViewById(R.id.iv_logout);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        //Pickup Button
        CardView cvPickUp = findViewById(R.id.cv_pickup);
        cvPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LocationPickerActivity.class);
                i.putExtra("isPickup", true);
                startActivityForResult(i, 69);
            }
        });

        //Destination Button
        CardView cvDestination = findViewById(R.id.cv_dest);
        cvDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LocationPickerActivity.class);
                i.putExtra("isPickup", false);
                i.putExtra("latlng", pickLatlng);
                startActivityForResult(i, 96);
            }
        });

        //Next Button
        RelativeLayout rv = findViewById(R.id.btn_next);
        rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickLatlng!=null && destLatlng!=null) {
                    Intent i = new Intent(MainActivity.this, ConfirmBookingActivity.class);
                    i.putExtra("pic_name", pickName);
                    i.putExtra("dest_name", destName);
                    i.putExtra("pickLatlng", pickLatlng);
                    i.putExtra("destLatlng", destLatlng);
                    startActivity(i);
                }else {
                    Toast.makeText(MainActivity.this, "Please select Pickup & Drop locations", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 69) {
            if (resultCode == RESULT_OK) {
                // Retrieve the data from the intent
                String resultData = data.getStringExtra("name");
                pickName=resultData;
                tvPickup.setText(resultData);
                pickLatlng = data.getParcelableExtra("latlng");
            } else if (resultCode == RESULT_CANCELED) {
                // Handle the case where the second activity was canceled
            }
        } else if (requestCode == 96) {
            if (resultCode == RESULT_OK) {
                // Retrieve the data from the intent
                String resultData = data.getStringExtra("name");
                destName=resultData;
                destLatlng= data.getParcelableExtra("latlng");
                tvDest.setText(resultData);
            } else if (resultCode == RESULT_CANCELED) {
                // Handle the case where the second activity was canceled
            }
        }
    }


    private void logout()
    {
        //Clearing user data from the device and logging out
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        finishAffinity(); //after logout closes the application

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); //when back button pressed it will close the application
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        float zoomLevel = 14.0f;

        Bitmap customMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.driver_marker);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(customMarkerBitmap, 150, 150, false);

        LatLng markerPosition = new LatLng(17.385044, 78.486671);

        // Add the custom driver location on the map
        mMap.addMarker(new MarkerOptions()
                .position(markerPosition)
                .title("  ")
                .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(17.388807377173, 78.4888100))
                .title("  ")
                .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));


        // Move the camera to the marker position
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, zoomLevel));


    }
}