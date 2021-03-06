package com.example.nicol.dronflyvis;

import java.util.ArrayList;

/**
 * @author Johannes
 * @author Martin
 */
public class SubTour extends Tour
{
    /**
     * Creates a new SubTour beginning and ending woth the Start Node
     * @param start the first and last node of the tour
     */
    SubTour(Node start)
    {
        tour.add(start);
        tour.add(start);
        length = 0;
    }

    /**
     * Adds a new Node to the SubTour
     * @param add the Node to add
     * @param index index of the node to insert before
     * @param distBetFstSec distance between the two Nodes with indices index - 1 and index
     * @param distToFst distance between the Node to add and the Node with index @param fstIndex
     * @param distToSec distance between the Node to add and the Node with index @param secIndex
     */
    public void addNode(Node add, int index, double distBetFstSec, double distToFst, double distToSec)
    {
        tour.add(index, add);
        length += distToFst + distToSec - distBetFstSec;
    }

    public SubTour clone()
    {
        SubTour t = new SubTour(null);
        t.length = this.length;
        t.tour = this.tour;
        return t;
    }
}
