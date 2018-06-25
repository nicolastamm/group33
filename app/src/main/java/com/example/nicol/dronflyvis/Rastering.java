package com.example.nicol.dronflyvis;

import java.util.ArrayList;

/**
 * @author Nicolas-Andreas Tamm-Garetto
 */
public class Rastering
{
    private static ArrayList<Node> polygon;
    private float flightHeight;
    private double fov , fotoWidth , fotoHeight;
    private ArrayList<ArrayList<Node>> raster;
    private ArrayList<ArrayList<ArrayList<Node>>> rasters;
    private ArrayList<Node[]> boundingBoxes;
    private Node[] boundingBox;
    private GeoTest geoTest;

    Rastering(ArrayList<Node> inputPolygon, double fov, float flightHeight)
    {
        this.polygon = inputPolygon;
        this.fov = fov;
        this.flightHeight = flightHeight;
        this.geoTest = new GeoTest(inputPolygon);
        fotoWidth = (2.0 * flightHeight) * (Math.tan(Math.toRadians(fov / 2.0)));
        fotoHeight = fotoWidth * (3.0/4.0); //Assuming 4:3 aspect ratio
        fotoWidth *= 0.30; //70% horizontal overlap.
        fotoHeight *= 0.15; //85% vertical overlap.
    }

    static Double[] searchForBorderCoordinates(ArrayList<Node> polygon)
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
            if(polygon.get(i).getLongitude() < longMin) {
                longMin = polygon.get(i).getLongitude();
            }
            else if(polygon.get(i).getLongitude() > longMax) {
                longMax = polygon.get(i).getLongitude();
            }

            if(polygon.get(i).getLatitude() < latMin) {
                latMin = polygon.get(i).getLatitude();
            }
            else if(polygon.get(i).getLatitude() > latMax) {
                latMax = polygon.get(i).getLatitude();
            }
        }
        return new Double[]{longMin, longMax, latMin, latMax};
    }

    private ArrayList<ArrayList<Node>> placeRaster(Double[] borderCoordinates)
    {
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


    static double metersToLat(double meters) {
        return meters / 111325.0;
    } // 1° of latitude is around 111.325 km.

    static double metersToLong(double meters, double lat) {
        return (meters / (111325.0 * Math.cos(Math.toRadians(lat))));
    }

    public static void main(String[] args) {
        ArrayList<Node> test = new ArrayList<>();
        test.add(new Node(47.707475, 9.188631, 2));
        test.add(new Node(47.707475, 9.201961, 2));
        test.add(new Node(47.698420, 9.188631, 2));
        test.add(new Node(47.698420, 9.201961, 2));

        Rastering raster = new Rastering(test, 78.8, 100);
        ArrayList<ArrayList<ArrayList<Node>>> thisRasters = raster.getRasters();
        for (ArrayList<ArrayList<Node>> thisRaster : thisRasters) {
            System.out.println(thisRaster);
        }
    }

    private ArrayList<ArrayList<ArrayList<Node>>> splitPolygon() {
        Double[] borderCoordinates = searchForBorderCoordinates(polygon);

        double polygonHeight = Math.abs(borderCoordinates[2] - borderCoordinates[3]);
        polygonHeight *= 111325.0;

        double polygonWidth = Math.abs(borderCoordinates[0] - borderCoordinates[1]);
        polygonWidth *= 111325.0 * Math.cos(Math.toRadians(borderCoordinates[2]));

        int verticalAmountFotos =(int) Math.ceil(polygonHeight / fotoHeight);
        int horizontalAmountFotos =(int) Math.ceil(polygonWidth / fotoWidth);

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

        double fotoWidthCoord = metersToLong(fotoWidth , borderCoordinates[3]);
        double fotoHeightCoord = metersToLat(fotoHeight);
        double subPolyWidth = subPolycols * fotoWidthCoord;
        double subPolyHeight = subPolyrows * fotoHeightCoord;
        double traversedLongitude = 0;
        double traversedLatitude = 0;
        rasters = new ArrayList<>();
        for (; borderCoordinates[0] + traversedLongitude - (subPolyWidth / 2.0) <= borderCoordinates[1] + (subPolyWidth / 2.0); )
        {
            for (; borderCoordinates[2] + traversedLatitude - (subPolyHeight / 2.0) <= borderCoordinates[3] + (subPolyHeight / 2.0); )
            {
                //Add to rasters the raster inside the specified sub-BoundingBox
                rasters.add(placeRaster(new Double[]
                        {
                                borderCoordinates[0] + traversedLongitude,
                                borderCoordinates[0] + traversedLongitude + subPolyWidth,
                                borderCoordinates[2] + traversedLatitude,
                                borderCoordinates[2] + traversedLatitude + subPolyHeight
                        }));

                traversedLatitude += subPolyHeight;
                traversedLatitude += metersToLat(fotoHeight);
            }
            traversedLatitude = 0;
            traversedLongitude += subPolyWidth + metersToLong(fotoWidth, borderCoordinates[2]);
        }
        rasters.removeIf(ArrayList::isEmpty);
        return rasters;
    }

    ArrayList<ArrayList<ArrayList<Node>>> getRasters() {
        return splitPolygon();
    }

    public static ArrayList<Node> getPolygon()
    {
        return polygon;
    }

    public ArrayList<ArrayList<Node>> getRaster() {
        return placeRaster(searchForBorderCoordinates(polygon));
    public ArrayList<ArrayList<Node>> getRaster() {
        return placeRaster(searchForBorderCoordinates(polygon));
    }
}