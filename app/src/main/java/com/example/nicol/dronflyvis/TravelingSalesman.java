package com.example.nicol.dronflyvis;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Johannes
 * @author Martin
 */
public class TravelingSalesman
{
    Node startNode;
    float ourTSPLength = 0;
    float cheapestLength;
    float farthestLength;
    float cheapestOptLength;
    float farthestOptLength;

    /**
     * Calculates an approximately optimal route using three different algorithms: <br>
     * 1. cheapestInsertion <br>
     * 2. farthestInsertion <br>
     * 3. an other algorithm designed by ourselves, this one only works for a grid of points
     * @param grid stores the route
     * @param startNode the first node for the tour
     * @param inputPoly the edge-nodes of the drawn Polygon, to test if one of the tours leaves the polygon
     * @return an ArrayList which stores the order of the route points
     */
    public ArrayList<Node> travelingSalesman(ArrayList<ArrayList<Node>> grid, Node startNode , ArrayList<Node> inputPoly)
    {
        if(grid.isEmpty())
        {
            return inputPoly;
        }

        this.startNode = startNode;

        for(int i = 0; i < grid.size(); ++i)
        {
            Iterator<Node> gridIter = grid.get(i).iterator();
            while(gridIter.hasNext())
            {
                Node act = gridIter.next();
                if((float)act.getLatitude() ==  (float)startNode.getLatitude() && (float)act.getLongitude() ==  (float)startNode.getLongitude())
                {
                    gridIter.remove();
                }
            }
        }

        ArrayList<ArrayList<Node>> gridCheapest = grid;
        ArrayList<ArrayList<Node>> gridFarthest = grid;
        ArrayList<ArrayList<Node>> gridOur = grid;

        /*
         * Calculating 5 tour, to get one approximately optimal one<br>
         * The first one is the cheapest insertion heuristic<br>
         * The second one is the farthest insertion heuristic<br>
         * The third one is a heuristic worked out by ourselves<br>
         * At the end we optimize the results of the cheapest and farthest Insertion
         */
        SubTour cheapestTour;
        ArrayList<Node> cheapest = twoDimToOneDim(gridCheapest);
        cheapestTour = cheapestInsertion(cheapest);

        SubTour farthestTour;
        ArrayList<Node> farthest = twoDimToOneDim(gridFarthest);
        farthestTour = farthestInsertion(farthest);

        ArrayList<Node> ourTSPRoute;
        ArrayList<ArrayList<Node>> ourtsp = gridOur;
        ourTSPRoute = ourTSP(ourtsp);

        Tour cheapestRouteOpt = opt(cheapestTour.clone());
        cheapestOptLength = (float) cheapestRouteOpt.getLength();

        Tour farthestRouteOpt = opt(farthestTour.clone());
        farthestOptLength = (float) farthestRouteOpt.getLength();

        System.out.println("Lengths :" + cheapestLength + " " + farthestLength + " " +  ourTSPLength + " " +   cheapestOptLength
                + " " + farthestOptLength);

        /*
         * search the shortest route of the 5 which we have calculated
         */
        if(cheapestLength <= farthestLength && cheapestLength <= ourTSPLength && cheapestLength <= cheapestOptLength && cheapestLength <= farthestOptLength)
        {
            ArrayList<Node> tour = cheapestTour.getTour();
            tour.remove(tour.size() - 1);
            return tour;
        }
        else if(farthestLength <= cheapestLength && farthestLength <= ourTSPLength && farthestLength <= cheapestOptLength && farthestLength <= farthestOptLength)
        {
            ArrayList<Node> tour = farthestTour.getTour();
            tour.remove(tour.size() - 1);
            return tour;
        }
        else if(cheapestOptLength <= cheapestLength && cheapestOptLength <= farthestLength && cheapestOptLength <= ourTSPLength && cheapestOptLength <= farthestOptLength)
        {
            ArrayList<Node> tour = cheapestRouteOpt.getTour();
            tour.remove(tour.size() - 1);
            return tour;
        }
        else if(farthestOptLength <= cheapestLength && farthestOptLength <= farthestLength && farthestOptLength <= ourTSPLength && farthestOptLength <= cheapestOptLength)
        {
            ArrayList<Node> tour = farthestRouteOpt.getTour();
            tour.remove(tour.size() - 1);
            return tour;
        }
        else
        {
            return ourTSPRoute;
        }
        //return new ArrayList<Node>();
    }

    /**
     * Creates an ArrayList out of an ArrayList of ArrayLists
     * @param grid as an two-dimensional ArrayList (ArrayList<ArrayList<Node>>)
     * @return an one-dimensional ArrayList (ArrayList<Node>)
     */
    private ArrayList<Node> twoDimToOneDim(ArrayList<ArrayList<Node>> grid)
    {
        ArrayList<Node> oneDim = new ArrayList<Node>();

        for(int i = 0; i < grid.size(); ++i)
        {
            for(int j = 0; j < grid.get(i).size(); ++j)
            {
                if(startNode != grid.get(i).get(j))
                {
                    oneDim.add(grid.get(i).get(j));
                }
            }
        }

        return oneDim;
    }

    /**
     * Calculates the distance in meters between two nodes
     * @param a the first node
     * @param b the second node
     * @return the distance between the two nodes a and b
     */
    public static double distance(Node a, Node b)
    {
        double earthRadius = 6371000; //in meters
        double diffLat = Math.toRadians(b.getLatitude() - a.getLatitude());
        double diffLng = Math.toRadians(b.getLongitude() - a.getLongitude());
        double curvatureOfEarth = Math.sin(diffLat/2) * Math.sin(diffLat/2) +
                Math.cos(Math.toRadians(a.getLatitude())) *
                        Math.cos(Math.toRadians(b.getLatitude())) *
                        Math.sin(diffLng/2) * Math.sin(diffLng/2);
        double angle = 2 * Math.atan2(Math.sqrt(curvatureOfEarth), Math.sqrt(1-curvatureOfEarth));

        return (float) (earthRadius * angle);
    }

    /**
     * Searches the best position to insert the Node n in the actual Tour
     * @param t the actual Tour
     * @param n the next Node to probably insert
     * @return an Array consisting of:<br>
     * 		   [0]: index of the position to insert<br>
     * 		   [1]: the distance, by which it would increase the tour<br>
     * 		   [2]: the distance between the Node, the new Node and the Node before the new Node will be inserted<br>
     * 		   [3]: the distance between the Node, after the new Node will be inserted and the new Node<br>
     * 		   [4]: the distance between the nodes, between which the new Node will be inserted
     */
    private double[] searchInsertPos(SubTour t, Node n)
    {
        ArrayList<Node> tour = t.getTour();
        double currentDist;
        double currentDistA;
        double currentDistB;
        double distAB;
        double distBet = 0;
        double distA = 0;
        double distB = 0;
        double dist = Double.MAX_VALUE;
        int index = 0;
        Node a;
        Node b;

        for(int i = 0; i < tour.size() - 1; ++i)
        {
            a = tour.get(i);
            b = tour.get(i + 1);
            currentDistA = distance(a, n);
            currentDistB = distance(n, b);
            distAB = distance(a, b);
            currentDist = currentDistA + currentDistB - distAB;

            if(currentDist < dist)
            {
                dist = currentDist;
                distA = currentDistA;
                distB = currentDistB;
                distBet = distAB;
                index = i + 1;
            }
        }

        double[] ret = new double[5];
        ret[0] = index;
        ret[1] = dist;
        ret[2] = distA;
        ret[3] = distB;
        ret[4] = distBet;

        return ret;
    }

    private Tour opt(SubTour t)
    {
        return opt(new Tour(t.getTour(), t.getLength()));
    }

    /**
     * Optimizes a route by swapping nodes, if this reduces the distance
     * @param t the Tour to optimize
     * @return the optimized route
     */
    private Tour opt(Tour t)
    {
        boolean flag = false;
        ArrayList<Node> route = copyList(t.getTour());
        double length = (float) t.getLength();

        /*
         *
         */
        int i = 1;
        int max = route.size() - 1;
        while(i < max)
        {
            int j = i + 1;
            while(j < max)
            {
                ArrayList<Node> tmp = copyList(route);
                /*
                 * create a tour without swapping nodes <br>
                 * after that, swapping
                 */
                Tour fst = new Tour(route);

                Collections.swap(route, i, j);

                Tour sec = new Tour(route);

                //swap nodes with index i and j, if this will reduce the length of the route
                if(fst.getLength() <= sec.getLength() && fst.getLength() < length  && (float)sec.getLength() != (float)length)
                {
                    route = fst.getTour();
                    length = fst.getLength();
                    flag = true;
                }
                else if(sec.getLength() <= fst.getLength() && sec.getLength() < length && (float)sec.getLength() != (float)length)
                {
                    route = sec.getTour();
                    length = sec.getLength();
                    flag = true;
                }
                else
                {
                    route = tmp;
                }
                j++;

            }
            i++;
        }

        if(flag)
        {
            //route = opt(new Tour(route, length)).getTour();
        }

        ArrayList<Node> tmp = copyList(route);
        tmp.remove(tmp.size() - 1);

        Tour fst = new Tour(tmp);
        Collections.reverse(tmp);
        Tour sec = new Tour(tmp);
        if(fst.getLength() <= sec.getLength())
        {
            return (new Tour(route));
        }
        else
        {
            Collections.reverse(route);
            return (new Tour(route));
        }
    }

    /**
     * Retruns a new list to avoid pointer problems with lists
     * @param list the list to copy
     * @return a list containing all values of the input list
     */
    public static ArrayList<Node> copyList(ArrayList<Node> list)
    {
        ArrayList<Node> lst = new ArrayList<Node>();

        for(int i = 0; i < list.size(); ++i)
        {
            lst.add(list.get(i));
        }

        return lst;
    }

    //=========================================CHEAPEST=========================================
    /**
     * calculating a route using the heuristic of cheapest-insertion
     * @param grid the ArrayList containing all Nodes, for the Route
     * @return an ArrayList of all the Nodes from the input in the right order for the route
     */
    private SubTour cheapestInsertion(ArrayList<Node> grid)
    {
        SubTour tour = new SubTour(startNode);

        if(!grid.isEmpty())
        {
            double[] dists = new double[5];

            Iterator<Node> gridIter = grid.iterator();
            Node act;
            double currentDist;
            double dist = Double.MAX_VALUE;
            Node closestNeighbour = null;

            /*
             * add the first Node into the SubTour
             * Search the node, which would increase the total length of the SubTour
             * by the smallest value
             */
            while (gridIter.hasNext()) {
                act = gridIter.next();
                currentDist = distance(startNode, act);
                if (currentDist < dist) {
                    dist = currentDist;
                    closestNeighbour = act;
                }
            }

            tour.addNode(closestNeighbour, 1, 0, dist, dist);
            grid.remove(closestNeighbour);

            double[] closestDists = new double[5];
            gridIter = grid.iterator();
            dist = Double.MAX_VALUE;
            closestNeighbour = null;

            /*
             * add the first Node into the SubTour
             * Search the node, which would increase the total length of the SubTour
             * by the smallest value
             */
            while (!grid.isEmpty()) {
                gridIter = grid.iterator();
                dist = Double.MAX_VALUE;
                while (gridIter.hasNext()) {
                    act = gridIter.next();
                    dists = searchInsertPos(tour, act);

                    if (dists[1] < dist) {
                        closestDists = dists;
                        dist = closestDists[1];
                        closestNeighbour = act;
                    }
                }

                tour.addNode(closestNeighbour, (int) closestDists[0], closestDists[4], closestDists[2], closestDists[3]);
                grid.remove(closestNeighbour);
            }
        }

        cheapestLength = (float) tour.getLength();
        return tour;
    }

    //=========================================FARTHEST=========================================
    /**
     * calculating a route using the heuristic of farthest-insertion
     * @param grid the ArrayList containing all Nodes, for the Route
     * @return an ArrayList of all the Nodes from the input in the right order for the route
     */
    private SubTour farthestInsertion(ArrayList<Node> grid)
    {
        SubTour tour = new SubTour(startNode);

        if(!grid.isEmpty())
        {
            double[] dists = new double[5];

            Iterator<Node> gridIter = grid.iterator();
            Node act = null;
            double currentDist;
            double dist = -1;
            Node farthestNeighbour = null;

            /*
             * add the first Node into the SubTour
             * Search the node, which would increase the total length of the SubTour
             * by the smallest value
             */
            while(gridIter.hasNext())
            {
                act = gridIter.next();
                currentDist = distance(startNode, act);
                if(currentDist > dist)
                {
                    dist = currentDist;
                    farthestNeighbour = act;
                }
            }

            tour.addNode(farthestNeighbour, 1, 0, dist, dist);
            grid.remove(farthestNeighbour);

            double[] farthestDists = new double[5];
            gridIter = grid.iterator();
            dist = -1;
            farthestNeighbour = null;

            /*
             * add the first Node into the SubTour
             * Search the node, which would increase the total length of the SubTour
             * by the smallest value
             */
            while(!grid.isEmpty())
            {
                gridIter = grid.iterator();
                dist = -1;
                while(gridIter.hasNext())
                {
                    act = gridIter.next();
                    dists = searchInsertPos(tour, act);

                    if(dists[1] > dist)
                    {
                        farthestDists = dists;
                        dist = farthestDists[1];
                        farthestNeighbour = act;
                    }
                }

                /*
                 **********Special case**********
                 * Without it it sometimes crash
                 */
                if(grid.size() == 1 && act != farthestNeighbour)
                {
                    farthestDists = searchInsertPos(tour, act);
                    farthestNeighbour = act;
                }

                tour.addNode(farthestNeighbour, (int) farthestDists[0], farthestDists[4], farthestDists[2], farthestDists[3]);
                grid.remove(farthestNeighbour);
            }

        }

        farthestLength = (float) tour.getLength();
        return tour;
    }


    //==========================================OURTSP==========================================
    /**
     * Calculates an approximately optimal route
     * @param grid stores the route points
     * @return an ArrayList which stores the order of the route points
     */
    private ArrayList<Node> ourTSP(ArrayList<ArrayList<Node>> grid)
    {
        HeapSort heap = new HeapSort();
        ArrayList<Node> route = new ArrayList<Node>();
        ArrayList<Node> lastColumn = null;
        ArrayList<Node> firstColumn = null;
        ArrayList<ArrayList<Node>> splitted = new ArrayList<ArrayList<Node>>();
        ourTSPLength = 0;
        boolean uneven;
        boolean split = false;
        double shortestDist = Double.MAX_VALUE;
        Node closestNeighbor = null;
        int l = 0;
        int k = 0;

        route.add(startNode);						//adds the position from the pilot as the first node to the route

        /*
         * If the grid is empty, or it contains only one List, which is Empty,
         * then a list only containing to startNode will be returned
         */
        if(grid.isEmpty() || (grid.size() == 1 && grid.get(0).isEmpty()))
        {
            return route;
        }

        uneven = (grid.size() % 2 != 0);

        for(int i = 0; i < grid.size(); ++i)
        {
            ArrayList<Node> tmp = grid.get(i);

            tmp = heap.sort(tmp);
        }

        grid.sort(new LongitudeComparator(1));		//will sort grid forwards

        /*
         * search closest Neighbor
         */
        for(int i = 0; i < grid.size(); ++i)
        {
            ArrayList<Node> tmp = grid.get(i);
            for(int j = 0; j < tmp.size(); ++j)
            {	//calculate distance between startNode and the actual Node
                double dist = Math.abs(Math.sqrt(Math.pow((tmp.get(j).getLatitude()  - startNode.getLatitude()),  2) +
                        Math.pow((tmp.get(j).getLongitude() - startNode.getLongitude()), 2)));
                if(dist < shortestDist)
                {
                    shortestDist = dist;
                    closestNeighbor = tmp.get(j);
                    l = j;							//position in the list
                    k = i;							//number of the list
                }
            }
        }

        ourTSPLength += distance(startNode, closestNeighbor);
        grid.get(k).remove(closestNeighbor);
        route.add(closestNeighbor);

        /*
         * walks through the first list, which is the nearest list to the startnode,
         * until it reaches the end of the list
         */
        while(l < grid.get(k).size())
        {
            if(grid.get(k).get(l).getPositionFlag() == 1)
            {
                //probably incomplete
                break;
            }
            Node act = grid.get(k).get(l);
            ourTSPLength += distance(route.get(route.size() - 1), act);
            route.add(act);
            grid.get(k).remove(l);
        }

        if(k > (grid.size() / 2))		//startNode is placed in/on the right half/side of the polygon
        {
            k--;
            while(k > 0)
            {
                int lastIndex = grid.get(k).size() - 1;
                Node act = grid.get(k).get(lastIndex);		//get last element of list with index k
                ourTSPLength += distance(route.get(route.size() - 1), act);
                route.add(act);
                grid.get(k).remove(lastIndex);

                k--;
            }
            /*
             * if there is an uneven number of columns the last one is removed
             */
            if(uneven)
            {
                lastColumn = grid.get(grid.size() - 1);
                grid.remove(lastColumn);
            }

            int i = 0;
            Iterator<ArrayList<Node>> gridIter = grid.iterator();
            ArrayList<Node> act;
            while(gridIter.hasNext())
            {
                act = gridIter.next();
                boolean sort = (i % 2 == 0);
                if(act.isEmpty() && gridIter.hasNext())
                {
                    act = gridIter.next();
                    i++;
                }

                if(sort)
                {
                    act = heap.sort(act, false);
                }

                /*
                 *
                 */
                Iterator<Node> actIter = act.iterator();
                while(actIter.hasNext())
                {
                    Node elem = actIter.next();
					/*
					if(elem.getPositionFlag() == 1)
					{
						ourTSPLength += distance(route.get(route.size() - 1), elem);
						route.add(elem);
						actIter.remove();
						splitted.add(act);
						split = true;
						//break;
					}*/

                    ourTSPLength += distance(route.get(route.size() - 1), elem);
                    route.add(elem);
                    actIter.remove();
                }
//				route.addAll(act);
                i++;
            }
            /*
             * if the columns were uneven the last column will be sorted like the one before
             */
            if(uneven)
            {
                lastColumn = heap.sort(lastColumn, true);
                /*
                 *
                 */
                Iterator<Node> lastColumnIter = lastColumn.iterator();
                while(lastColumnIter.hasNext())
                {
                    Node elem = lastColumnIter.next();
					/*
					if(elem.getPositionFlag() == 1)
					{
						//break;
					}*/

                    ourTSPLength += distance(route.get(route.size() - 1), elem);
                    route.add(elem);
                    lastColumnIter.remove();
                }
//				route.addAll(lastColumn);
            }
        }
        else	//startNode is placed in/on the left half/side of the polygon
        {
            k++;
            while(k < grid.size())
            {
                int lastIndex = grid.get(k).size() - 1;
                Node act = grid.get(k).get(lastIndex);		//get last element of list with index k
                ourTSPLength += distance(route.get(route.size() - 1), act);
                route.add(act);		//get last element of list with index k
                grid.get(k).remove(lastIndex);

                k++;
            }

            if(uneven)
            {
                firstColumn = grid.get(0);
                grid.remove(firstColumn);
            }

            grid.sort(new LongitudeComparator(-1));		//will sort grid backwards
            /*
             * sorts always one column in maxHeapsort and the next one in minHeapsort
             */
            int i = grid.size() - 1;
            Iterator<ArrayList<Node>> gridIter = grid.iterator();
            ArrayList<Node> act;
            while(gridIter.hasNext())
            {
                act = gridIter.next();
                boolean sort = (i % 2 == 0);
                if(act.isEmpty() && gridIter.hasNext())
                {
                    act = gridIter.next();
                    i--;
                }

                if(split)
                {
                    act = heap.sort(act, !sort);
                    i--;
                }
                else
                {
                    act = heap.sort(act, sort);
                }
                /*
                 *
                 */
                Iterator<Node> actIter = act.iterator();
                while(actIter.hasNext())
                {
                    Node elem = actIter.next();
					/*if(elem.getPositionFlag() == 1)
					{
						ourTSPLength += distance(route.get(route.size() - 1), elem);
						route.add(elem);
						actIter.remove();
						splitted.add(act);
						split = true;
						break;
					}*/

                    ourTSPLength += distance(route.get(route.size() - 1), elem);
                    route.add(elem);
                    actIter.remove();
                    split = false;
                }
//				route.addAll(act);
                i--;
                if(!splitted.isEmpty() && !split)
                {
                    Iterator<ArrayList<Node>> splitIter = splitted.iterator();
                    while(splitIter.hasNext())
                    {
                        ArrayList<Node> elem = splitIter.next();
                        Iterator<Node> elemIter = elem.iterator();
                        while(elemIter.hasNext())
                        {
                            Node node = elemIter.next();
                            ourTSPLength += distance(route.get(route.size() - 1), node);
                            route.add(node);
                            elemIter.remove();
                        }
                    }
                }
            }

            if(uneven)
            {
                firstColumn = heap.sort(firstColumn, true);

                Iterator<Node> firstColumnIter = firstColumn.iterator();
                while(firstColumnIter.hasNext())
                {
                    Node elem = firstColumnIter.next();
					/*
					if(elem.getPositionFlag() == 1)
					{
						//break;
					}*/

                    ourTSPLength += distance(route.get(route.size() - 1), elem);
                    route.add(elem);
                    firstColumnIter.remove();
                }

//				route.addAll(firstColumn);
            }
        }

        ourTSPLength += distance(route.get(route.size() - 1), startNode);
        //route.add(startNode);
        return route;			//returns the route to fly for the drone
    }

}
