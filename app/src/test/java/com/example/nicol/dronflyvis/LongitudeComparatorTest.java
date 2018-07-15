package com.example.nicol.dronflyvis;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LongitudeComparatorTest {

    private LongitudeComparator comp;
    @Before
    public void setUp() throws Exception
    {
        comp = new LongitudeComparator(0);
    }
    @Test
    public void compare()
    {
        ArrayList<Node> testNodesA = new ArrayList<Node>();
        ArrayList<Node> testNodesB = new ArrayList<Node>();

        testNodesA.add(new Node(147.6744,  9.1649, 0));
        testNodesB.add(new Node(47.6744,  19.1649, 0));
        assertEquals(0, comp.compare(testNodesA, testNodesB));
    }
    @Test
    public void firstNodeEmpty()
    {
        ArrayList<Node> testNodesA = new ArrayList<Node>();
        ArrayList<Node> testNodesB = new ArrayList<Node>();

        testNodesB.add(new Node(47.6744,  9.1649, 0));
        assertEquals(-1, comp.compare(testNodesA, testNodesB));
    }
    @Test
    public void secondNodeEmpty()
    {
        ArrayList<Node> testNodesA = new ArrayList<Node>();
        ArrayList<Node> testNodesB = new ArrayList<Node>();

        testNodesA.add(new Node(47.6744,  9.1649, 0));
        assertEquals(1, comp.compare(testNodesA, testNodesB));
    }
}