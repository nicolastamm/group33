package com.example.nicol.dronflyvis;

import java.util.ArrayList;
//From JTS Topology Suite by Vivid Solutions under GNU LESSER GENERAL PUBLIC LICENSE Version 2.1
import com.example.nicol.dronflyvis.Node;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

class GeoTest
{
    GeometryFactory gf;
    private Polygon polygonToTest;
    GeoTest(ArrayList<Node> polygon)
    {
        polygon.add(polygon.get(0)); //LinearRing need a closed Polygon
        this.gf= new GeometryFactory();
        ArrayList<Coordinate> points = new ArrayList<Coordinate>();
        for(Node node : polygon)
        {
            points.add(new Coordinate(node.getLatitude() , node.getLongitude()));
        }
        this.polygonToTest = gf.createPolygon(new LinearRing(new CoordinateArraySequence(points
                .toArray(new Coordinate[points.size()])), gf), null);
    }
    boolean isPointInPoly(double pointLatitude , double pointLongitude)
    {
        Point point = gf.createPoint(new Coordinate(pointLatitude , pointLongitude));
        boolean result = point.within(polygonToTest);
        return result;
    }
}
