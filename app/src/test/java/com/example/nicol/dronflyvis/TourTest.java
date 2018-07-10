package com.example.nicol.dronflyvis;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TourTest {

    private ArrayList<Node> nodes = new ArrayList<Node>();
    private Tour tour;
    @Before
    public void setUp()
    {

        nodes.add(new Node(48.519145, 9.621806,0));
        nodes.add(new Node(48.519157, 9.622234,0));
        nodes.add(new Node(48.518880, 9.622038,0));
        tour = new Tour(nodes);
    }
    @Test
    public void getTour()
    {
        assertEquals(nodes, tour.getTour());
    }

    @Test
    public void getLength()
    {
        assertEquals(65.56744956970215, tour.getLength(),0.0);
    }
}