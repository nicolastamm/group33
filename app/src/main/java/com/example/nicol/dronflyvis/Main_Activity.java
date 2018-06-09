package com.example.nicol.dronflyvis;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

public class Main_Activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageButton infobuch;


    //Polygonpunkte und der Polygon selbst
    ArrayList<Marker> markers = new ArrayList<Marker>();
    Polygon shape;

    //unsere Modusen bis jetzt
    private Boolean deleteModus = false;
    private Boolean drawModus = false;
    private Boolean pinModus = false;
    private int mapType;
    private double markerLat;
    private double markerLng;
    private float zoom;

    private float[] settings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(getIntent().getExtras() != null)
        {

            settings = getIntent().getExtras().getFloatArray("com.example.nicol.dronflyvis.SETTINGS");
        }



        infobuch = (ImageButton)findViewById(R.id.infobuch4);
        infobuch.setImageResource(R.drawable.infobuch);
        infobuch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Buch_PopUp_Activity.class);
                startActivity(intent);
            }
        });





        final ImageButton pinImageButton = (ImageButton) findViewById(R.id.pin);
        final ImageButton deleteImageButton = (ImageButton) findViewById(R.id.delete);
        final ImageButton drawImageButton = (ImageButton) findViewById(R.id.draw);
        final ImageButton clearImageButton = (ImageButton)findViewById(R.id.clear);

        pinImageButton.setImageResource(R.drawable.pinicon);
        deleteImageButton.setImageResource(R.drawable.deleteicon);
        drawImageButton.setImageResource(R.drawable.drawicon);
        clearImageButton.setImageResource(R.drawable.clear_image_button_style);


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

        // ****************************************************************************************************************************




        // Change Button
        // ****************************************************************************************************************************

        Button searchButton = (Button)findViewById(R.id.main_act_change_button);
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
    // ****************************************************************************************************************************
    // was unten steht ist selbsterklerend eigentlich
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Shalte die Toolbar aus. Mich nervt es einfach :)
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled (false);
        mMap.getUiSettings().setZoomControlsEnabled(true);




        if (mMap != null) {

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
            {
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



            if (mMap != null) {

               mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                   @Override
                   public void onMapLongClick(LatLng latLng) {

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
                                   "Nothing to delete",
                                   Toast.LENGTH_LONG
                           ).show();
                       }

                       if(drawModus) {
                           Main_Activity.this.setMarker("Local", latLng.latitude, latLng.longitude);
                       }

                   }
               });
                }


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
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
                                if(markers.size()>=1){
                                drawPolygon();}


                    }
                    return true;
                }
            });

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
            {

                @Override
                public void onMarkerDragStart(Marker marker)
                {

                    marker.showInfoWindow();
                }

                @Override
                public void onMarkerDrag(Marker marker)
                {
                    LatLng ll = marker.getPosition();
                    marker.setSnippet("lat : " +ll.latitude+ " lng :" +ll.longitude+"");
                    marker.showInfoWindow();
                    if(shape != null){
                        shape.remove();
                        shape=null;
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

                    drawPolygon();
                    marker.hideInfoWindow();
                }
            });

        }


    }



    private void setMarker(String locality, double lat, double lng)
    {

        MarkerOptions options = new MarkerOptions()
                .title("Marker")
                .draggable(true)
                .position(new LatLng(lat,lng))
                .snippet("lat :" +lat+ "\nlng :" +lng+"")
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
                .fillColor(0x66FF8C00)
                .strokeWidth(4)
                .strokeColor(Color.BLACK);

        //ArrayList<Marker> mmarkers = new ArrayList<Marker>(ordnePolygon());
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
        if(getDistanceMeters(lat1, lng1, lat2, lng2) > 300)
        {
            return true;
        }
        return false;
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

    public void main_activity_next(View view)
    {
        if(markers.size() == 0)
        {
            Warning warning = new Warning("You have to draw a polygon", "Please draw", false, "OK", this);
            android.app.AlertDialog alertDialog = warning.createWarning();
            alertDialog.setTitle("Missing Polygon");
            alertDialog.show();
            return;
        }
        Node startNode = new Node(markers.get(0).getPosition().latitude,markers.get(0).getPosition().longitude,0);
        ArrayList<Node> nodeList = new ArrayList<Node>();

        Intent intent = new Intent(this, Tours_View_And_Export_Activity.class);
        for(Marker marker : markers)
        {
            nodeList.add(new Node(marker.getPosition().latitude, marker.getPosition().longitude, 0));
        }
        intent.putParcelableArrayListExtra("com.example.nicol.dronflyvis.NODELIST", nodeList);
        intent.putExtra("com.example.nicol.dronflyvis.BEARING", mMap.getCameraPosition().bearing);
        intent.putExtra("com.example.nicol.dronflyvis.mapZOOM", mMap.getCameraPosition().zoom);
        intent.putExtra("com.example.nicol.dronflyvis.mapLAT","" + mMap.getCameraPosition().target.latitude);
        intent.putExtra("com.example.nicol.dronflyvis.mapLNG","" + mMap.getCameraPosition().target.longitude);
        intent.putExtra("com.example.nicol.dronflyvis.mapType", mMap.getMapType());
        intent.putExtra("com.example.nicol.dronflyvis.SETTINGS", settings);

        startActivity(intent);

    }


    private void removePolygon()
    {
        if(shape!=null) {
            shape.remove();


            for (int i = 0; i < markers.size(); i++) {
                Marker m = markers.get(i);
                m.remove();
                m = null;

            }

            markers.removeAll(markers);
        }

    }


/**
    public ArrayList<Marker> ordnePolygon(){
        ArrayList<Marker> geordnet = new ArrayList<Marker>();


        for(int i = 0; i<markers.size(); i++ ){
            Marker aktMarker = markers.get(i);
            Marker aktMinMarker = aktMarker;
            float mindist = 0f;
            if(!isInArray(geordnet,markers.get(i))){
                geordnet.add(aktMarker);
            }
            for(int j = 0; j<markers.size(); j++ ){
                if(!isInArray(geordnet,markers.get(j)) &  getDistanceMeters(
                        aktMarker.getPosition().latitude,aktMarker.getPosition().latitude,
                        markers.get(j).getPosition().latitude,markers.get(j).getPosition().latitude)<mindist)
                {
                    mindist = getDistanceMeters(
                            aktMarker.getPosition().latitude,aktMarker.getPosition().latitude,
                            markers.get(j).getPosition().latitude,markers.get(j).getPosition().latitude);
                    aktMinMarker = markers.get(j);

                }
            }

            geordnet.add(aktMinMarker);
        }

        return geordnet;
    }



    public static long getDistanceMeters(double lat1, double lng1, double lat2, double lng2) {

        double l1 = toRadians(lat1);
        double l2 = toRadians(lat2);
        double g1 = toRadians(lng1);
        double g2 = toRadians(lng2);

        double dist = acos(sin(l1) * sin(l2) + cos(l1) * cos(l2) * cos(g1 - g2));
        if(dist < 0) {
            dist = dist + Math.PI;
        }

        return Math.round(dist * 6378100);
    }

    public boolean isInArray(ArrayList<Marker> liste,Marker marker)
    {
        boolean isIn = false;
            if(liste.size()>0){
                for(int i = 0; i<liste.size();i++){
                    if(liste.get(i)==marker){
                        isIn = true;
                    }
                }
            }
        return isIn;

    }

*/


    public void main_activity_back(View view)
    {
        onBackPressed();
    }


}
