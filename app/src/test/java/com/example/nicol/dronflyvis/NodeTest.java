package com.example.nicol.dronflyvis;

import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTest {

    @Test
    public void setPositionFlag() {
    }

    @Test
    public void getPositionFlag() {
    }

    @Test
    public void getLongitude() {
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
    public void gth() {
    }

    @Test
    public void lth() {
    }

    @Test
    public void geq() {
    }

    @Test
    public void leq() {
    }

    @Test
    public void testToString() {
        Node testNode = new Node(48.864716,2.349014,1);
        assertEquals("latitude=48.864716 longitude=2.349014 flag=1", testNode.toString());
    }

    @Test
    public void describeContents() {
    }

    @Test
    public void writeToParcel() {
    }
}