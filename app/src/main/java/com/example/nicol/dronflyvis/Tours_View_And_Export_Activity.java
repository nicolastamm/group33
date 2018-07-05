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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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

/**
 * @author Artuk
 * @author Hilmi
 * @author Johannes
 * @author Nico
 * @author Heiko
 *
 * This class takes care of displaying the calculated tour in two different modes
 * and is responsible for exporting the CSV files.
 */
public class Tours_View_And_Export_Activity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static  String FILE_NAME = "";

    private ImageButton infobook;
    private ImageButton mapImageButton;
    private GoogleMap mMap;
    public int MarkerCounter= 0;
    public int colour = 0;
    private float[] aspectRatio;
    private float ratio;
    private float zoom;
    private double lat;
    private double lng;
    private int mapType;
    private float[] settings;
    private  Boolean split;
    private int droneFlag;
    private float[] overlap;

    int PolyCount;

    ArrayList<Marker> path = new ArrayList<>();
    Node actStartNode;
    Polyline polyline;

    ArrayList<ArrayList<Marker>> paths = new ArrayList<ArrayList<Marker>>();
    ArrayList<Node> actStartNodes;
    ArrayList<Polyline> polylines = new ArrayList<Polyline>();

    ArrayList<Node> nodeList;
    ArrayList<Node> route;
    ArrayList<ArrayList<Node>> allRoutes = new ArrayList<ArrayList<Node>>();

    TravelingSalesman tsm = new TravelingSalesman();

    /**
     * This method is concerned with creating with the whole content of the window.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tours_view_and_export_activity);

        /**
         * Define configuration options
         */
        Configuration croutonConfiguration = new Configuration.Builder()
                .setDuration(3500).build();
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
        Crouton.showText(Tours_View_And_Export_Activity.this, R.string.crouton_tours_activity, style);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /**
         * get all the data from the last activity.
         */
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
            overlap = getIntent().getExtras().getFloatArray("com.example.nicol.dronflyvis.OVERLAP");
            aspectRatio = getIntent().getExtras().getFloatArray("com.example.nicol.dronflyvis.ASPECT_RATIO");
            ratio = (aspectRatio[0]/aspectRatio[1]);
        }
        /**
         * Set the image resource and make the book clickable.
         */
        infobook = findViewById(R.id.tvae_activity_infobuch_button);
        infobook.setImageResource(R.drawable.infobuch);
        infobook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Buch_PopUp_Activity.class);
                startActivity(intent);
            }
        });

        /**
         * Set the map image resources of the imagebutton.
         */
        mapImageButton = findViewById(R.id.tvae_activity_change_button);
        mapImageButton.setImageResource(R.drawable.map_image_button_style);

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
     * Establish essential functionality which has to be shown to the user while he is working on the map.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        /**
         * Enable/Disable functions.
         */
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(mapType);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));

        /**
         * Calculate the tour route asynchronously.
         */
        if (split) {
            new AsyncRasters().execute(nodeList);
        }
        else{
            new AsyncRaster().execute(nodeList);
        }
    }


    /**
     * create a context menu to change the map type on long click.
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
     * Defines nodes bitmap
     * enumerate the nodes with numbers
     * @param context
     * @param text
     * @param colourMode
     * @return bitmap
     */
    public Bitmap makeBitmap(Context context, String text, int colourMode) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;

        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.markerroute2);
        bitmap = bitmap.copy(ARGB_8888, true);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        /**
         * Add the same color for the lines of the tour like the color of their nodes.
         */
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

    /**
     * Draws the lines between the setted marker to visualise the tour routes.
     * @param markArray
     */
    private void drawPath(ArrayList<Marker> markArray)
    {
        /**
         * Line and marker options when in split mode.
         */
        if(split){
            PolylineOptions options = new PolylineOptions()
                    .width(10);

            for (int i = 0; i < markArray.size(); i++)
            {
                if (markArray.get(i) != null || markArray.size() > 0) {
                    options.add(markArray.get(i).getPosition());
                }
            }
            /**
             * Same line color for the routes like the nodes
             */
            int lineColor = colour % 4;
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
            /**
             * Line and marker options when in nosplit mode.
             */
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

        /**
         * With this Method you can set a new startnode in the calculated tour if you click on an marker.
         */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                /**
                 * Clear existing routes.
                 */
                allRoutes.clear();

                /**
                 * Get the modus state from the previous activity for executing the right functionality.
                 */
                if(split){
                    colour = 0;

                    /**
                     * Call method to determine in which subroute the click was placed.
                     */
                    int fromPol = pointFromPoly(marker);
                    int count=0;

                    /**
                     * Remove all existing paths.
                     */
                    if (paths != null) {
                        for(int j = 0; j< paths.size(); j++) {
                            for (int i = 0; i < path.size(); i++) {
                                Marker m = path.get(i);
                                m.remove();
                                m = null;
                            }
                        }
                        paths.removeAll(paths);
                    }

                    /**
                     * Reset the new startpoint for the subroute.
                     */
                    actStartNodes.get(fromPol).setLatitude(marker.getPosition().latitude);
                    actStartNodes.get(fromPol).setLongitude(marker.getPosition().longitude);


                    /**
                     * Remove the lines between markers in subroute.
                     */
                    if(polylines!=null){
                        for(int i = 0;i<polylines.size();i++){
                            polylines.get(i).remove();
                        }
                    }


                    Rastering raster = new Rastering(nodeList, settings[2], settings[1], ratio ,overlap[0], overlap[1]);
                    TravelingSalesman tsm = new TravelingSalesman();

                    ArrayList<ArrayList<ArrayList<Node>>> actRaster = raster.getRasters();

                    /**
                     * Reset the markers, lines and enumeration of markers.
                     */
                    for (ArrayList<ArrayList<Node>> i : actRaster) {
                        ArrayList<Marker> path = new ArrayList<>();



                        route = tsm.travelingSalesman(i, actStartNodes.get(count) , nodeList);
                        allRoutes.add(route);
                        count++;

                        for (int j = 0; j < route.size(); j++) {
                            double lt = route.get(j).getLatitude();
                            double lon = route.get(j).getLongitude();


                            String text = String.valueOf(MarkerCounter);
                            Bitmap bitmap = makeBitmap(Tours_View_And_Export_Activity.this, text, colour);

                            MarkerOptions options = new MarkerOptions()
                                    .draggable(false)
                                    .position(new LatLng((float) lt, (float) lon))
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                    .anchor((float) 0.5, (float) 0.5);
                            path.add(mMap.addMarker(options));

                            MarkerCounter++;
                        }
                        paths.add(path);

                        drawPath(path);
                        colour++;
                        MarkerCounter = 0;
                    }

                }

                else {
                    /**
                     * Same as above but only for one tour without splitting.
                     */
                    MarkerCounter = 0;
                    if (path != null) {
                        for (int i = 0; i < path.size(); i++) {
                            Marker m = path.get(i);
                            m.remove();
                            m = null;
                        }
                        path.removeAll(path);
                    }


                    if (actStartNode != null) {
                        actStartNode.setLatitude(marker.getPosition().latitude);
                        actStartNode.setLongitude(marker.getPosition().longitude);
                    }

                    if (polyline != null) {

                        polyline.remove();
                    }

                    Rastering raster = new Rastering(nodeList, settings[2], settings[1], ratio,overlap[0], overlap[1]);
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


                        path.add(mMap.addMarker(options));
                    }
                    drawPath(path);


                }

                return true;
            }
        });

    }

    /**
     * Method that find out in which subroute a marker was clicked.
     *
     * @param marker
     * @return polyCount
     */
    public int pointFromPoly(Marker marker){
        Marker Startmarker = marker;
         PolyCount = 0;

        for(int i = 0; i< paths.size(); i++){
            for(int j = 0; j< paths.get(i).size(); j++){
                double lt = paths.get(i).get(j).getPosition().latitude;
                double lon = paths.get(i).get(j).getPosition().longitude;

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
    public void  export_csv(View view){



        //Request storage permissions during runtime
        ActivityCompat.requestPermissions( Tours_View_And_Export_Activity.this ,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_EXTERNAL_STORAGE);

        //Request storage permissions during runtime
        ActivityCompat.requestPermissions( Tours_View_And_Export_Activity.this ,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

            //write data to file
            try
            {
                fos = new FileOutputStream(file);
                fos.write(content.getBytes());

                Toast.makeText(this,"File saved at: " + file,Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this,"FileNotFound, please try again to export",Toast.LENGTH_LONG).show();
                break;
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
        String content = "";
        // iterates through the whole list and writes every Nodes Longitude and Latitude
        for (int i = 0; i < route.size() - 1; ++i)
        {
            Node add = route.get(i);
            content += settings[1] + ",0,4,0," + i + ",0,0,0,0,0,-1,5,1,"
                    + (float)add.getLatitude() + "," + (float)add.getLongitude() + ",false,99,30\n";
        }
        Node add = route.get(route.size() - 1);
        content += settings[1] + ",0,4,0," + (route.size() - 1) + ",0,0,0,0,0,-1,5,1,"
                + (float)add.getLatitude() + "," + (float)add.getLongitude() + ",false," + Integer.MIN_VALUE + ",0";

        return content;
    }

    /**
     * Back to previous activity.
     * @param view
     */
    public void tvae_back(View view){
        onBackPressed();
    }

    private class AsyncRasters extends AsyncTask<ArrayList<Node>, Void, ArrayList<ArrayList<Node>>> {
        @Override
        protected ArrayList<ArrayList<Node>> doInBackground(ArrayList<Node>... arrayLists) {
            ArrayList<ArrayList<Node>> routes = new ArrayList<>();
            Rastering raster = new Rastering(arrayLists[0], settings[2], settings[1], ratio, overlap[0], overlap[1]);
            ArrayList<ArrayList<ArrayList<Node>>> actRaster = raster.getRasters();

            int count = 0;
            actStartNodes = new ArrayList<Node>();
            for (ArrayList<ArrayList<Node>> i : actRaster) {
                if (!i.isEmpty()) {
                    ArrayList<Marker> pfad = new ArrayList<>();
                    actStartNodes.add(new Node(i.get(0).get(0).getLatitude(), i.get(0).get(0).getLongitude(), 2));
                    routes.add(tsm.travelingSalesman(i, actStartNodes.get(count), nodeList));
                    count++;
                }
            }
            return routes;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<Node>> result) {
            for (ArrayList<Node> x : result) {
                if (!x.isEmpty()) {
                    for (Node j : x) {
                        double lt = j.getLatitude();
                        double lon = j.getLongitude();
                        MarkerCounter++;
                        String text = String.valueOf(MarkerCounter);
                        Bitmap bitmap = makeBitmap(Tours_View_And_Export_Activity.this, text, colour);
                        MarkerOptions options = new MarkerOptions()
                                .draggable(false)
                                .position(new LatLng((float) lt, (float) lon))
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .anchor((float) 0.5, (float) 0.5);
                        path.add(mMap.addMarker(options));
                    }
                    paths.add(path);

                    PolylineOptions options = new PolylineOptions()
                            .width(10);
                    for (int i = 0; i < path.size(); i++) {
                        if (path.get(i) != null || path.size() > 0)
                            options.add(path.get(i).getPosition());

                    }
                    int lineColor = colour % 4;
                    switch (lineColor) {
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

                    colour++;
                    MarkerCounter = 0;
                    path = new ArrayList<>();
                    allRoutes.add(x);
                }
            }
        }
    }

    private class AsyncRaster extends AsyncTask<ArrayList<Node>, Void, ArrayList<Node>> {
        @Override
        protected ArrayList<Node> doInBackground(ArrayList<Node>... arrayLists) {
            Rastering raster = new Rastering(arrayLists[0], settings[2], settings[1], ratio, overlap[0], overlap[1]);

            ArrayList<ArrayList<Node>> actRaster = raster.getRaster();
            if (actRaster.isEmpty()) {
                route = nodeList;
            } else {
                actStartNode = new Node(actRaster.get(0).get(0).getLatitude(), actRaster.get(0).get(0).getLongitude(), 2);
                route = tsm.travelingSalesman(actRaster, actStartNode, nodeList);
            }
            return route;
        }

        @Override
        protected void onPostExecute(ArrayList<Node> result) {
            for (int i = 0; i < result.size(); i++) {
                double lt = result.get(i).getLatitude();
                double lon = result.get(i).getLongitude();

                MarkerCounter++;
                String text = String.valueOf(MarkerCounter);
                Bitmap bitmap = makeBitmap(Tours_View_And_Export_Activity.this, text, 2);

                MarkerOptions options = new MarkerOptions()
                        .draggable(false)
                        .position(new LatLng((float) lt, (float) lon))
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .anchor((float) 0.5, (float) 0.5);


                path.add(mMap.addMarker(options));
            }
            drawPath(path);
            allRoutes.add(result);
        }
    }

    private class AsyncTSM extends AsyncTask<Void, Void, TravelingSalesman> {

        @Override
        protected TravelingSalesman doInBackground(Void... voids) {

            return new TravelingSalesman();
        }

        @Override
        protected void onPostExecute(TravelingSalesman result) {
            tsm = result;
        }
    }
}

