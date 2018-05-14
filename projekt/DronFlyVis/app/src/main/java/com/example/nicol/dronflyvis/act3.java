package com.example.nicol.dronflyvis;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class act3 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker startMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act3);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
           mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
               @Override
               public void onMapClick(LatLng latLng) {
                   act3.this.setMarker("Local",latLng.latitude, latLng.longitude);
               }
           });
        }
    }



    Circle sichtFeld;
    private void setMarker(String locality, double lat, double lng){
        if(startMarker!= null){
            removeMarkerandCircle();

        }
        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .draggable(true)
                .position(new LatLng(lat,lng))
                .snippet("StartPos");


        startMarker= mMap.addMarker(options);
        sichtFeld = drawCircle(new LatLng(lat, lng));

    }

    private Circle drawCircle(LatLng latLng) {
        CircleOptions options = new CircleOptions()
                .center(latLng)
                .radius(500)
                .fillColor(0x33FF0000)
                .strokeColor(Color.BLUE)
                .strokeWidth(3);
        return mMap.addCircle(options);
    }

    private void removeMarkerandCircle(){
        startMarker.remove();
        startMarker = null;
        sichtFeld.remove();
        sichtFeld = null;
    }

    public void act_3_next(View view){
        Intent Intent = new Intent(this, act4.class);
        // map man
        startActivity(Intent);
    }

    public void act_3_back(View view){
        Intent Intent = new Intent(this, act2.class);
        // map man
        startActivity(Intent);
    }
}

