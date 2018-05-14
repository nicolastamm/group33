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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class act4 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Marker> markers = new ArrayList<Marker>();
    Polygon shape;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act4);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    Marker aktMarker = marker;
                    aktMarker.remove();
                    markers.remove(marker);

                    if(shape != null){

                        shape.remove();
                        shape=null;
                    }
                    drawPolygon();
                    return false;
                }
            });

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    if(shape != null){
                        shape.remove();
                        shape=null;
                    }
                    drawPolygon();

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    if(shape != null){
                        shape.remove();
                        shape=null;
                    }
                    drawPolygon();
                }
            });



            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    act4.this.setMarker("Local",latLng.latitude, latLng.longitude);
                }
            });
        }

    }

    private void setMarker(String locality, double lat, double lng){


        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .draggable(true)
                .position(new LatLng(lat,lng))
                .snippet("Pos");

        markers.add(mMap.addMarker(options));

        if(markers.size() >= 3){
            if(shape != null){
                shape.remove();
                shape=null;
            }
            drawPolygon();
        }
    }

    private void drawPolygon() {
        PolygonOptions options = new PolygonOptions()
                .fillColor(0x330000FF)
                .strokeWidth(3)
                .strokeColor(Color.GREEN);
        for(int i=0;i<markers.size();i++ ){
            if(markers.get(i) != null || markers.size()>0){
                options.add(markers.get(i).getPosition());
            }
        }
        shape = mMap.addPolygon(options);
    }

    public void act_4_next(View view){
        Intent Intent = new Intent(this, act5.class);
        // map man
        startActivity(Intent);
    }

    public void act_4_back(View view){
        Intent Intent = new Intent(this, act3.class);
        // map man
        startActivity(Intent);
    }


}
