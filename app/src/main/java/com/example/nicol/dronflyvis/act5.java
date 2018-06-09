package com.example.nicol.dronflyvis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class act5 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
   ArrayList<Marker> markers;
   ArrayList<Marker> pfad = new ArrayList<>();
   private Node startNode;
   public int MarkerCounter= 0;

   private double lat;
   private double lng;
   private int mapType;
   private float zoom;


    /**
     * Ich tue hier nur ein Paar klene Änderungen bezüglich des Pfads.
     * Wenn wir so weit sind, müssen wir den Pfad mahlen
     * Dafür sollen die Punkte übergeben werden(nicht notwendigerweise als marker!)
     * und in den markes Array geschrieben werden
     *
     * Ich bereite schon mal einen DrowPfad Method.
     *
     * Input-man bitte alles in array reinschreiben nach dem die Übergabe erfollgt ist
     *
     * Design-man, für dich ist jetzt ein kleiner Counter in den Variablen,
     * Also wenn wir so weit mit unseren Icons sind, können wir mit dem Counter die Markers nicht nur mahlen
     * sondern auch in der verbundener Reinfolge ausgeben.
     *
     *
     * VG Android Integrator :)
     * */


    public String Pfad = "DronFlyVisPfad";
    private static final String FILE_NAME = "DronPfad.txt";
    ArrayList<Node> nodeList;
    ArrayList<Node> route;
    private float altitude;
    private float fov;
    private float pixels;
    private float[] settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act5);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getIntent().getExtras() != null)
        {
            nodeList = getIntent().getExtras().getParcelableArrayList("com.example.nicol.dronflyvis.NODELIST");
            startNode = getIntent().getExtras().getParcelable("com.example.nicol.dronflyvis.START_NODE");
            lat = Double.parseDouble(getIntent().getExtras().getString("com.example.nicol.dronflyvis.ACT4_LAT"));
            lng = Double.parseDouble(getIntent().getExtras().getString("com.example.nicol.dronflyvis.ACT4_LONG"));
            mapType = getIntent().getExtras().getInt("com.example.nicol.dronflyvis.ACT4_MAP_TYPE");
            zoom = getIntent().getExtras().getFloat("com.example.nicol.dronflyvis.ACT4_ZOOM");
            settings = getIntent().getExtras().getFloatArray("com.example.nicol.dronflyvis.SETTINGS");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled (false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));
        mMap.setMapType(mapType);

        TravelingSalesman tsm = new TravelingSalesman();
        //new Rastering(nodeList, startNode, settings[2], settings[1], settings[0]);
        new Rastering(nodeList, startNode, settings[2], settings[1], settings[0]);
        /*
         *if(splitPolygons == true)
         * {
         *   Rastering.splitPolygon();
         *   ArrayList<Node>[] rasters = Rastering.getRasters();
         *
         *   for(ArrayList<Node> i : rasters)
         *   {
         *      routes[x] = tsm.travelingSalesman(i);
         *   }
         * }
         * */
        route = tsm.travelingSalesman(Rastering.getRoute() , Rastering.getStartingpoint());

        for(int i = 0; i<route.size(); i++)
        {
            double lt = route.get(i).getLatitude();
            double lon = route.get(i).getLongitude();

            MarkerOptions options = new MarkerOptions()
                    .title("Marker"+MarkerCounter+"")
                    .draggable(true)
                    .position(new LatLng((float)lt,(float)lon));

            pfad.add(mMap.addMarker(options));
            MarkerCounter++;
        }
        drawPfad();
        for(int j = 0; j<pfad.size();j++){
            pfad.get(j).showInfoWindow();
        }

    }

    public void act_5_back(View view){
        onBackPressed();
    }

        public void akt5export(View view) {
           String myPfad = Pfad;
           FileOutputStream fos = null;

            try {
                fos = openFileOutput(FILE_NAME, MODE_APPEND);
                fos.write(myPfad.getBytes());

                Toast.makeText(this,"gespeichert" + getFilesDir() + "/" +FILE_NAME,Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if(fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



    private void drawPfad()
    {

        PolylineOptions optionss = new PolylineOptions()
                .width(3)
                .color(Color.RED);
                for(int i=0;i<pfad.size();i++ )
                {
                    if(pfad.get(i) != null || pfad.size()>0){
                        optionss.add(pfad.get(i).getPosition());
                    }
                }
                mMap.addPolyline(optionss);

    }





    }

