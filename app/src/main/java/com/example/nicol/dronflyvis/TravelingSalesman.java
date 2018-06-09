package com.example.nicol.dronflyvis;

import com.example.nicol.dronflyvis.HeapSort;
import com.example.nicol.dronflyvis.LongitudeComparator;
import com.example.nicol.dronflyvis.Node;
import com.example.nicol.dronflyvis.Rastering;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Martin
 * (@author Johannes)
 */
public class TravelingSalesman 
{
	/**
	 * Calculates an approximately optimal route
	 * @param grid stores the route points
	 * @return an ArrayList which stores the order of the route points
	 */
	public ArrayList<Node> travelingSalesman(ArrayList<ArrayList<Node>> grid , Node startNode)
	{
		HeapSort heap = new HeapSort();
		ArrayList<Node> route = new ArrayList<Node>();
		ArrayList<Node> lastColumn = null;
		ArrayList<Node> firstColumn = null;
		ArrayList<Node> splitted = null;
		boolean uneven;
		double shortestDist = Double.MAX_VALUE;
		Node closestNeighbor = null;
		int l = 0;
		int k = 0;

		uneven = (grid.size() % 2 != 0);
		
		route.add(startNode);						//adds the position from the pilot as the first node to the route
		
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

		grid.get(k).remove(closestNeighbor);
		route.add(closestNeighbor);


		while(l < grid.get(k).size())
		{
			route.add(grid.get(k).get(l));
			grid.get(k).remove(l);
		}	

		if(k > (grid.size() / 2))		//startNode is placed in/on the right half/side of the polygon
		{
			k--;
			while(k > 0)
			{
				int lastIndex = grid.get(k).size() - 1;
				route.add(grid.get(k).get(lastIndex));		//get last element of list with index k
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
				
				splitted = addNodesInRoute(route, act);
				if(splitted != null)
				{
					grid.add(splitted);
				}
				i++;
			}
			/*
			 * if the columns were uneven the last column will be sorted like the one before
			 */
			if(uneven)
			{
				lastColumn = heap.sort(lastColumn, true);
				splitted = addNodesInRoute(route, lastColumn);
				if(splitted != null)
				{
					grid.add(splitted);
				}
			}
		}
		else	//startNode is placed in/on the left half/side of the polygon
		{
			k++;
			while(k < grid.size())
			{
				int lastIndex = grid.get(k).size() - 1;
				route.add(grid.get(k).get(lastIndex));		//get last element of list with index k
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
				
				act = heap.sort(act, sort);
				
				splitted = addNodesInRoute(route, act);
				if(splitted != null)
				{
					grid.add(splitted);
				}
				i--;
			}
			
			if(uneven)
			{
				firstColumn = heap.sort(firstColumn, true);
				splitted = addNodesInRoute(route, firstColumn);
				if(splitted != null)
				{
					grid.add(splitted);
				}
			}
		}
		route.add(startNode);
		return route;			//returns the route to fly for the drone
	}
	
	
	/**
	 * Adds the Nodes to the route
	 * If the distance between two neighbors nodes is to big, the list is splitted at this index
	 * @param route to add the nodes
	 * @param list contains the nodes to add
	 * @return null iff all nodes of list are added, else the splitted list will be returned
	 */
	private ArrayList<Node> addNodesInRoute(ArrayList<Node> route, ArrayList<Node> list)
	{/*
		for(int i = 0; i < list.size() - 1; ++i)
		{
			route.add(list.get(i));
			if(Math.abs(list.get(i).getLatitude() - list.get(i + 1).getLatitude()) > 0.0003)
			{
				List<Node> nodes = list.subList(i + 1, list.size());
				int length = list.size() - i - 1;
				list = new ArrayList<Node>();
				for(int j = 0; j < length; ++j)
				{
					list.add(nodes.get(j));
				}
				
				return list;
			}

		}
		route.add(list.get(list.size() - 1));*/
		route.addAll(list);
		
		return null;
	}	
	
}
