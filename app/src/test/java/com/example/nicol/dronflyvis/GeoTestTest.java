package com.example.nicol.dronflyvis;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GeoTestTest {

    private GeometryFactory geometryFactory;
    private ArrayList<Node> nodes;
    private Polygon polygon;
    private GeoTest geoTest;
    @Before
    public void setUp()
    {
        geometryFactory = new GeometryFactory();
        nodes = new ArrayList<Node>();
        nodes.add(new Node(48.519145, 9.621806,0));
        nodes.add(new Node(48.519157, 9.622234,0));
        nodes.add(new Node(48.518880, 9.622038,0));
        nodes.add(nodes.get(0));
        geoTest = new GeoTest(nodes);
    }
    @Test
    public void isPointInPoly()
    {
        assertEquals(true ,geoTest.isPointInPoly(48.519054, 9.621994));
    }
    @Test
    public void pointNotInPoly()
    {
        assertEquals(false ,geoTest.isPointInPoly(51.393652, 0.179008));
    }
}