package com.sober.drive;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LocationPickerActivity extends AppCompatActivity {

    String strPick="Pickup Location : ";

    RelativeLayout btnNext;
    TextView tv;

    String selectedPlaceNm="Unknown Place";
    LatLng selectedPlaceLatlng=new LatLng(17.385044,78.486671);
    LatLng old=null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        btnNext = findViewById(R.id.btn_next);
        tv = findViewById(R.id.tv_location);

        boolean isPickup = getIntent().getBooleanExtra("isPickup",true);
        if(isPickup==false)
        {
            btnNext.setBackgroundTintList(getResources().getColorStateList(R.color.red));
            strPick="Destination Location : ";
            tv.setText("Mark as Destination");
            old=getIntent().getParcelableExtra("latlng");

        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPickup==true)
                {
                    Intent i = new Intent();
                    i.putExtra("name", selectedPlaceNm);
                    i.putExtra("latlng", selectedPlaceLatlng);
                    setResult(RESULT_OK, i);
                    finish();
                }
                else {
                    //Toast.makeText(LocationPickerActivity.this, old.toString()+"---"+selectedPlaceLatlng.toString(), Toast.LENGTH_SHORT).show();
                    if (old!=null)
                    {
                        if (old.latitude==selectedPlaceLatlng.latitude && old.longitude==selectedPlaceLatlng.longitude)
                        {
                            Toast.makeText(getApplicationContext(), "Please a different location away from pickup", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent i = new Intent();
                            i.putExtra("name", selectedPlaceNm);
                            i.putExtra("latlng", selectedPlaceLatlng);
                            setResult(RESULT_OK, i);
                            finish();
                        }
                    }

                }

            }
        });

        MapView mMapView = findViewById(R.id.locationPickerView);
        MapsInitializer.initialize(this);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();



        mMapView.getMapAsync(googleMap -> {
            LatLng posisiabsen = new LatLng(17.385044, 78.486671);

            float zoomLevel = 15.0f;

            googleMap.addMarker(new MarkerOptions().position(posisiabsen).title(strPick+"Hyderabad"));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posisiabsen, zoomLevel));
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(strPick+String.valueOf(latLng)));
                    selectedPlaceLatlng=latLng;
                    selectedPlaceNm="Unknown Place";
                }
            });

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);


        });

        // Inside your MainActivity or the Activity where you use Places SDK
        Places.initialize(getApplicationContext(), "AIzaSyDIk7RkWhunJbIqFM_qSUstjZxmyW7DaNo");


        //@SuppressLint("MissingInflatedId") EditText editText = findViewById(R.id.et_search);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                selectedPlaceNm=place.getName();

                Geocoder geocoder = new Geocoder(LocationPickerActivity.this);

                try {
                    List<Address> addresses = geocoder.getFromLocationName(String.valueOf(place.getName()), 1);

                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        selectedPlaceLatlng=latLng;

                        // Add marker to the map
                        Toast.makeText(LocationPickerActivity.this, latLng.toString(), Toast.LENGTH_SHORT).show();
                        mMapView.getMapAsync(googleMap -> {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(latLng)
                                    .title(strPick + place.getName());
                            googleMap.clear();
                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.5f));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                        });
                    } else {
                        Toast.makeText(LocationPickerActivity.this, "Location not found, Try marking on map manually.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(LocationPickerActivity.this, "Error geocoding location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });







    }
}