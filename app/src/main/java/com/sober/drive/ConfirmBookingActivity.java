package com.sober.drive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.SphericalUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;


import java.util.ArrayList;
import java.util.List;

public class ConfirmBookingActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    protected LatLng pickup, destination;
    private GoogleMap mMapView;
    String pickName,destName;

    Location pickupLocation=null;
    Location destinationLocation=null;

    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission=false;

    //polyline object
    private List<Polyline> polylines=null;
    TextView tvFare,tvTax,tvTotal;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);
        pickup=getIntent().getParcelableExtra("pickLatlng");
        destination=getIntent().getParcelableExtra("destLatlng");
        pickName=getIntent().getStringExtra("pic_name");
        destName=getIntent().getStringExtra("dest_name");

        TextView tvPick=findViewById(R.id.tv_pick);
        TextView tvDest = findViewById(R.id.tv_des);
        tvFare=findViewById(R.id.tv_driver_fare);
        tvTax=findViewById(R.id.tv_taxes);
        tvTotal=findViewById(R.id.tv_total);

        tvPick.setText(pickName);
        tvDest.setText(destName);

        requestPermision();



        @SuppressLint("WrongViewCast") RelativeLayout rv=findViewById(R.id.btn_search);
        rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmBookingActivity.this, SearchingActivity.class);
                intent.putExtra("pickName", pickName);
                intent.putExtra("destName",destName);
                intent.putExtra("fare",tvTotal.getText().toString());
                startActivity(intent);
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView2);
        mapFragment.getMapAsync(this);

        //getDirections();
        Findroutes(pickup,destination);
    }

    private void requestPermision()
    {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        else{
            locationPermission=true;
        }
    }


    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(ConfirmBookingActivity.this,"Unable to get location", Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyDIk7RkWhunJbIqFM_qSUstjZxmyW7DaNo")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMapView = googleMap;

        // Move camera to the starting point
        mMapView.moveCamera(CameraUpdateFactory.newLatLngZoom(pickup, 12));

        // Draw the route on the map
        //getDirections();
    }
    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
//        Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(ConfirmBookingActivity.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(pickup);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.primary));
                polyOptions.width(10);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMapView.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

                // Calculate the distance for the current route
                double distance = SphericalUtil.computeLength(polyline.getPoints());
                // Distance is in meters, you can convert it to kilometers if needed
                double distanceInKm = distance / 1000;
                int dis = (int) distanceInKm;
                tvFare.setText("₹ " + dis * 10);
                tvTax.setText("₹ " + dis * 0.5);
                tvTotal.setText("₹ " + (dis * 10 + dis * 0.5));

            }
            else {

            }

        }



        Bitmap customMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pin_pick);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(customMarkerBitmap, 150, 150, false);

        Bitmap customMarkerBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.pin_dest);
        Bitmap resizedBitmap2 = Bitmap.createScaledBitmap(customMarkerBitmap2, 150, 150, false);

        //Add Marker on route pickup position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("Pick Up");
        startMarker.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
        mMapView.addMarker(startMarker);

        //Add Marker on route destination position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        endMarker.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap2));
        mMapView.addMarker(endMarker);

        mMapView.moveCamera(CameraUpdateFactory.newLatLngZoom(polylineStartLatLng, 15.0f));
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(pickup,destination);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(pickup,destination);

    }
}