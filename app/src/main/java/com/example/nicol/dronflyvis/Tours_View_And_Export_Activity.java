package com.example.nicol.dronflyvis;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class Tours_View_And_Export_Activity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static  String FILE_NAME = "";


    private ImageButton infobuch;
    private ImageButton mapImageButton;
    private GoogleMap mMap;
    public int MarkerCounter= 0;
    public int farbe = 0;

    private float zoom;
    private double lat;
    private double lng;
    private int mapType;
    private float[] settings;
    private  Boolean split;
    private int droneFlag;


    int PolyCount;

    ArrayList<Marker> pfad = new ArrayList<>();
    Node actStartNode;
    Polyline polyline;

    ArrayList<ArrayList<Marker>> pfads = new ArrayList<ArrayList<Marker>>();
    ArrayList<Node> actStartNodes;
    ArrayList<Polyline> polylines = new ArrayList<Polyline>();

    ArrayList<Node> nodeList;
    ArrayList<Node> route;
    ArrayList<ArrayList<Node>> allRoutes = new ArrayList<ArrayList<Node>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tours_view_and_export_activity);

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
        Crouton.showText(Tours_View_And_Export_Activity.this, R.string.crouton_tours_activity, style);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getIntent().getExtras() != null)
        {
            nodeList = getIntent().getExtras().getParcelableArrayList("com.example.nicol.dronflyvis.NODELIST");
            settings = getIntent().getExtras().getFloatArray("com.example.nicol.dronflyvis.SETTINGS");
            lng = Float.parseFloat(getIntent().getExtras().getString("com.example.nicol.dronflyvis.mapLNG"));
            lat = Float.parseFloat(getIntent().getExtras().getString("com.example.nicol.dronflyvis.mapLAT"));
            zoom = getIntent().getExtras().getFloat("com.example.nicol.dronflyvis.mapZOOM");
            mapType = getIntent().getExtras().getInt("com.example.nicol.dronflyvis.mapType");
            split =  getIntent().getExtras().getBoolean("com.example.nicol.dronflyvis.splitPoly");
            droneFlag = getIntent().getExtras().getInt("com.example.nicol.dronflyvis.RADIO_SELECTION");
        }


        infobuch = findViewById(R.id.tvae_activity_infobuch_button);
        infobuch.setImageResource(R.drawable.infobuch);
        infobuch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Buch_PopUp_Activity.class);
                startActivity(intent);
            }
        });

        mapImageButton = findViewById(R.id.tvae_activity_change_button);
        mapImageButton.setImageResource(R.drawable.map_image_button_style);
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




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        mMap.setMapType(mapType);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));


        if (split) {
            int count=0;

            Rastering raster = new Rastering(nodeList, (float) settings[2], settings[1]);
            TravelingSalesman tsm = new TravelingSalesman();



            ArrayList<ArrayList<ArrayList<Node>>> actRaster = raster.getRasters();

            actStartNodes = new ArrayList<Node>();




            for (ArrayList<ArrayList<Node>> i : actRaster) {
                ArrayList<Marker> pfad = new ArrayList<>();


                actStartNodes.add(new Node(i.get(0).get(0).getLatitude(), i.get(0).get(0).getLongitude(), 2));
                route = tsm.travelingSalesman(i, actStartNodes.get(count) , nodeList);
                count++;
                allRoutes.add(route);

                for (int j = 0; j < route.size(); j++) {
                    double lt = route.get(j).getLatitude();
                    double lon = route.get(j).getLongitude();

                    MarkerCounter++;
                    String text = String.valueOf(MarkerCounter);
                    Bitmap bitmap = makeBitmap(this, text, farbe);

                    MarkerOptions options = new MarkerOptions()
                            .draggable(false)
                            .position(new LatLng((float) lt, (float) lon))
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .anchor((float) 0.5, (float) 0.5);
                    pfad.add(mMap.addMarker(options));
                }
                pfads.add(pfad);

                drawPfad(pfad);
                farbe++;
                MarkerCounter = 0;
            }

        }
        else{

            Rastering raster = new Rastering(nodeList, settings[2], settings[1]);
            TravelingSalesman tsm = new TravelingSalesman();

            ArrayList<ArrayList<Node>>  actRaster = raster.getRaster();
            actStartNode = new Node(actRaster.get(0).get(0).getLatitude(),actRaster.get(0).get(0).getLongitude(),2);
            if(actRaster.isEmpty())
            {
                route = nodeList;
            }
            else
            {
                route = tsm.travelingSalesman(actRaster,actStartNode, nodeList);
            }
            allRoutes.add(route);

            for(int i = 0; i<route.size(); i++)
            {
                double lt = route.get(i).getLatitude();
                double lon = route.get(i).getLongitude();

                MarkerCounter++;
                String text = String.valueOf(MarkerCounter);
                Bitmap bitmap = makeBitmap(this, text,2);

                MarkerOptions options = new MarkerOptions()
                        .draggable(false)
                        .position(new LatLng((float)lt,(float)lon))
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .anchor((float) 0.5, (float) 0.5);


                pfad.add(mMap.addMarker(options));
            }
            drawPfad(pfad);
        }
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

    public Bitmap makeBitmap(Context context, String text, int colourMode) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;

        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.markerroute2);
        bitmap = bitmap.copy(ARGB_8888, true);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


        if(split) {
            colourMode = colourMode % 4;
            switch (colourMode) {
                case (0):
                    bitmap = BitmapFactory.decodeResource(resources, R.drawable.markerstandardgreen);
                    bitmap = bitmap.copy(ARGB_8888, true);
                    paint.setColor(Color.WHITE);
                    break;
                case (1):
                    bitmap = BitmapFactory.decodeResource(resources, R.drawable.markerstandardcyan);
                    bitmap = bitmap.copy(ARGB_8888, true);
                    paint.setColor(Color.WHITE);
                    break;
                case (2):
                    bitmap = BitmapFactory.decodeResource(resources, R.drawable.markerstandardmagenta);
                    bitmap = bitmap.copy(ARGB_8888, true);
                    paint.setColor(Color.WHITE);
                    break;
                case (3):
                    bitmap = BitmapFactory.decodeResource(resources, R.drawable.markerstandardyellow);
                    bitmap = bitmap.copy(ARGB_8888, true);
                    paint.setColor(Color.WHITE);
                    break;

            }
        }


        paint.setTextSize(8 * scale);
        paint.setColor(Color.WHITE);

        Canvas canvas = new Canvas(bitmap);

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = bitmap.getWidth()/2 - bounds.width()/2;
        int y = bitmap.getHeight()/2 + bounds.height()/2;

        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    private void drawPfad(ArrayList<Marker> markArray)
    {
        if(split){
            PolylineOptions options = new PolylineOptions()
                    .width(10);

            for (int i = 0; i < markArray.size(); i++)
            {
                if (markArray.get(i) != null || markArray.size() > 0) {
                    options.add(markArray.get(i).getPosition());
                }
            }

            int lineColor = farbe % 4;
            switch (lineColor){
                case (0):
                    options.color(getColor(R.color.green));
                    break;
                case (1):
                    options.color(getColor(R.color.cyan));
                    break;
                case (2):
                    options.color(getColor(R.color.magenta));
                    break;
                case (3):
                    options.color(getColor(R.color.yellow));
                    break;
            }


            polylines.add(mMap.addPolyline(options));
        }
        else{
            PolylineOptions optionss = new PolylineOptions()
                    .width(10)
                    .color(Color.RED);

            for (int i = 0; i < markArray.size(); i++)
            {
                if (markArray.get(i) != null || markArray.size() > 0) {
                    optionss.add(markArray.get(i).getPosition());
                }
            }


            polyline = mMap.addPolyline(optionss);
        }





        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                allRoutes.clear();
                if(split){
                    farbe = 0;
                    int fromPol = pointFromPoly(marker);
                    int count=0;

                    if (pfads != null) {
                        for(int j = 0; j<pfads.size();j++) {
                            for (int i = 0; i < pfad.size(); i++) {
                                Marker m = pfad.get(i);
                                m.remove();
                                m = null;
                            }
                        }
                        pfads.removeAll(pfads);
                    }


                    actStartNodes.get(fromPol).setLatitude(marker.getPosition().latitude);
                    actStartNodes.get(fromPol).setLongitude(marker.getPosition().longitude);



                    if(polylines!=null){
                        for(int i = 0;i<polylines.size();i++){
                            polylines.get(i).remove();
                        }
                    }


                    Rastering raster = new Rastering(nodeList, (float) settings[2], settings[1]);
                    TravelingSalesman tsm = new TravelingSalesman();

                    ArrayList<ArrayList<ArrayList<Node>>> actRaster = raster.getRasters();


                    for (ArrayList<ArrayList<Node>> i : actRaster) {
                        ArrayList<Marker> pfad = new ArrayList<>();



                        route = tsm.travelingSalesman(i, actStartNodes.get(count) , nodeList);
                        allRoutes.add(route);
                        count++;

                        for (int j = 0; j < route.size(); j++) {
                            double lt = route.get(j).getLatitude();
                            double lon = route.get(j).getLongitude();


                            String text = String.valueOf(MarkerCounter);
                            Bitmap bitmap = makeBitmap(Tours_View_And_Export_Activity.this, text, farbe);

                            MarkerOptions options = new MarkerOptions()
                                    .draggable(false)
                                    .position(new LatLng((float) lt, (float) lon))
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                    .anchor((float) 0.5, (float) 0.5);
                            pfad.add(mMap.addMarker(options));

                            MarkerCounter++;
                        }
                        pfads.add(pfad);

                        drawPfad(pfad);
                        farbe++;
                        MarkerCounter = 0;
                    }




                }

                else {
                    MarkerCounter = 0;
                    if (pfad != null) {
                        for (int i = 0; i < pfad.size(); i++) {
                            Marker m = pfad.get(i);
                            m.remove();
                            m = null;
                        }
                        pfad.removeAll(pfad);
                    }


                    if (actStartNode != null) {
                        actStartNode.setLatitude(marker.getPosition().latitude);
                        actStartNode.setLongitude(marker.getPosition().longitude);
                    }

                    if (polyline != null) {

                        polyline.remove();
                    }

                    Rastering raster = new Rastering(nodeList, settings[2], settings[1]);
                    TravelingSalesman tsm = new TravelingSalesman();

                    ArrayList<ArrayList<Node>> actRaster = raster.getRaster();

                    if (actRaster.isEmpty()) {
                        route = nodeList;
                    } else {
                        route = tsm.travelingSalesman(actRaster, new Node(actStartNode.getLatitude(), actStartNode.getLongitude(), 2), nodeList);
                    }
                    allRoutes.add(route);

                    for (int i = 0; i < route.size(); i++) {
                        double lt = route.get(i).getLatitude();
                        double lon = route.get(i).getLongitude();

                        MarkerCounter++;
                        String text = String.valueOf(MarkerCounter);
                        Bitmap bitmap = makeBitmap(Tours_View_And_Export_Activity.this, text, 2);

                        MarkerOptions options = new MarkerOptions()
                                .draggable(false)
                                .position(new LatLng((float) lt, (float) lon))
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .anchor((float) 0.5, (float) 0.5);


                        pfad.add(mMap.addMarker(options));
                    }
                    drawPfad(pfad);


                }

                return true;
            }
        });

    }

    public int pointFromPoly(Marker marker){
        Marker Startmarker = marker;
         PolyCount = 0;




        for(int i = 0; i<pfads.size();i++){
            for(int j= 0; j<pfads.get(i).size();j++){
                double lt = pfads.get(i).get(j).getPosition().latitude;
                double lon = pfads.get(i).get(j).getPosition().longitude;

                if(Startmarker.getPosition().latitude==lt & Startmarker .getPosition().longitude==lon){
                    PolyCount=i;
                }
            }
        }

        return PolyCount;
    }

    /**
     * @author Johannes
     * @param view
     */
    public void  export_csv(View view) {
        //Artuk war hier hehe :)

        //AllRoutes brauchst eigentlich nur du, wegen der neuer Funktionalität hast die bei dir local
        // Sonnst muss ich die ständig löschen und neu deffinieren
/*
        ArrayList<ArrayList<Node>> allRoutes = new ArrayList<ArrayList<Node>>();

        if(split) {
            int count = 0;

            Rastering raster = new Rastering(nodeList, (float) settings[2], settings[1]);
            TravelingSalesman tsm = new TravelingSalesman();

            ArrayList<ArrayList<ArrayList<Node>>> actRaster = raster.getRasters();


            for (ArrayList<ArrayList<Node>> i : actRaster) {
                ArrayList<Marker> pfad = new ArrayList<>();

                ArrayList<Node> routee = new ArrayList<Node>();
                routee = tsm.travelingSalesman(i, actStartNodes.get(count), nodeList);
                allRoutes.add(routee);
                count++;
            }
        }
        else{
            Rastering raster = new Rastering(nodeList, settings[2], settings[1]);
            TravelingSalesman tsm = new TravelingSalesman();

            ArrayList<ArrayList<Node>> actRaster = raster.getRaster();
            ArrayList<Node> routee = new ArrayList<Node>();

            if (actRaster.isEmpty()) {
                routee = nodeList;
            } else {
                routee = tsm.travelingSalesman(actRaster, new Node(actStartNode.getLatitude(), actStartNode.getLongitude(), 2), nodeList);
            }

             allRoutes.add(routee);
        }*/



        //Request storage permissions during runtime
        ActivityCompat.requestPermissions( Tours_View_And_Export_Activity.this ,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_EXTERNAL_STORAGE);

        /*
         * Gets the current Date und Time, to timestamp the CSV
         */
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy' 'HH-mm");
        Date currentTime = new Date();
        String timeStamp = "" + format.format(currentTime);

        String content = "";
        String directory = "";

        for(int i = 0; i < allRoutes.size(); ++i)
        {
            //Only create Export for selected drone
            switch(droneFlag) {
                case 0:
                    content = routeForMavicPro(allRoutes.get(i));

                    if(i != 0)
                    {
                        FILE_NAME = "Route " + timeStamp + "(" + i + ").csv";
                    }
                    else
                    {
                        FILE_NAME = "Route " + timeStamp + ".csv";
                    }

                    directory = "DJI/";
                    break;
                case 1:
                    content = routeForBebop(allRoutes.get(i));

                    if(i != 0)
                    {
                        FILE_NAME = timeStamp + "(" + i + ") Route";
                    }
                    else
                    {
                        FILE_NAME = timeStamp + " Route";
                    }
                    directory = "ARPro3/";
                    break;
                default:
                    content = "";
                    Toast.makeText(this,"Invalid Drone selected",Toast.LENGTH_LONG).show();
                    break;
            }

            //Get the path to the directory to save the CSV
            File file = android.os.Environment.getExternalStorageDirectory();
            String path = file.getAbsolutePath() + "/DroneTours/" + directory;
            file = new File(path);
            //If there is no folder, create a new one
            file.mkdirs();
            file = new File(path + FILE_NAME);

            //Create a new File to later write in the data
            try
            {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,"CouldNotCreateFile",Toast.LENGTH_LONG).show();
            }
            FileOutputStream fos = null;

            /*
             *
             */
            if(droneFlag == 1)
            {
                InputStream in = getResources().openRawResource(R.raw.arpro3);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String ar = "";
                try
                {
                    while (reader.readLine() != null)
                    {
                        ar += reader.readLine();
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                content = ar + content;
            }

            //write data to file
            try
            {
                fos = new FileOutputStream(file);
                fos.write(content.getBytes());

                Toast.makeText(this,"File saved at: " + file,Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this,"FileNotFound, please try again to export",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,"IOException, during write to file",Toast.LENGTH_LONG).show();
            } finally{
                if(fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }  //end for

        File poly = android.os.Environment.getExternalStorageDirectory();
        String polyPath = poly.getAbsolutePath() + "/DroneTours/Polygons";

        String filename = "Polygon " + timeStamp + ".csv";
        poly = new File(polyPath);

        //If there is no folder, create a new one
        poly.mkdirs();
        poly = new File(polyPath + "/" + filename);

        try
        {
            poly.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(this,"CouldNotCreateFile",Toast.LENGTH_LONG).show();
        }

        FileOutputStream fos = null;
        content = "";

        for(int i = 0; i < nodeList.size() - 1; i++)
        {
            content += nodeList.get(i).getLatitude() + "," + nodeList.get(i).getLongitude() + "\n";
        }

        //write data to file
        try
        {
            fos = new FileOutputStream(poly);
            fos.write(content.getBytes());

            Toast.makeText(this,"File saved at: " + poly ,Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"FileNotFound, please try again to export",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"IOException, during write to file",Toast.LENGTH_LONG).show();
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

    /**
     * @author Johannes
     * Creates the Content for the CSV, which is needed for LitchiOnline
     * @return the content used for the CSV File
     */
    private String routeForMavicPro(ArrayList<Node> route)
    {
        String content;
        content = "latitude,longitude,altitude.m.,heading.deg.,curvesize.m.,rotationdir,gimbalmode,"
                + "gimbalpitchangle,";

        for(int i = 1; i <= 15; ++i)
        {
            content += "actiontype" + i + ",actionparam" + i + ",";
        }
        content += "\r\n";

        // iterates through the whole list and writes every Nodes Longitude and Latitude
        for (int i = 0; i < route.size(); ++i)
        {
            Node add = route.get(i);
            content += add.getLatitude() + "," + add.getLongitude() + "," + settings[1]
                    + ",0,0,0,0,0,1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0\r\n";
        }

        return content;
    }

    /**
     * @author Johannes
     * Creates the Content for the CSV, which is needed for AR Pro 3
     * @return the content used for the CSV File
     */
    private String routeForBebop(ArrayList<Node> route)
    {
        String content = null;
        // iterates through the whole list and writes every Nodes Longitude and Latitude
        for (int i = 0; i < route.size() - 1; ++i)
        {
            Node add = route.get(i);
            content += settings[1] + ",0,4,0," + i + ",0,0,0,0,0,-1,5,1,"
                    + (float)add.getLatitude() + "," + (float)add.getLongitude() + ",false,99,30\r\n";
        }
        Node add = route.get(route.size() - 1);
        content += settings[1] + ",0,4,0," + (route.size() - 1) + ",0,0,0,0,0,-1,5,1,"
                + (float)add.getLatitude() + "," + (float)add.getLongitude() + ",false," + Integer.MIN_VALUE + ",0";

        return content;
    }

    public void tvae_back(View view){
        onBackPressed();
    }
}

