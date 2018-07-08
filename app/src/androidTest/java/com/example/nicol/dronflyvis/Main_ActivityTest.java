package com.example.nicol.dronflyvis;

import com.google.android.gms.maps.model.Marker;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class Main_ActivityTest {

    @Test
    public void checkRadius1() {

        double lat1 = 47.688373;
        double lng1 = 9.191949000000001;
        double lat2 = 47.688573;
        double lng2 = 9.191949000000001;

        double l1 = Math.toRadians(lat1);
        double l2 = Math.toRadians(lat2);
        double g1 = Math.toRadians(lng1);
        double g2 = Math.toRadians(lng2);

        double dist = Math.acos(Math.sin(l1) * Math.sin(l2) + Math.cos(l1) * Math.cos(l2) * Math.cos(g1 - g2));
        if(dist < 0) {
            dist = dist + Math.PI;
        }

        int result = (int) Math.round(dist * 6378100);
        boolean actual = result > 300;

        assertEquals(false, actual);
    }

    @Test
    public void checkRadius2() {

        double lat1 = 47.688373;
        double lng1 = 9.191949000000001;
        double lat2 = 47.688567;
        double lng2 = 9.191451;

        double l1 = Math.toRadians(lat1);
        double l2 = Math.toRadians(lat2);
        double g1 = Math.toRadians(lng1);
        double g2 = Math.toRadians(lng2);

        double dist = Math.acos(Math.sin(l1) * Math.sin(l2) + Math.cos(l1) * Math.cos(l2) * Math.cos(g1 - g2));
        if(dist < 0) {
            dist = dist + Math.PI;
        }

        int result = (int) Math.round(dist * 6378100);
        boolean actual = result > 300;

        assertEquals(true, actual);
    }

    @Test
    public void getDistanceMeters() {

        double lat1 = 47.688373;
        double lng1 = 9.191949000000001;
        double lat2 = 47.688567;
        double lng2 = 9.191451;

        double l1 = Math.toRadians(lat1);
        double l2 = Math.toRadians(lat2);
        double g1 = Math.toRadians(lng1);
        double g2 = Math.toRadians(lng2);

        double dist = Math.acos(Math.sin(l1) * Math.sin(l2) + Math.cos(l1) * Math.cos(l2) * Math.cos(g1 - g2));
        if(dist < 0) {
            dist = dist + Math.PI;
        }

        int result = (int) Math.round(dist * 6378100);

        assertEquals(37, result);
    }

    @Test
    public void deletePointsInPoly() {
        ArrayList<Marker> markerlist = null;

        if (markerlist != null) {
            for (Marker m : markerlist) {
                m.remove();
            }
            markerlist = new ArrayList<>();
        }

        assertEquals(null, markerlist);
    }
}