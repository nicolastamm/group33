package com.example.nicol.dronflyvis;

import com.example.nicol.dronflyvis.Node;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author Johannes
 */
public class LongitudeComparator implements Comparator<ArrayList<Node>>
{
	private int sig;
	
	/**
	 * @param sig changes sorting direction
	 */
	LongitudeComparator(int sig)
	{
		this.sig = sig;
	}
	
	@Override
	public int compare(ArrayList<Node> a, ArrayList<Node> b)
	{
		if(a.isEmpty())
		{
			return -1;
		}
		else if(b.isEmpty())
		{
			return 1;
		}
		/*
		 * Subtract the Longitude of the last element of b from the Longitude of the last element of a.
		 * If the Longitude of the last element of a is larger, the result will be positive.
		 * If the Longitude of the last element of b is larger, the result will be negative.
		 * Else, it will be 0.
		 */
		return (int) (Math.signum(sig * (a.get(a.size() - 1).getLongitude() - b.get(b.size() - 1).getLongitude())));
	}
}

