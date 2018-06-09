package com.example.nicol.dronflyvis;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Nicolas-Andreas Tamm-Garetto
 */
public class Rastering
{
    private ArrayList<Node> polygon;
    private float pixelSize , flightHeight;
    private double fov;
    /*added*/
    private static ArrayList<ArrayList<Node>> route;
    private static Node startNode;
    private GeoTest geoTest;
    private ArrayList<ArrayList<Node>> [] rasters;
    
    public Rastering(ArrayList<Node> inputPolygon, Node startNode, double fov, float flightHeight, float pixelSize)
    {
        this.polygon = inputPolygon;
        this.fov = fov;
        this.flightHeight = flightHeight;
        this.pixelSize = pixelSize;
        Rastering.startNode = startNode;		//added
        this.geoTest = new GeoTest(inputPolygon);
        rasterizePolygon();						//added
    }
    
    ArrayList<ArrayList<Node>> rasterizePolygon()
    {
    	//this is where the magic happens
        ArrayList<ArrayList<Node>> outputRaster = placeRaster(searchForBorderCoordinates());
        route = outputRaster;
        return outputRaster;
    }
    
    private Double[] searchForBorderCoordinates()
    {
        //Initialize with existing values. Dont know what the global minimum is, dont need to know
        double longMin = polygon.get(0).getLongitude();
        double longMax = longMin;

        double latMin = polygon.get(0).getLatitude();
        double latMax = latMin;

        //dont need first element. Values initialized with it => i=1
        for(int i = 1 ; i < polygon.size() ; i++)
        {
            if(polygon.get(i).getLongitude() < longMin) {longMin = polygon.get(i).getLongitude();}
            else if(polygon.get(i).getLongitude() > longMax){longMax = polygon.get(i).getLongitude();}

            if(polygon.get(i).getLatitude() < latMin) {latMin = polygon.get(i).getLatitude();}
            else if(polygon.get(i).getLatitude() > latMax){latMax = polygon.get(i).getLatitude();}
        }
        return new Double[]{longMin + metersToLong(1 , latMin) , longMax + metersToLong(1 , latMax)
                , latMin + metersToLat(1), latMax + metersToLat(1)};
    }

    private ArrayList<ArrayList<Node>> placeRaster(Double[] borderCoordinates)
    {

        double fotoWidth = (2.0 * flightHeight) * (Math.tan(Math.toRadians(fov/2.0)));
        double fotoHeight = fotoWidth * (3.0/4.0); //Assuming 4:3 aspect ratio
        fotoWidth *= 0.30; //70% horizontal overlap.
        fotoHeight *= 0.15; //85% vertical overlap.
        System.out.println("Width(Long):" + fotoWidth + ", Height(Lat):" + fotoHeight);
        //Converting meters to latitude and longitude.
        fotoWidth = metersToLong(fotoWidth , borderCoordinates[3]);
        fotoHeight = metersToLat(fotoHeight);

        ArrayList<ArrayList<Node>> outputRaster = new ArrayList<>();

        for(int i = 0; (double) i * fotoWidth + borderCoordinates[0] <= borderCoordinates[1] + (fotoWidth/2.0); i++)
        {
            outputRaster.add(new ArrayList<Node>());
            double potentialNodeLongitude =  i * fotoWidth + borderCoordinates[0] - (fotoWidth/2.0);
            for(int j = 0 ; (double) j * fotoHeight +  borderCoordinates[2] <= borderCoordinates[3] + (fotoHeight/2.0); j++)
            {
                double potentialNodeLatitude = (double) j * fotoHeight + borderCoordinates[2] - (fotoHeight/2.0);
                if(geoTest.isPointInPoly(potentialNodeLatitude , potentialNodeLongitude))
                {
                    if(outputRaster.get(i).isEmpty())
                        outputRaster.get(i).add(new Node(potentialNodeLatitude, potentialNodeLongitude , 0));
                    else if(outputRaster.get(i).get(outputRaster.get(i).size()-1).getPositionFlag() == 1)
                        outputRaster.get(i).add(new Node(potentialNodeLatitude, potentialNodeLongitude , 1));
                    else
                        outputRaster.get(i).add(new Node(potentialNodeLatitude, potentialNodeLongitude , 0));
                }
                else if(!outputRaster.get(i).isEmpty())
                {
                    outputRaster.get(i).get(outputRaster.get(i).size()-1).setPositionFlag(1);
                }

                if((j+1) * fotoHeight + borderCoordinates[2] <= borderCoordinates[3] && !outputRaster.get(i).isEmpty())
                    outputRaster.get(i).get(outputRaster.get(i).size()-1).setPositionFlag(0);
            }
        }
        System.out.println(Arrays.toString(outputRaster.toArray()) + outputRaster.size());
        return outputRaster;
    }

    private static double metersToLat(double meters) {return meters / 111325.0;} // 1° of latitude is around 111.325 km.
    private static double metersToLong(double meters , double lat) {return (meters / (111325.0 * Math.cos(Math.toRadians(lat))));}

     /**
      * @return ArrayList<ArrayList<Node>>, which contains all Points of the Route
      */
	public static ArrayList<ArrayList<Node>> getRoute()		//added
	{
		return route;
	}

    /**
     * @return the startnode of the Route
     */
	public static Node getStartingpoint()					//added
	{
		return startNode;
	}
	public static void main(String[] args)
    {
        ArrayList<Node> test = new ArrayList<Node>();
        test.add(new Node(47.688573, 9.190749 , 2));
        test.add(new Node(47.688573, 9.191949, 2));
        test.add(new Node(47.687973, 9.191749, 2));
        test.add(new Node(47.688173, 9.190949, 2));

        new Rastering(test, new Node(47.688585, 9.190106, 2),  78.8, 100, 3);
    }
}