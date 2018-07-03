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

public class Main_Activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Boolean deleteModus = false;
    private Boolean drawModus = true;
    private Boolean pinModus = false;
    private Boolean polyAufteilung = false;
    private float[] settings;
    private Boolean shapefill = true;

    //private Intent importPolyIntent;
    private static final int requestCode = 9;

    ArrayList<Marker> markers = new ArrayList<Marker>();
    ArrayList<Marker> actPointsInPoly = new ArrayList<Marker>();

    ArrayList<Marker> actBoderMarkers = new ArrayList<Marker>();
    ArrayList<Polyline> actPolyLynes = new ArrayList<Polyline>();

    ArrayList<ArrayList<ArrayList<Node>>> rasters = new ArrayList<>();

    Polygon shape;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        // Define configuration options
        Configuration croutonConfiguration = new Configuration.Builder()
                .setDuration(3500).build();
        // Define styles for crouton
        Style style = new Style.Builder()
                .setBackgroundColorValue(Color.argb(200,0,0,0))
                .setGravity(Gravity.CENTER_HORIZONTAL)
                .setConfiguration(croutonConfiguration)
                .setHeight(200)
                .setTextColorValue(Color.WHITE).build();
        // Display style and configuration
        Crouton.showText(Main_Activity.this, R.string.crouton_main_activity , style);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if(getIntent().getExtras() != null)
        {
            settings = getIntent().getExtras().getFloatArray("com.example.nicol.dronflyvis.INPUT_VALUES");
        }

        ImageButton infobuch = findViewById(R.id.infobuch_main_activity);
        infobuch.setImageResource(R.drawable.infobuch);

        infobuch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Buch_PopUp_Activity.class);
                startActivity(intent);
            }
        });

        PlaceAutocompleteFragment placesFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        TextView aut_comp_text = findViewById(R.id.place_autocomplete_search_input);
        aut_comp_text.setTextColor(Color.WHITE);
        findViewById(R.id.place_autocomplete_fragment).setBackgroundColor(Color.argb(150, 0,0,0));

        ImageView searchIcon = findViewById(R.id.place_autocomplete_search_button);
        searchIcon.setScaleX(1.5f);
        searchIcon.setScaleY(1.5f);

        ImageView clearButton = findViewById(R.id.place_autocomplete_clear_button);
        clearButton.setScaleX(1.5f);
        clearButton.setScaleY(1.5f);

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

        clearImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dBuilder = new AlertDialog.Builder(Main_Activity.this);

                dBuilder.setTitle("Delete Polygon");
                dBuilder.setMessage("Are you sure that you want delete the polygon?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deletePointsInPoly();
                                removePolygon();
                                dialogInterface.cancel();
                            }})
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

        splitImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(polyAufteilung){
                    splitImageButton.setImageResource(R.drawable.nosplit);

                    polyAufteilung = false;
                    deletePointsInPoly();

                    // Define configuration options
                    Configuration croutonConfiguration = new Configuration.Builder()
                            .setDuration(1000).build();
                    // Define styles for crouton
                    Style style = new Style.Builder()
                            .setBackgroundColorValue(Color.argb(200,0,0,0))
                            .setGravity(Gravity.CENTER_HORIZONTAL)
                            .setConfiguration(croutonConfiguration)
                            .setHeight(200)
                            .setTextColorValue(Color.WHITE).build();
                    // Display style and configuration
                    Crouton.showText(Main_Activity.this, R.string.crouton_normal_mode , style);
                }
                else{
                    splitImageButton.setImageResource(R.drawable.split);

                    // Define configuration options
                    Configuration croutonConfiguration = new Configuration.Builder()
                            .setDuration(1000).build();
                    // Define styles for crouton
                    Style style = new Style.Builder()
                            .setBackgroundColorValue(Color.argb(200,0,0,0))
                            .setGravity(Gravity.CENTER_HORIZONTAL)
                            .setConfiguration(croutonConfiguration)
                            .setHeight(200)
                            .setTextColorValue(Color.WHITE).build();
                    // Display style and configuration
                    Crouton.showText(Main_Activity.this, R.string.crouton_split_mode , style);

                    if(markers != null){
                        if (markers.size() >= 3) {
                            drawPointInPoly();
                        }
                    }
                    polyAufteilung = true;
                }

            }
        });

        importImageButton.setOnClickListener(new View.OnClickListener() {
            /**
             * @author Johannes
             */
            @Override
            public void onClick(View view) {
                //Request storage permissions during runtime
                ActivityCompat.requestPermissions( Main_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                File poly = android.os.Environment.getExternalStorageDirectory();
                String polyPath = poly.getAbsolutePath() + "/DroneTours/Polygons/";

                Intent importPolyIntent = new Intent(Intent.ACTION_GET_CONTENT);
                importPolyIntent.setDataAndType(Uri.parse(polyPath), "*/*");
                startActivityForResult(importPolyIntent, requestCode);
            }
        });

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
     * @author Johannes
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Uri path;
        InputStream inputStream;

        if (resultCode == RESULT_OK && requestCode == Main_Activity.requestCode)
        {
            path = data.getData();
            try
            {
                inputStream = getContentResolver().openInputStream(path);


                try {
                    String line;
                    String[] latLong;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    removePolygon();

                    /*
                     * read the input line by line
                     */
                    while((line = reader.readLine()) != null)
                    {
                        latLong = line.split(",");
                        Double lat = Double.parseDouble(latLong[0]);
                        Double lng = Double.parseDouble(latLong[1]);

                        Main_Activity.this.setMarker("Local", lat, lng);
                    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setMapToolbarEnabled (false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (mMap != null) {

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng)
                {
                    if(!drawModus &!deleteModus){
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



            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    Marker aktMarker = marker;

                    if(deleteModus) {

                        aktMarker.remove();
                        markers.remove(marker);
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

                            if (polyAufteilung & markers.size() >= 3) {
                                drawPointInPoly();
                            }
                        }
                    }
                    return true;
                }
            });

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDragStart(Marker marker)
                {


                }

                @Override
                public void onMarkerDrag(Marker marker)
                {
                    deletePointsInPoly();

                    if(shape != null){
                        shape.remove();
                        shape=null;
                    }

                    if(polyAufteilung) {
                        drawBoundingBoxes();
                    }

                    if(polyAufteilung){
                        shapefill = false;
                    }

                    drawPolygon();

                }

                @Override
                public void onMarkerDragEnd(Marker marker)
                {

                    if(shape != null){
                        shape.remove();
                        shape=null;
                    }

                    if(polyAufteilung) {
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
            if(polyAufteilung) {
                drawPointInPoly();
            }
        }
    }

    private  void redrawMarker(){

        if (markers.size() >= 2){
            markers.get(0).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markeranfangende));
            markers.get(markers.size()-1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markeranfangende));

            for (int i = 1; i < markers.size()-1; i++){
                markers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerstandard));
            }
        }
    }

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

    public boolean checkRadius(double lat1, double lng1, double lat2, double lng2)
    {
        return getDistanceMeters(lat1, lng1, lat2, lng2) > 300;
    }

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

    }




    public void main_activity_next(View view)
    {
        if(markers.size() == 0) {
            Warning warning = new Warning("You have to draw a polygon", "Please draw", false, "OK", this);
            android.app.AlertDialog alertDialog = warning.createWarning();
            alertDialog.setTitle("Missing Polygon");
            alertDialog.show();
            return;
        }
        if (!polyAufteilung) {
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
                        .setPositiveButton("Split", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                drawPointInPoly();
                                polyAufteilung = true;

                                final ImageButton splitImageButtonAlert = findViewById(R.id.split);
                                splitImageButtonAlert.setImageResource(R.drawable.split);

                                // Define configuration options
                                Configuration croutonConfiguration = new Configuration.Builder()
                                        .setDuration(3500).build();
                                // Define styles for crouton
                                Style style = new Style.Builder()
                                        .setBackgroundColorValue(Color.argb(200, 0, 0, 0))
                                        .setGravity(Gravity.CENTER_HORIZONTAL)
                                        .setConfiguration(croutonConfiguration)
                                        .setHeight(200)
                                        .setTextColorValue(Color.WHITE).build();
                                // Display style and configuration
                                Crouton.showText(Main_Activity.this, R.string.crouton_split_mode, style);


                                dialogInterface.cancel();
                            }
                        })
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
                                intent.putExtra("com.example.nicol.dronflyvis.splitPoly", polyAufteilung);

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


        ArrayList<Node> nodeList = new ArrayList<Node>();
        Intent intent = new Intent(this, Tours_View_And_Export_Activity.class);

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
        intent.putExtra("com.example.nicol.dronflyvis.splitPoly", polyAufteilung);

        startActivity(intent);
    }

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

    public int countPointInPoly() {

        ArrayList<Node> actNodeListe = new ArrayList<Node>();
        for(Marker marker : markers)
        {
            actNodeListe.add(new Node(marker.getPosition().latitude, marker.getPosition().longitude, 0));
        }

        Rastering raster = new Rastering(actNodeListe, settings[2], settings[1]);

        ArrayList<ArrayList<Node>> actRaster = raster.getRaster();

        int anzahl = 0;
        for (ArrayList<Node> i : actRaster) {
            for (Node j : i) {
                anzahl++;
            }
        }
        return anzahl;
    }


    public void deletePointsInPoly(){
        if (actPointsInPoly != null) {
            for (Marker m : actPointsInPoly) {
                m.remove();
            }
            actPointsInPoly = new ArrayList<>();
        }
    }

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

        ArrayList<Node[]> border = BoundingBoxesGenerator.getBoundingBoxes(actNodeListe, (float) 78.8, 100);
        for(int i = 0; i<border.size();i++){

            PolylineOptions options2 = new PolylineOptions()
                    .width(7)
                    .color(Color.RED);
            for(int j=0;j<4;j++ )
            {
                options2.add(new LatLng(border.get(i)[j].getLongitude(), border.get(i)[j].getLatitude()));
            }
            options2.add(new LatLng( border.get(i)[0].getLongitude(),border.get(i)[0].getLatitude()));
            actPolyLynes.add(mMap.addPolyline(options2));
        }

    }
    public void main_activity_back(View view)
    {
        onBackPressed();
    }

    private class AsyncRastering extends AsyncTask<ArrayList<Node>, Void, ArrayList<ArrayList<ArrayList<Node>>>> {
        @Override
        protected ArrayList<ArrayList<ArrayList<Node>>> doInBackground(ArrayList<Node>... arrayLists) {
            Rastering raster = new Rastering(arrayLists[0], settings[2], settings[1]);
            return raster.getRasters();
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<ArrayList<Node>>> result) {

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
