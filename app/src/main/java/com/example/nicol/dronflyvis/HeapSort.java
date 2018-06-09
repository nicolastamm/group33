package com.example.nicol.dronflyvis;

import com.example.nicol.dronflyvis.Node;

import java.util.ArrayList;

/**
 * @author Martin
 * @author Johannes
 */
public class HeapSort
{
	private ArrayList<Node> column;
	private boolean min;
	
	/**
	 * Starts an MinHeapSort
	 * @param column the List to sort
	 * @return the sorted list is returned
	 */
	public ArrayList<Node> sort(ArrayList<Node> column)
	{
		this.column = column;
		this.min = true;
		
		sortList(0, column.size() - 1);
		
		return this.column;
	}
	
	/**
	 * Starts an MinHeapSort
	 * @param column the List to sort
	 * @param min true for MinHeapSort, false for MaxHeapSort
	 * @return the sorted list is returned
	 */
	public ArrayList<Node> sort(ArrayList<Node> column, boolean min)
	{
		this.column = column;
		this.min = min;
		
		sortList(0, column.size() - 1);
		
		return this.column;
	}

	/**
	 * Starts the Heapsort 
	 * @param i first index of the list to sort
	 * @param j last index
	 */
	private void sortList(int i, int j) 
	{
		start();
		int k = j;
		while(k >= 1)
		{
			swap(i, k);
			k--;
			sink(i, k);
		}
	}

	/**
	 * refreshes the sink criteria of the Heapsort
	 * @if is the Minimumheapsort
	 * @else is the Maximumheapsort
	 * @param i
	 * @param j
	 */
	private void sink(int i, int j) 
	{
		while((2 * i) + 1 <= j)
		{
			int k = (2 * i) + 1;
			if(min)
			{
				if((k < j) && (column.get(k + 1).lth(column.get(k))))
				{
					k++;
				}
				if(column.get(i).leq(column.get(k)))
				{
					break;
				}
				else
				{
					swap(i, k);
					i = k;
				}
			}
			else
			{
				if((k < j) && (column.get(k + 1).gth(column.get(k))))
				{
					k++;
				}
				if(column.get(i).geq(column.get(k)))
				{
					break;
				}
				else
				{
					swap(i, k);
					i = k;
				}
			}
		}
	}
	
	/**
	 * Swap the Nodes with indices @param i and @param k
	 * @param i first index
	 * @param k second index
	 */
	private void swap(int i, int k)
	{
		Node fstTemp = column.get(i);
		Node secTemp = column.get(k);
		column.remove(k);
		column.remove(i);
		column.add(i, secTemp);
		column.add(k, fstTemp);
	}

	/**
	 * Calls sink n/2 times
	 * To get the Heap structure
	 */
	private void start()
	{
		for(int a = (column.size() / 2); a >= 0; --a)
		{
			sink(a, (column.size() - 1));
		}
	}
}
