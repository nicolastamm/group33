package com.example.nicol.dronflyvis;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * @author Artuk
 * @author Hilmi
 * @author Nico
 * @author Heiko
 *
 * This is the main part of our app.
 * This class accomplishes the main goals of our app.
 */
public class Main_Activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Boolean deleteModus = false;
    private Boolean drawModus = true;
    private Boolean pinModus = false;
    private Boolean split = false;
    private float[] settings;
    private Boolean shapefill = true;
    private float[] aspectRatio;
    private float ratio;
    private float[] overlap;
    private static final int requestCode = 9;
    private int droneFlag;

    ArrayList<Marker> markers = new ArrayList<Marker>();
    ArrayList<Marker> actPointsInPoly = new ArrayList<Marker>();

    ArrayList<Marker> actBoderMarkers = new ArrayList<Marker>();
    ArrayList<Polyline> actPolyLynes = new ArrayList<Polyline>();

    ArrayList<ArrayList<ArrayList<Node>>> rasters = new ArrayList<>();

    Polygon shape;

    /**
     * This method is concerned with creating with the whole content of the window.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        /**
         * Define configuration options.
         */
        Configuration croutonConfiguration = new Configuration.Builder()
                .setDuration(3500).build();
        /**
         * Define styles for crouton.
         */
        Style style = new Style.Builder()
                .setBackgroundColorValue(Color.argb(200,0,0,0))
                .setGravity(Gravity.CENTER_HORIZONTAL)
                .setConfiguration(croutonConfiguration)
                .setHeight(200)
                .setTextColorValue(Color.WHITE).build();
        /**
         * Display style and configuration.
         */
        Crouton.showText(Main_Activity.this, R.string.crouton_main_activity , style);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /**
         * Get user input from previous settings activity.
         */
        if(getIntent().getExtras() != null)
        {
            settings = getIntent().getExtras().getFloatArray("com.example.nicol.dronflyvis.INPUT_VALUES");
            aspectRatio = getIntent().getExtras().getFloatArray("com.example.nicol.dronflyvis.ASPECT_RATIO");
            ratio = (aspectRatio[0]/aspectRatio[1]);
            overlap = getIntent().getExtras().getFloatArray("com.example.nicol.dronflyvis.OVERLAP");
            droneFlag = getIntent().getExtras().getInt("com.example.nicol.dronflyvis.RADIO_SELECTION");
        }
        /**
         * Set the image resource and make the book clickable.
         */
        ImageButton infobook = findViewById(R.id.infobuch_main_activity);
        infobook.setImageResource(R.drawable.infobuch);

        infobook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Buch_PopUp_Activity.class);
                startActivity(intent);
            }
        });

        /**
         * searchbar with autocompletion.
         */
        PlaceAutocompleteFragment placesFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        /**
         * Layout of searchbar.
         */
        TextView aut_comp_text = findViewById(R.id.place_autocomplete_search_input);
        aut_comp_text.setTextColor(Color.WHITE);
        findViewById(R.id.place_autocomplete_fragment).setBackgroundColor(Color.argb(150, 0,0,0));

        ImageView searchIcon = findViewById(R.id.place_autocomplete_search_button);
        searchIcon.setScaleX(1.5f);
        searchIcon.setScaleY(1.5f);

        ImageView clearButton = findViewById(R.id.place_autocomplete_clear_button);
        clearButton.setScaleX(1.5f);
        clearButton.setScaleY(1.5f);


        /**
         * Update camera depending on chosen location.
         */
        placesFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place)
            {
                LatLng latLng = place.getLatLng();
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

        /**
         * Set the image resources of the imagebuttons
         */
        final ImageButton pinImageButton = findViewById(R.id.pin);
        final ImageButton deleteImageButton = findViewById(R.id.delete);
        final ImageButton drawImageButton = findViewById(R.id.draw);
        final ImageButton clearImageButton = findViewById(R.id.clear);
        final ImageButton importImageButton = findViewById(R.id.importo);
        final ImageButton splitImageButton = findViewById(R.id.split);
        final ImageButton mapImageButton = findViewById(R.id.main_act_change_button);

        pinImageButton.setImageResource(R.drawable.pinicon);
        deleteImageButton.setImageResource(R.drawable.deleteicon);
        drawImageButton.setImageResource(R.drawable.drawselectedicon);
        clearImageButton.setImageResource(R.drawable.clear_image_button_style);
        importImageButton.setImageResource(R.drawable.import_image_button);
        splitImageButton.setImageResource(R.drawable.nosplit);
        mapImageButton.setImageResource(R.drawable.map_image_button_style);

        /**
         * When pinbutton is clicked disable the scroll function of googlemaps
         * and change image resource.
         */
        pinImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pinModus){
                    pinImageButton.setImageResource(R.drawable.pinicon);
                    mMap.getUiSettings().setScrollGesturesEnabled(true);

                    pinModus=false;
                }
                else{
                    pinImageButton.setImageResource(R.drawable.pinselectedicon);
                    mMap.getUiSettings().setScrollGesturesEnabled(false);

                    pinModus=true;
                }
            }
        });

        /**
         * When deletebutton is clicked the delete mode will be activated
         * and change of image resource.
         */
        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deleteModus){
                    deleteImageButton.setImageResource(R.drawable.deleteicon);
                    deleteModus = false;
                }
                else{
                    deleteImageButton.setImageResource(R.drawable.delteselectedicon);
                    deleteModus = true;

                    drawImageButton.setImageResource(R.drawable.drawicon);
                    drawModus = false;
                    }
            }
        });

        /**
         * When drawbutton is clicked the draw mode will be activated
         * and change of image resource.
         */
        drawImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawModus){
                    drawImageButton.setImageResource(R.drawable.drawicon);
                    drawModus = false;
                }
                else{
                    drawImageButton.setImageResource(R.drawable.drawselectedicon);
                    drawModus = true;

                    deleteImageButton.setImageResource(R.drawable.deleteicon);
                    deleteModus = false;
                }
            }
        });

        /**
         * When clearbutton is clicked then delete the drawn polygon.
         */
        clearImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * User gets promted a with a popup window asking if the user wants really
                 * delete the drawn polygon.
                 */
                AlertDialog.Builder dBuilder = new AlertDialog.Builder(Main_Activity.this);

                dBuilder.setTitle("Delete Polygon");
                dBuilder.setMessage("Are you sure that you want delete the polygon?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            /**
                             * If the user wants delete polygon some methods will be called.
                             * @param dialogInterface
                             * @param i
                             */
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deletePointsInPoly();
                                removePolygon();
                                dialogInterface.cancel();
                            }})
                        /**
                         * Cancellation is also a viable option.
                         */
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alertDialog = dBuilder.create();
                alertDialog.setTitle("Delete Polygon");
                alertDialog.show();

            }
        });

        /**
         * This is the split imagebutton which is responsible for the whole split/nonsplit mode.
         */
        splitImageButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Depending on which mode is selected the methods will be adjusted.
             * @param view
             */
            @Override
            public void onClick(View view) {
                if(split){
                    splitImageButton.setImageResource(R.drawable.nosplit);

                    split = false;
                    deletePointsInPoly();

                    /**
                     * Define configuration options
                     */
                    Configuration croutonConfiguration = new Configuration.Builder()
                            .setDuration(1000).build();
                    /**
                     * Define styles for crouton
                     */
                    Style style = new Style.Builder()
                            .setBackgroundColorValue(Color.argb(200,0,0,0))
                            .setGravity(Gravity.CENTER_HORIZONTAL)
                            .setConfiguration(croutonConfiguration)
                            .setHeight(200)
                            .setTextColorValue(Color.WHITE).build();
                    /**
                     * Display style and configuration
                     */
                    Crouton.showText(Main_Activity.this, R.string.crouton_normal_mode , style);
                }
                else{
                    splitImageButton.setImageResource(R.drawable.split);

                    /**
                     * Define configuration options
                     */
                    Configuration croutonConfiguration = new Configuration.Builder()
                            .setDuration(1000).build();
                    /**
                     * Define styles for crouton
                     */
                    Style style = new Style.Builder()
                            .setBackgroundColorValue(Color.argb(200,0,0,0))
                            .setGravity(Gravity.CENTER_HORIZONTAL)
                            .setConfiguration(croutonConfiguration)
                            .setHeight(200)
                            .setTextColorValue(Color.WHITE).build();
                    /**
                     * Display style and configuration
                     */
                    Crouton.showText(Main_Activity.this, R.string.crouton_split_mode , style);

                    if(markers != null){
                       if(markers.size() >= 3) {
                           drawPointInPoly();
                       }
                    }
                    split = true;
                }

            }
        });

        importImageButton.setOnClickListener(new View.OnClickListener() {
            /**
             * @author Johannes
             * Opens a new Intent in the File System
             */
            @Override
            public void onClick(View view) {
                //Request storage permissions during runtime
                ActivityCompat.requestPermissions( Main_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                //Set path to open file system at right folder
                File poly = android.os.Environment.getExternalStorageDirectory();
                String polyPath = poly.getAbsolutePath() + "/DroneTours/Polygons/";

                //open popUpWindow in Filesystem
                Intent importPolyIntent = new Intent(Intent.ACTION_GET_CONTENT);
                importPolyIntent.setDataAndType(Uri.parse(polyPath), "text/*");
                startActivityForResult(importPolyIntent, requestCode);
            }
        });


        /**
         * change the map type on ordinary click.
         */
        mapImageButton.setOnClickListener(new View.OnClickListener() {
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

        registerForContextMenu(mapImageButton);
        mapImageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });


    }

    /**
     * This Function gets the return of the Intent, in which the user
     * selects an Polygon, that shall be imported
     * @author Johannes
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Uri path;
        InputStream inputStream;

        /*
         * If any file was selected the resultCode will be RESULT_CANCELED, in
         * this case there is no input to handle
         * If the user has selected a file, the resultCode will be RESULT_OK, in
         * this case the requestCode is checked, if it equals to the requestCode, with
         * which the Intent was created
         */
        if(resultCode == RESULT_OK && requestCode == this.requestCode)
        {
            path = data.getData(); //get all Data from returned Intent
            try
            {
                inputStream = getContentResolver().openInputStream(path);


                try {
                    String line;
                    String[] latLong;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    /*
                     * If there is already a Polygon drawn in the activity,
                     * it will be removed
                     */
                    removePolygon();

                    /*
                     * read the input line by line
                     */
                    while((line = reader.readLine()) != null)
                    {
                        latLong = line.split(",");
                        Double lat = Double.parseDouble(latLong[0]);
                        Double lng = Double.parseDouble(latLong[1]);

                        /*
                         * Draw the imported Polygon in the activity
                         */
                        Main_Activity.this.setMarker("Local", lat, lng);
                    }

                    /*
                     * Camera animation:
                     * Zoom on first point of polygon
                     */
                    LatLng latLng = new LatLng(markers.get(0).getPosition().latitude, markers.get(0).getPosition().longitude);
                    CameraUpdate nloc = CameraUpdateFactory.newLatLngZoom(latLng, 13);

                    mMap.animateCamera(nloc);
                }
                catch (FileNotFoundException e)
                {
                    Toast.makeText(Main_Activity.this, "FileNotFound File Reader" ,Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    Toast.makeText(Main_Activity.this, "IOExec read line" ,Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Create a context menu to change the map type on long click.
     */
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

    /**
     * Establish essential functionality which has to be shown to the user while he is working on the map.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        /**
         * Enable/Disable functions
         */
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setMapToolbarEnabled (false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        /**
         * Determent by the chosen modus functionality changes accordingly.
         */
        if (mMap != null) {

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng)
                {
                    if(!drawModus & !deleteModus){
                        Toast.makeText(
                                Main_Activity.this,
                                "Please select a mode",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                    if(deleteModus){
                        Toast.makeText(
                                Main_Activity.this,
                                "Nothing to delete ",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    if(drawModus) {
                        Main_Activity.this.setMarker("Local", latLng.latitude, latLng.longitude);

                    }
                }
            });


            /**
             * Depending on the mode the behavior of the clicklister will be changed.
             */
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    Marker aktMarker = marker;

                    if(deleteModus) {

                        aktMarker.remove();
                        markers.remove(marker);

                        /**
                         * The behavior of the functions will be adjust.
                         */

                        redrawMarker();

                        if (shape != null) {

                            shape.remove();
                            shape = null;
                        }
                        if (markers.size() >= 1) {
                            drawPolygon();

                            if (markers.size() < 3) {
                                deletePointsInPoly();
                            }

                            if (split & markers.size() >= 3) {
                                drawPointInPoly();
                            }
                        }
                    }
                    return true;
                }
            });


            /**
             * This method handels the functionality which should adjust in case of dragging or letting go.
             */
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDragStart(Marker marker)
                {
                    /**
                     * Don't care
                     */

                }

                /**
                 * Case of dragging.
                 * @param marker
                 */
                @Override
                public void onMarkerDrag(Marker marker)
                {
                    deletePointsInPoly();

                    if(shape != null){
                        shape.remove();
                        shape=null;
                    }

                    if(split) {
                        drawBoundingBoxes();
                    }

                    if(split){
                        shapefill = false;
                    }

                    drawPolygon();

                }

                /**
                 * Case of letting go.
                 * @param marker
                 */
                @Override
                public void onMarkerDragEnd(Marker marker)
                {

                    if(shape != null){
                        shape.remove();
                        shape=null;
                    }

                    if(split) {
                        if(markers.size()>=3) {
                            drawPointInPoly();

                            if (actPolyLynes != null) {
                                for (int i = 0; i < actPolyLynes.size(); i++) {
                                    actPolyLynes.get(i).remove();
                                }
                                actPolyLynes = null;
                                actPolyLynes = new ArrayList<Polyline>();
                            }

                            shapefill = true;
                        }
                    }

                    drawPolygon();

                }
            });
        }
    }


    /**
     * Method for setting drawing a marker on the map and draws new shapes when markers are added.
     *
     * @param locality
     * @param lat
     * @param lng
     */
    private void setMarker(String locality, double lat, double lng) {
        MarkerOptions options = new MarkerOptions()
                .draggable(true)
                .position(new LatLng(lat,lng))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerstandard))
                .anchor((float)0.5, (float)0.5);

        markers.add(mMap.addMarker(options));
        redrawMarker();

        if(markers.size() >= 3)
        {
            if(shape != null){
                shape.remove();
                shape=null;
            }
            drawPolygon();
            if(split) {
                drawPointInPoly();
            }
        }
    }

    /**
     * Set the first and last marker in a different color
     */
    private  void redrawMarker(){

        if (markers.size() >= 2){
            markers.get(0).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markeranfangende));
            markers.get(markers.size()-1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markeranfangende));

            for (int i = 1; i < markers.size()-1; i++){
                markers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerstandard));
            }
        }
    }

    /**
     * This method draws the polygon on the map.
     */
    private void drawPolygon()
    {
        PolygonOptions options = new PolygonOptions()
                .strokeWidth(8)
                .strokeColor(Color.BLACK);

        if(shapefill){
            options.fillColor(0x66FF8C00);
        }
        else{
            options.fillColor(Color.argb(0,0,0,0));
        }

        for(int i=0;i<markers.size();i++ )
        {
            if(markers.size()>0){
                options.add(markers.get(i).getPosition());
            }

        }
        shape = mMap.addPolygon(options);
    }

    /**
     * Checks whether the given coordinates are farther then 300 meters.
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return boolean
     */
    public boolean checkRadius(double lat1, double lng1, double lat2, double lng2)
    {
        return getDistanceMeters(lat1, lng1, lat2, lng2) > 300;
    }

    /**
     * Get the distance between to coordinates in meters.
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return long
     */
    public static long getDistanceMeters(double lat1, double lng1, double lat2, double lng2) {

        double l1 = Math.toRadians(lat1);
        double l2 = Math.toRadians(lat2);
        double g1 = Math.toRadians(lng1);
        double g2 = Math.toRadians(lng2);

        double dist = Math.acos(Math.sin(l1) * Math.sin(l2) + Math.cos(l1) * Math.cos(l2) * Math.cos(g1 - g2));
        if(dist < 0) {
            dist = dist + Math.PI;
        }

        return Math.round(dist * 6378100);
    }

    /**
     * While the split mode is set, different marker colors for the splitted polygon will be drawn
     * using a multithreading method.
     */
    public void drawPointInPoly() {

        deletePointsInPoly();

        ArrayList<Node> actNodeListe = new ArrayList<Node>();
        for(Marker marker : markers)
        {
            actNodeListe.add(new Node(marker.getPosition().latitude, marker.getPosition().longitude, 0));
        }

        //Rastering raster = new Rastering(actNodeListe, (float) 78.8, 100);
        //Rastering raster = new Rastering(actNodeListe, settings[2], settings[1]);
        new AsyncRastering().execute(actNodeListe);


        return;
    }

    /**
     * Function of next button.
     * @param view
     */
    public void main_activity_next(View view) {
        /**
         * If no markers are drawn, a warning will be shown.
        */
        if(markers.size() == 0) {
            Warning warning = new Warning("You have to draw a polygon", "Please draw", false, "OK", this);
            android.app.AlertDialog alertDialog = warning.createWarning();
            alertDialog.setTitle("Missing Polygon");
            alertDialog.show();
            return;
        }

        /**
         * If the user has drawn a polygon which is too large another warning will be shown and we give
         * the user the possibility to split the polygon into several.
         */
        if (!split) {
            if (countPointInPoly() > 99) {
                AlertDialog.Builder dBuilder = new AlertDialog.Builder(Main_Activity.this);

                dBuilder.setTitle("Polygon is too large!");
                dBuilder.setMessage("Your polygon has more than 99 points! Would you like to split it?")
                        .setCancelable(false)
                        .setNeutralButton("Abort", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        /**
                         * User has the option to click split, and the polygon will be split
                         * */
                        .setPositiveButton("Split", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                drawPointInPoly();
                                split = true;

                                final ImageButton splitImageButtonAlert = findViewById(R.id.split);
                                splitImageButtonAlert.setImageResource(R.drawable.split);

                                /**
                                 * Define configuration options
                                 */
                                Configuration croutonConfiguration = new Configuration.Builder()
                                        .setDuration(3500).build();
                                /**
                                 * Define configuration options
                                 */
                                Style style = new Style.Builder()
                                        .setBackgroundColorValue(Color.argb(200, 0, 0, 0))
                                        .setGravity(Gravity.CENTER_HORIZONTAL)
                                        .setConfiguration(croutonConfiguration)
                                        .setHeight(200)
                                        .setTextColorValue(Color.WHITE).build();
                                /**
                                 * Display style and configuration
                                 */
                                Crouton.showText(Main_Activity.this, R.string.crouton_split_mode, style);


                                dialogInterface.cancel();
                            }
                        })
                        /**
                         * The user can choose the don't split option in this case all the important
                         * data will be handed over to the next activity immediately.
                         */
                        .setNegativeButton("Don't split", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ArrayList<Node> nodeList = new ArrayList<Node>();
                                Intent intent = new Intent(Main_Activity.this, Tours_View_And_Export_Activity.class);

                                for (Marker marker : markers) {
                                    nodeList.add(new Node(marker.getPosition().latitude, marker.getPosition().longitude, 0));
                                }

                                intent.putParcelableArrayListExtra("com.example.nicol.dronflyvis.NODELIST", nodeList);
                                intent.putExtra("com.example.nicol.dronflyvis.BEARING", mMap.getCameraPosition().bearing);
                                intent.putExtra("com.example.nicol.dronflyvis.mapZOOM", mMap.getCameraPosition().zoom);
                                intent.putExtra("com.example.nicol.dronflyvis.mapLAT", "" + mMap.getCameraPosition().target.latitude);
                                intent.putExtra("com.example.nicol.dronflyvis.mapLNG", "" + mMap.getCameraPosition().target.longitude);
                                intent.putExtra("com.example.nicol.dronflyvis.mapType", mMap.getMapType());
                                intent.putExtra("com.example.nicol.dronflyvis.SETTINGS", settings);
                                intent.putExtra("com.example.nicol.dronflyvis.splitPoly", split);
                                intent.putExtra("com.example.nicol.dronflyvis.ASPECT_RATIO", aspectRatio);
                                intent.putExtra("com.example.nicol.dronflyvis.OVERLAP",overlap);

                                startActivity(intent);
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = dBuilder.create();
                alertDialog.setTitle("Polygon is too large!");
                alertDialog.show();
                return;
            }
        }

        /**
         * Same as above but in the other case.
         */

        ArrayList<Node> nodeList = new ArrayList<Node>();
        Intent intent = new Intent(this, Tours_View_And_Export_Activity.class);

        for (Marker marker : markers) {
            nodeList.add(new Node(marker.getPosition().latitude, marker.getPosition().longitude, 0));
        }

        intent.putParcelableArrayListExtra("com.example.nicol.dronflyvis.NODELIST", nodeList);
        intent.putExtra("com.example.nicol.dronflyvis.BEARING", mMap.getCameraPosition().bearing);
        intent.putExtra("com.example.nicol.dronflyvis.mapZOOM", mMap.getCameraPosition().zoom);
        intent.putExtra("com.example.nicol.dronflyvis.mapLAT","" + mMap.getCameraPosition().target.latitude);
        intent.putExtra("com.example.nicol.dronflyvis.mapLNG","" + mMap.getCameraPosition().target.longitude);
        intent.putExtra("com.example.nicol.dronflyvis.mapType", mMap.getMapType());
        intent.putExtra("com.example.nicol.dronflyvis.SETTINGS", settings);
        intent.putExtra("com.example.nicol.dronflyvis.splitPoly", split);
        intent.putExtra("com.example.nicol.dronflyvis.ASPECT_RATIO", aspectRatio);
        intent.putExtra("com.example.nicol.dronflyvis.OVERLAP",overlap);
        intent.putExtra("com.example.nicol.dronflyvis.RADIO_SELECTION", droneFlag);
        startActivity(intent);
    }

    /**
     * Remove the polygon.
     */
    private void removePolygon()
    {
        if (shape != null)
            shape.remove();

        if (markers != null) {
            for (Marker m : markers) {
                m.remove();
            }
        }
        markers = new ArrayList<>();
    }

    /**
     * Count the number of nodes in the polygon
     * @return count
     */
    public int countPointInPoly() {

        ArrayList<Node> actNodeListe = new ArrayList<Node>();
        for(Marker marker : markers)
        {
            actNodeListe.add(new Node(marker.getPosition().latitude, marker.getPosition().longitude, 0));
        }



        Rastering raster = new Rastering(actNodeListe, settings[2], settings[1], ratio, overlap[0], overlap[1]);

        ArrayList<ArrayList<Node>> actRaster = raster.getRaster();

        if(actNodeListe == null)
        {
            System.out.println("ERROR 1");
        }

        if(actRaster == null)
        {
            System.out.println("ERROR 2");
        }

        int count = 0;
        for (ArrayList<Node> i : actRaster) {
            for (Node j : i) {
                count++;
            }
        }

        return count;
    }

    /**
     * Method which deletes the point in polygon depending on modus
     */
    public void deletePointsInPoly(){
        if (actPointsInPoly != null) {
            for (Marker m : actPointsInPoly) {
                m.remove();
            }
            actPointsInPoly = new ArrayList<>();
        }
    }

    /**
     * Draws boundingboxes while dragging marker in the split mode for better control
     * of split and radius for an large polygon.
     */
    public void drawBoundingBoxes(){

        if(actPolyLynes!=null){
            for(int i = 0; i<actPolyLynes.size();i++){
                actPolyLynes.get(i).remove();
            }

            actPolyLynes=null;
            actPolyLynes = new ArrayList<Polyline>();

        }


        ArrayList<Node> actNodeListe = new ArrayList<Node>();
        for(Marker marker : markers)
        {
            actNodeListe.add(new Node(marker.getPosition().latitude, marker.getPosition().longitude, 0));
        }

        ArrayList<Node[]> border = BoundingBoxesGenerator.getBoundingBoxes(actNodeListe, settings[2], settings[1], ratio, overlap[0], overlap[1]);
        for(int i = 0; i<border.size();i++){

            PolylineOptions options2 = new PolylineOptions()
                    .width(7)
                    .color(Color.RED);
            for(int j=0;j<4;j++ )
            {
                    options2.add(new LatLng( border.get(i)[j].getLongitude(),border.get(i)[j].getLatitude()));
            }
            options2.add(new LatLng( border.get(i)[0].getLongitude(),border.get(i)[0].getLatitude()));
            actPolyLynes.add(mMap.addPolyline(options2));
        }

    }

    /**
     * Shows the previous activityy.
     * @param view
     */
    public void main_activity_back(View view)
    {
        onBackPressed();
    }

    /**
     * A class concerned with multithreading to better performance.
     */
    private class AsyncRastering extends AsyncTask<ArrayList<Node>, Void, ArrayList<ArrayList<ArrayList<Node>>>> {
        @Override
        protected ArrayList<ArrayList<ArrayList<Node>>> doInBackground(ArrayList<Node>... arrayLists) {
            Rastering raster = new Rastering(arrayLists[0], settings[2], settings[1], ratio, overlap[0], overlap[1]);
            return raster.getRasters();
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<ArrayList<Node>>> result) {
            /**
             * Different colors for the subpolygons of a large splitted one.
             */
            int colour = -1;
            for (ArrayList<ArrayList<Node>> i : result) {
                colour++;
                colour = colour % 4;
                for (ArrayList<Node> x : i) {
                    for (Node j : x) {
                        double lt = j.getLatitude();
                        double lon = j.getLongitude();

                        MarkerOptions options;
                        switch (colour) {
                            case (0):
                                options = new MarkerOptions()
                                        .draggable(false)
                                        .position(new LatLng(lt, lon))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerstandardgreen))
                                        .anchor((float) 0.5, (float) 0.5);
                                actPointsInPoly.add(mMap.addMarker(options));
                                break;
                            case (1):
                                options = new MarkerOptions()
                                        .draggable(false)
                                        .position(new LatLng(lt, lon))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerstandardcyan))
                                        .anchor((float) 0.5, (float) 0.5);
                                actPointsInPoly.add(mMap.addMarker(options));
                                break;
                            case (2):
                                options = new MarkerOptions()
                                        .draggable(false)
                                        .position(new LatLng(lt, lon))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerstandardmagenta))
                                        .anchor((float) 0.5, (float) 0.5);
                                actPointsInPoly.add(mMap.addMarker(options));
                                break;
                            case (3):
                                options = new MarkerOptions()
                                        .draggable(false)
                                        .position(new LatLng(lt, lon))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerstandardyellow))
                                        .anchor((float) 0.5, (float) 0.5);
                                actPointsInPoly.add(mMap.addMarker(options));
                                break;
                        }

                    }
                }
            }
        }
    }
}
