package com.example.nicol.dronflyvis;

import java.util.ArrayList;

/**
 * @author Johannes
 *
 */
public class Tour
{
    protected ArrayList<Node> tour = new ArrayList<Node>();
    protected double length;

    /**
     * Creat a new Tour with a given route and its length
     * @param tour the given tour
     * @param length the length of the tour
     */
    Tour(ArrayList<Node> tour, double length)
    {
        this.tour = tour;
        this.length = length;
    }

    /**
     * Empty Constructor needed, because SubTour extends from this class and has
     * an different constructor, using other arguments
     */
    public Tour()
    {
        length = 0;
    }

    /**
     * @return the tour
     */
    public ArrayList<Node> getTour()
    {
        return tour;
    }

    /**
     * @return the length
     */
    public double getLength()
    {
        return length;
    }
}

