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
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class Tours_View_And_Export_Activity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static  String FILE_NAME = "DronTour.csv";
    ArrayList<Marker> pfad = new ArrayList<>();

    private ImageButton infobuch;
    private GoogleMap mMap;
    public int MarkerCounter= 0;
    private double height;

    private float zoom;
    private double lat;
    private double lng;
    private int mapType;
    private float[] settings;


    ArrayList<Node> nodeList;
    ArrayList<Node> route;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tours_view_and_export_activity);

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
        }


        infobuch = (ImageButton)findViewById(R.id.tvae_activity_infobuch_button);
        infobuch.setImageResource(R.drawable.infobuch);
        infobuch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Buch_PopUp_Activity.class);
                startActivity(intent);
            }
        });

        Button changeButton = (Button)findViewById(R.id.tvae_activity_change_button);

        changeButton.setOnClickListener(new View.OnClickListener() {
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

        registerForContextMenu(changeButton);

        changeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        height = 100.0;
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled (false);
        mMap.getUiSettings().setZoomControlsEnabled(true);



        mMap.setMapType(mapType);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));


        TravelingSalesman tsm = new TravelingSalesman();
        Rastering raster = new Rastering(nodeList, (float) 78.8, 100);
        route = tsm.travelingSalesman(raster.getRasters()[0] , new Node(nodeList.get(0).getLatitude(), nodeList.get(0).getLongitude(), 2));
        Log.i("test", ""+route);


        for(int i = 0; i<route.size(); i++)
        {
            double lt = route.get(i).getLatitude();
            double lon = route.get(i).getLongitude();

            MarkerCounter++;
            String text = String.valueOf(MarkerCounter);
            Bitmap bitmap = makeBitmap(this, text);

            MarkerOptions options = new MarkerOptions()
                    .draggable(false)
                    .position(new LatLng((float)lt,(float)lon))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .anchor((float)0.5, (float)0.5);;
            pfad.add(mMap.addMarker(options));
        }
        drawPfad();
        for(int j = 0; j<pfad.size();j++){
            pfad.get(j).showInfoWindow();
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

    public Bitmap makeBitmap(Context context, String text){

        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.markerroute2);
        bitmap = bitmap.copy(ARGB_8888, true);

        Canvas canvas = new  Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(8 * scale);
        paint.setShadowLayer(1f,0f,1f, Color.WHITE);

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = bitmap.getWidth()/2 - bounds.width()/2;
        int y = bitmap.getHeight()/2 - bounds.height()/3;

        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    private void drawPfad()
    {

        PolylineOptions optionss = new PolylineOptions()
                .width(7)
                .color(Color.RED);

                for(int i=0;i<pfad.size();i++ )
                {
                    if(pfad.get(i) != null || pfad.size()>0){
                        optionss.add(pfad.get(i).getPosition());
                    }
                }

                pfad.get(0).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerstart));
                pfad.get(pfad.size()-1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerstart));
                mMap.addPolyline(optionss);
    }

    public void akt5export(View view) {
        /*
         * Gets the current Date und Time, to timestamp the CSV
         */
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy' 'HH.mm");
        Date currentTime = new Date();
        String timeStamp = "" + format.format(currentTime);

        String content = null;
        //Only create Export for selected drone
        int bebopFlag = 0;
        switch(bebopFlag) {
            case 0:
                content = routeForMavicPro();
                FILE_NAME = "DJI/Route " + timeStamp + ".csv";
                break;
            case 1:
                content = routeForBebop();
                FILE_NAME += "ARPro3/" + timeStamp + " Route";
                break;
            default:
                content = "";
                Toast.makeText(this,"Invalid Drone selected",Toast.LENGTH_LONG).show();
                break;
        }

        //Request storage permissions during runtime
        ActivityCompat.requestPermissions( Tours_View_And_Export_Activity.this ,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_EXTERNAL_STORAGE);

        //Get the path to the directory to save the CSV
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/DroneTours/";
        File file = new File(path);

        //If there is no folder, create a new one

        file = new File(path + FILE_NAME);
        file.mkdirs();
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
            Toast.makeText(this,"FileNotFound, please tyr again to export",Toast.LENGTH_LONG).show();
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
     * Creates the Content for the CSV, which is needed for LitchiOnline
     * @return the content used for the CSV File
     */
    private String routeForMavicPro()
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
            content += add.getLatitude() + "," + add.getLongitude() + "," + height
                    + ",0,0,0,0,0,1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0\r\n";
        }

        return content;
    }

    /**
     * Creates the Content for the CSV, which is needed for AR Pro 3
     * @return the content used for the CSV File
     */
    private String routeForBebop()
    {
        String content = null;
        // iterates through the whole list and writes every Nodes Longitude and Latitude
        for (int i = 0; i < route.size() - 1; ++i)
        {
            Node add = route.get(i);
            content += height + ",0,4,0," + i + ",0,0,0,0,0,-1,5,1,"
                    + (float)add.getLatitude() + "," + (float)add.getLongitude() + ",false,99,30\r\n";
        }
        Node add = route.get(route.size() - 1);
        content += height + ",0,4,0," + (route.size() - 1) + ",0,0,0,0,0,-1,5,1,"
                + (float)add.getLatitude() + "," + (float)add.getLongitude() + ",false," + Integer.MIN_VALUE + ",0";

        return content;
    }

    public void act_5_back(View view){
        onBackPressed();
    }
}

