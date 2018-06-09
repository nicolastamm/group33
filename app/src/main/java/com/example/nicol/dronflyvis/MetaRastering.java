package com.example.nicol.dronflyvis;

import java.util.ArrayList;

public class MetaRastering
{
    private ArrayList<Node>[] polygonListe;
    private ArrayList<Node> polygon;
    private float pixelSize , flightHeight;
    private double fov;
    /*added*/
    private static ArrayList<ArrayList<Node>> route;
    private GeoTest geoTest;

    public MetaRastering(ArrayList<Node> inputPolygon , float flightHeight , double fov)
    {
        this.polygon = inputPolygon;
        this.flightHeight = flightHeight;
        this.fov = fov;
        this.geoTest = new GeoTest(inputPolygon);
        splitPolygon();
    }

    private void splitPolygon()
    {
        double vertExcDist , horExcDist;
        horExcDist = 2.0 * flightHeight * (Math.cos(Math.toRadians(fov)/2.0));
    }

    public ArrayList<Node>[] getRasterList() {return polygonListe;}


    private static double metersToLat(double meters) {return meters / 111325.0;} // 1° of latitude is around 111.325 km.
    private static double metersToLong(double meters , double lat) {return (meters / (111325.0 * Math.cos(Math.toRadians(lat))));}


}
