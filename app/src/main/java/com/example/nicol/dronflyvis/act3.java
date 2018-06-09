package com.example.nicol.dronflyvis;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdate;
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

    private ImageButton infobuch;
    private GoogleMap mMap;
    Marker startMarker;
    Circle sichtFeld;

    private float pos;
    private float zoom;
    private double lat;
    private double lng;
    private int mapType;
    private float[] settings;
    private boolean inputOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act3);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getIntent().getExtras() != null)
        {
            pos = getIntent().getExtras().getFloat("com.example.nicol.dronflyvis.BEARING");
            zoom = getIntent().getExtras().getFloat("com.example.nicol.dronflyvis.ZOOM");
            lat = Double.parseDouble(getIntent().getExtras().getString("com.example.nicol.dronflyvis.LAT"));
            lng = Double.parseDouble(getIntent().getExtras().getString("com.example.nicol.dronflyvis.LNG"));
            mapType = getIntent().getExtras().getInt("com.example.nicol.dronflyvis.MapType");
            settings = getIntent().getExtras().getFloatArray("com.example.nicol.dronflyvis.SETTINGS");
        }

        infobuch = (ImageButton)findViewById(R.id.infobuch3);
        infobuch.setImageResource(R.drawable.infobuch);
        infobuch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),buch_act.class);
                startActivity(intent);
            }
        });
    }



    // Erledige die Sachen mit Setonclicklistener, animate Cammera und und...
    //also alles was vor dem laden Der Actyvity passieren soll
    // ****************************************************************************************************************************
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled (false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (mMap != null)
        {
           mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
           {
               @Override
               public void onMapClick(LatLng latLng)
               {
                   act3.this.setMarker("Local",latLng.latitude, latLng.longitude);
               }
           });
        }
        mMap.setMapType(mapType);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {


            @Override
            public void onMarkerDragStart(Marker marker) {

                double nlat = marker.getPosition().latitude;
                double nlng = marker.getPosition().longitude;
                LatLng ll = marker.getPosition();
                marker.setSnippet("lat : " +ll.latitude+ " lng :" +ll.longitude+"");
                marker.showInfoWindow();

                if(sichtFeld!=null){
                    sichtFeld.remove();
                    sichtFeld = null;
                }
                sichtFeld = drawCircle(new LatLng(nlat, nlng));
            }

            @Override
            public void onMarkerDrag(Marker marker) {

                double nlat = marker.getPosition().latitude;
                double nlng = marker.getPosition().longitude;

                if(sichtFeld!=null){
                    sichtFeld.remove();
                    sichtFeld = null;
                }
                sichtFeld = drawCircle(new LatLng(nlat, nlng));

                LatLng ll = marker.getPosition();
                marker.setSnippet("lat : " +ll.latitude+ " lng :" +ll.longitude+"");
                marker.showInfoWindow();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                double nlat = marker.getPosition().latitude;
                double nlng = marker.getPosition().longitude;

                if(sichtFeld!=null){
                    sichtFeld.remove();
                    sichtFeld = null;
                }
                sichtFeld = drawCircle(new LatLng(nlat, nlng));
                marker.hideInfoWindow();

            }
        });
        // ****************************************************************************************************************************




        // Change Button
        // ****************************************************************************************************************************
        Button searchButton = (Button)findViewById(R.id.change3);
        // Normales Click
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch(mMap.getMapType())
                {
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
        });

        registerForContextMenu(searchButton);
        searchButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });


    }
    //Context menu für change Button
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.change_button_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case(R.id.change_opt_1):
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case(R.id.change_opt_2):
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case(R.id.change_opt_3):
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case(R.id.change_opt_4):
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    // ****************************************************************************************************************************
    // Setmarker methode für Startmarker mit dem Circle um den sPunkt
    // Es Kann nur ein Startmarker gebe, also zus löschen marker+circle drin
    // ****************************************************************************************************************************
    private void setMarker(String locality, double lat, double lng)
    {
        if(startMarker!= null)
        {
            removeMarkerandCircle();

        }
        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .draggable(true)
                .position(new LatLng(lat,lng))
                .snippet("StartPos")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerstart))
                .anchor((float)0.5, (float)0.5);

        startMarker= mMap.addMarker(options);
        sichtFeld = drawCircle(new LatLng(lat, lng));

    }

    private Circle drawCircle(LatLng latLng)
    {
        CircleOptions options = new CircleOptions()
                .center(latLng)
                .radius(300)
                .fillColor(0x66696969)
                .strokeColor(Color.RED)
                .strokeWidth(3);

        return mMap.addCircle(options);
    }

    private void removeMarkerandCircle()
    {
        startMarker.remove();
        startMarker = null;
        sichtFeld.remove();
        sichtFeld = null;
    }


    //zwischen den Acticitys
    // ****************************************************************************************************************************
    public void act_3_next(View view)
    {
        if(startMarker == null)
        {
            Warning warning = new Warning("You have to set a start Marker", "Please set start", false, "OK", this);
            AlertDialog alertDialog = warning.createWarning();
            alertDialog.setTitle("Missing StartMarker");
            alertDialog.show();
            return;
        }
        inputOk = true;

        Intent intent = new Intent(this, act4.class);
        if(inputOk)
        {
            Node startNode = new Node(startMarker.getPosition().latitude, startMarker.getPosition().longitude,0);
            intent.putExtra("com.example.nicol.dronflyvis.MARKER_LNG", startMarker.getPosition().longitude);
            intent.putExtra("com.example.nicol.dronflyvis.MARKER_LAT", startMarker.getPosition().latitude);
        }

        intent.putExtra("com.example.nicol.dronflyvis.ACT2BEARING", mMap.getCameraPosition().bearing);
        intent.putExtra("com.example.nicol.dronflyvis.mapZOOM", mMap.getCameraPosition().zoom);
        intent.putExtra("com.example.nicol.dronflyvis.mapLAT","" + mMap.getCameraPosition().target.latitude);
        intent.putExtra("com.example.nicol.dronflyvis.mapLNG","" + mMap.getCameraPosition().target.longitude);
        intent.putExtra("com.example.nicol.dronflyvis.mapType", mMap.getMapType());
        intent.putExtra("com.example.nicol.dronflyvis.SETTINGS", settings);

        startActivity(intent);
    }

    public void act_3_back(View view)
    {
        onBackPressed();
    }
}

