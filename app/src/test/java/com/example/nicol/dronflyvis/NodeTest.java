package com.example.nicol.dronflyvis;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTest {

    private Node testNode;
    private Node testNode2;
    @Before
    public void setNodes() throws Exception
    {
        testNode = new Node(48.864716,2.349014,1);
        testNode2 = new Node(13.864716,22.349014,1);
    }
    @Test
    public void getPositionFlag()
    {
        assertEquals(1, testNode.getPositionFlag());
    }

    @Test
    public void gth()
    {
        assertEquals(true, testNode.gth(testNode2));
    }

    @Test
    public void lth()
    {
        assertEquals(false, testNode.lth(testNode2));
    }

    @Test
    public void geq()
    {
        assertEquals(true, testNode.geq(testNode2));
    }

    @Test
    public void leq()
    {
        assertEquals(false, testNode.leq(testNode2));
    }

    @Test
    public void testToString()
    {
        assertEquals("latitude=48.864716 longitude=2.349014 flag=1", testNode.toString());
    }


}