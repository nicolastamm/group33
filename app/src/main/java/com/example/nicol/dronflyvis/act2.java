package com.example.nicol.dronflyvis;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class act2 extends FragmentActivity implements OnMapReadyCallback
{
    ImageButton infobuch;
    private GoogleMap mMap;
    private LatLng latLng;
    private float[] settings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        settings = getIntent().getExtras().getFloatArray("com.example.nicol.dronflyvis.INPUT_VALUES");


        /**
         * Also hier mal eine komplette Überarbeiung des change Buttons
         *
         * 1.
         * Die Möglichkeit per click die View zu ändern habe ich drin gelassen
         * und diese Funktioniert auch wie früher
         * 2.
         * Dazu kamm die Möglichkeit mit onLongClickListner
         * Drückt man länger auf change Button, dann wird eine Menu ausgegeben mit allen der
         * Möglichkeit der Views.
         *
         * 3.
         * Um das Machen zu können musste ich noch ein Resoursen Folder erstellen Menu
         * Da können eigentlich auch alle unsere Menü Xmls rein
         * Also Hilmi das kannst du dir anschauen wenn du willst:)
         *  nur die Item-Tags sind wichtig(das die da stehen) aber wie die aussehen kannst du dich darüber kümmern
         *
         *  Falls die Menü idee gut ist, wir tuen den Code einfach überall kopieren.
         *
         *  VG Android Integrator :)
         * */

        //Der Searchbutton und alles was dazugehört
        // ****************************************************************************************************************************

        Button searchButton = (Button)findViewById(R.id.change2);

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

        //option mit menu
        registerForContextMenu(searchButton);
        searchButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });




        infobuch = (ImageButton)findViewById(R.id.infobuch2);
        infobuch.setImageResource(R.drawable.infobuch);
        infobuch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),buch_act.class);
                startActivity(intent);
            }
        });


        PlaceAutocompleteFragment placesFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        placesFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place)
            {

                latLng = place.getLatLng();
                CameraUpdate nloc = CameraUpdateFactory.newLatLngZoom(
                        latLng, 10);

                mMap.animateCamera(nloc);
            }
            @Override
            public void onError(Status status)
            {
                Log.i("error","" + status);
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


    // bis es alles eingezeigt wird erledigie
    // ****************************************************************************************************************************
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled (false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
    // ****************************************************************************************************************************



    // zwischen Activitys
    // ****************************************************************************************************************************
    public void act2back(View view)
    {
        onBackPressed();
    }

    public void act_2_next(View view)
    {

        Intent intent = new Intent(this, act3.class);
        intent.putExtra("com.example.nicol.dronflyvis.BEARING", mMap.getCameraPosition().bearing);
        intent.putExtra("com.example.nicol.dronflyvis.ZOOM",mMap.getCameraPosition().zoom);
        intent.putExtra("com.example.nicol.dronflyvis.LAT",  "" + mMap.getCameraPosition().target.latitude);
        intent.putExtra("com.example.nicol.dronflyvis.LNG","" + mMap.getCameraPosition().target.longitude);
        intent.putExtra("com.example.nicol.dronflyvis.MapType", mMap.getMapType());
        intent.putExtra("com.example.nicol.dronflyvis.SETTINGS", settings);
        startActivity(intent);

    }
}

