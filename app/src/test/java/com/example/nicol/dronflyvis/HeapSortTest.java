package com.example.nicol.dronflyvis;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class HeapSortTest {

    @Test
    public void sort1(){
        HeapSort hs = new HeapSort();

        ArrayList<Node> arrayList = new ArrayList<Node>();

        Node a = new Node(47.688373,9.191949000000007,0);
        Node b = new Node(47.688573,9.191949000000001,0);
        Node c = new Node(47.687973,9.191749000000004,0);
        Node d = new Node(47.688173,9.191749000000002,0);

        arrayList.add(a);
        arrayList.add(b);
        arrayList.add(c);
        arrayList.add(d);

        ArrayList<Node> arraylist = new ArrayList<Node>();
        arraylist.add(b);
        arraylist.add(a);
        arraylist.add(d);
        arraylist.add(c);

        assertEquals(arraylist, hs.sort(arrayList, true));
    }

    @Test
    public void sort2(){
        HeapSort hs = new HeapSort();

        ArrayList<Node> arrayList = new ArrayList<Node>();

        Node a = new Node(47.688373,9.191949000000007,0);
        Node b = new Node(47.688573,9.191949000000001,0);
        Node c = new Node(47.687973,9.191749000000004,0);
        Node d = new Node(47.688173,9.191749000000002,0);

        arrayList.add(a);
        arrayList.add(b);
        arrayList.add(c);
        arrayList.add(d);

        ArrayList<Node> arraylist = new ArrayList<Node>();
        arraylist.add(c);
        arraylist.add(d);
        arraylist.add(a);
        arraylist.add(b);

        assertEquals(arraylist, hs.sort(arrayList, false));
    }

}

        /*ArrayList<Node> arrayList = new ArrayList<Node>();

        Node a = new Node(47.688373,9.191949000000001,0);
        Node b = new Node(47.688573,9.191949000000001,0);
        Node c = new Node(47.687973,9.191749000000002,0);
        Node d = new Node(47.688173,9.191749000000002,0);
        Node e = new Node(47.688373,9.191749000000002,0);
        Node f = new Node(47.688573,9.191749000000002,0);
        Node g = new Node(47.688773,9.191749000000002,0);
        Node h = new Node(47.687973,9.191549000000002,0);
        Node i = new Node(47.688173,9.191549000000002,0);
        Node j = new Node(47.688373,9.191549000000002,0);
        Node k = new Node(47.688573,9.191549000000002,0);
        Node l = new Node(47.688773,9.191549000000002,0);
        Node m = new Node(47.688973,9.191549000000002,0);
        Node o = new Node(47.687973,9.191349000000002,0);
        Node p = new Node(47.688173,9.191349000000002,0);
        Node q = new Node(47.688373,9.191349000000002,0);
        Node r = new Node(47.688573,9.191349000000002,0);
        Node s = new Node(47.688773,9.191349000000002,0);
        Node t = new Node(47.688173,9.191149000000003,0);
        Node u = new Node(47.688373,9.191149000000003,0);
        Node v = new Node(47.688573,9.191149000000003,0);
        Node w = new Node(47.688173,9.190949000000003,0);
        Node x = new Node(47.688373,9.190949000000003,0);
        Node y = new Node(47.688573,9.190949000000003,0);
        Node z = new Node(47.688373,9.190749000000004,0);
        Node aa = new Node(47.688573,9.190749000000004,0);
        Node ab = new Node(47.688373,9.190549000000004,0);
        Node ac = new Node(47.688573,9.190549000000004,0);

        arrayList.add(a);
        arrayList.add(b);
        arrayList.add(c);
        arrayList.add(d);
        arrayList.add(e);
        arrayList.add(f);
        arrayList.add(g);
        arrayList.add(h);
        arrayList.add(i);
        arrayList.add(j);
        arrayList.add(k);
        arrayList.add(l);
        arrayList.add(m);
        arrayList.add(o);
        arrayList.add(p);
        arrayList.add(q);
        arrayList.add(r);
        arrayList.add(s);
        arrayList.add(t);
        arrayList.add(u);
        arrayList.add(v);
        arrayList.add(w);
        arrayList.add(x);
        arrayList.add(y);
        arrayList.add(z);
        arrayList.add(aa);
        arrayList.add(ab);
        arrayList.add(ac);*/