package com.example.nicol.dronflyvis;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class act2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void search(View view){
        EditText searchloc = (EditText)findViewById(R.id.texttosearch);
        String location = searchloc.getText().toString();
        List<Address> addressList = null;
        if(location != null || location.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location,1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());

            CameraUpdate nloc = CameraUpdateFactory.newLatLngZoom(
                    latLng, 10);
            mMap.animateCamera(nloc);


        }

    }

    public void changetype(View view){
        switch(mMap.getMapType()){
            case GoogleMap.MAP_TYPE_NORMAL:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case GoogleMap.MAP_TYPE_SATELLITE:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case GoogleMap.MAP_TYPE_TERRAIN:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case GoogleMap.MAP_TYPE_HYBRID:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

        }
    }

    public void act2back(View view){
        Intent Intent = new Intent(this, act1.class);
        startActivity(Intent);
    }

    public void act_2_next(View view){

        Intent Intent = new Intent(this, act3.class);
        // map man
        startActivity(Intent);
    }
}

