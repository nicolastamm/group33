package com.example.nicol.dronflyvis;

import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTest {

    @Test
    public void setPositionFlag() {
    }

    @Test
    public void getPositionFlag()
    {
        Node testNode = new Node(48.864716,2.349014,1);
        assertEquals(1, testNode.getPositionFlag());
    }

    @Test
    public void setLongitude() {
    }

    @Test
    public void getLatitude() {
    }

    @Test
    public void setLatitude() {
    }

    @Test
    public void gth()
    {
        Node testNode = new Node(48.864716,2.349014,1);
        Node testNode2 = new Node(13.864716,22.349014,1);
        assertEquals(true, testNode.gth(testNode2));
    }

    @Test
    public void lth()
    {
        Node testNode = new Node(47.6744,9.1649,1);
        Node testNode2 = new Node(13.864716,22.349014,1);
        assertEquals(false, testNode.lth(testNode2));
    }

    @Test
    public void geq()
    {

    }

    @Test
    public void leq() {
    }

    @Test
    public void testToString()
    {
        Node testNode = new Node(48.864716,2.349014,1);
        assertEquals("latitude=48.864716 longitude=2.349014 flag=1", testNode.toString());
    }

    @Test
    public void describeContents()
    {

    }
    @Test
    public void writeToParcel()
    {

    }
}