package com.example.nicol.dronflyvis;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Nicolas-Andreas Tamm-Garetto
 */
public class Rastering
{
    private ArrayList<Node> polygon;
    private float flightHeight;
    private double fov , fotoWidth , fotoHeight;
    private ArrayList<ArrayList<Node>> raster;
    private ArrayList<ArrayList<Node>>[] rasters;
    private GeoTest geoTest;
    
    public Rastering(ArrayList<Node> inputPolygon, double fov, float flightHeight)
    {
        this.polygon = inputPolygon;
        this.fov = fov;
        this.flightHeight = flightHeight;
        this.geoTest = new GeoTest(inputPolygon);
        raster = placeRaster(searchForBorderCoordinates());
    }
    
    private Double[] searchForBorderCoordinates()
    {
        //Initialize with existing values. Dont know what the global minimum is, dont need to know
        double longMin = polygon.get(0).getLongitude();
        double longMax = longMin;

        double latMin = polygon.get(0).getLatitude();
        double latMax = latMin;

        //dont need first element. Values initialized with it => i=1
        // Go through all the elements and store minima and maxima for long and lat.
        for(int i = 1 ; i < polygon.size() ; i++)
        {
            if(polygon.get(i).getLongitude() < longMin)
                {longMin = polygon.get(i).getLongitude();}
            else if(polygon.get(i).getLongitude() > longMax)
                {longMax = polygon.get(i).getLongitude();}

            if(polygon.get(i).getLatitude() < latMin)
                {latMin = polygon.get(i).getLatitude();}
            else if(polygon.get(i).getLatitude() > latMax)
                {latMax = polygon.get(i).getLatitude();}
        }
        return new Double[]
                {
                        longMin + metersToLong(1 , latMin) ,
                        longMax + metersToLong(1 , latMax) ,
                        latMin + metersToLat(1),
                        latMax + metersToLat(1)
                };
    }

    private ArrayList<ArrayList<Node>> placeRaster(Double[] borderCoordinates)
    {

        fotoWidth = (2.0 * flightHeight) * (Math.tan(Math.toRadians(fov/2.0)));
        fotoHeight = fotoWidth * (3.0/4.0); //Assuming 4:3 aspect ratio
        fotoWidth *= 0.30; //70% horizontal overlap.
        fotoHeight *= 0.15; //85% vertical overlap.
        double fotoWidthCoord = metersToLong(fotoWidth , borderCoordinates[3]);
        double fotoHeightCoord = metersToLat(fotoHeight);

        ArrayList<ArrayList<Node>> outputRaster = new ArrayList<>();
        // The first for-loop places the columns,
        // therefore it "steps" with fotoWidth and stops when reaching longMax
        for(int i = 0; (double) i * fotoWidthCoord + borderCoordinates[0] <= borderCoordinates[1] + (fotoWidthCoord/2.0); i++)
        {
            //These Arraylists store only points from each column
            outputRaster.add(new ArrayList<Node>());
            double potentialNodeLongitude =  i * fotoWidthCoord + borderCoordinates[0] - (fotoWidthCoord/2.0);

            // The second for-loop fills up the columns,
            // therefore it "steps" with fotoHeight and stops when reaching latMax
            for(int j = 0 ; (double) j * fotoHeightCoord +  borderCoordinates[2] <= borderCoordinates[3] + (fotoHeightCoord/2.0); j++)
            {
                double potentialNodeLatitude = (double) j * fotoHeightCoord + borderCoordinates[2] - (fotoHeightCoord/2.0);
                //if point is in poly...
                if(geoTest.isPointInPoly(potentialNodeLatitude , potentialNodeLongitude))
                {
                    //and the column was empty...
                    if(outputRaster.get(i).isEmpty())
                        outputRaster.get(i).add
                                (new Node
                                        (
                                                potentialNodeLatitude,
                                                potentialNodeLongitude,
                                                0
                                        ) //then this point does not border a "concavity" (FLAG = 0)
                                );
                    //else, and the last node added borders "concavity" (FLAG = 1)
                    else if(outputRaster.get(i).get(outputRaster.get(i).size()-1).getPositionFlag() == 1)
                        outputRaster.get(i).add
                                (new Node
                                                (
                                                        potentialNodeLatitude,
                                                        potentialNodeLongitude,
                                                        1
                                                ) //then this point borders a "concavity" (FLAG = 1)
                                );
                    // but if the column is not empty, and the last node does not border a "concavity" (FLAG = 0)
                    else
                        outputRaster.get(i).add
                                (new Node
                                        (
                                                potentialNodeLatitude,
                                                potentialNodeLongitude,
                                                0
                                        )// then this point also does not border with "concavity" (FLAG = 0)
                                );
                }
                //If point is not in poly and the column is not empty
                else if(!outputRaster.get(i).isEmpty())
                {
                    outputRaster.get(i).get(outputRaster.get(i).size()-1).setPositionFlag(1);
                    //then the last added poly borders a "concavity" (FLAG = 1)
                }
            }
        }
        //We remove empty columns.
        outputRaster.removeIf(ArrayList::isEmpty);
        //the last point of each column can not have (FLAG = 1), we correct this as well.
        for(ArrayList<Node> i : outputRaster) i.get(i.size()-1).setPositionFlag(0);

        return outputRaster;
    }

    private void splitPolygon() {
        Double[] borderCoordinates = searchForBorderCoordinates();

        double polygonHeight = Math.abs(borderCoordinates[2] - borderCoordinates[3]);
        polygonHeight *= 111325.0;

        double polygonWidth = Math.abs(borderCoordinates[0] - borderCoordinates[1]);
        polygonWidth *= 111325.0 * Math.cos(Math.toRadians(borderCoordinates[2]));

        System.out.println("Polygon Height: " + polygonHeight);
        System.out.println("Polygon Width: " + polygonWidth);

        int verticalAmountFotos =(int) Math.ceil(polygonHeight / fotoHeight);
        int horizontalAmountFotos =(int) Math.ceil(polygonWidth / fotoWidth);

        System.out.println("Vertical Entries: " + verticalAmountFotos);
        System.out.println("Horizontal Entries: " + horizontalAmountFotos);

        int subPolycols = 0;
        int subPolyrows = 0;
        for(int i = 9 ; i >= 0 ; i--)
        {
            if((i+1) * fotoWidth < 300.0)
            {
                subPolycols = i;
                break;
            }
        }
        for(int i = 11 ; i >= 0 ; i--)
        {
            if((i+1) * fotoHeight < 300.0)
            {
                subPolyrows = i;
                break;
            }
        }
        System.out.println("SubPoly Colums: " + subPolycols);
        System.out.println("SubPoly Rows: " + subPolyrows);

        int extraCol = horizontalAmountFotos % subPolycols;
        int extraRow = verticalAmountFotos % subPolyrows;
        int amountSubPoly = (verticalAmountFotos / subPolyrows) * (horizontalAmountFotos / subPolycols);

        for(int i = 0 ; i < amountSubPoly ; i++)
        {

        }
    }


	public static void main(String[] args)
    {
        ArrayList<Node> test = new ArrayList<>();
        test.add(new Node(47.707475, 9.188631, 2));
        test.add(new Node(47.707475, 9.201961, 2));
        test.add(new Node(47.698420, 9.188631, 2));
        test.add(new Node(47.698420, 9.201961, 2));

        Rastering raster = new Rastering(test,  78.8, 100);
        raster.splitPolygon();
    }

    private static double metersToLat(double meters) {return meters / 111325.0;} // 1° of latitude is around 111.325 km.
    private static double metersToLong(double meters , double lat) {return (meters / (111325.0 * Math.cos(Math.toRadians(lat))));}
    public  ArrayList<ArrayList<Node>> getRaster() {return raster;}
    public ArrayList<ArrayList<Node>>[] getRasters() { splitPolygon();return rasters; }
}