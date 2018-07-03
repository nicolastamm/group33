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
     * Creat a new Tour with a given route and calculate its length
     * @param tour the given tour
     */
    Tour(ArrayList<Node> tour)
    {
        this.tour = tour;
        length = 0;
        for(int i = 0; i < tour.size() - 1; i++)
        {
            length += TravelingSalesman.distance(tour.get(i), tour.get(i + 1));
        }
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

