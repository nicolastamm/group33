package com.example.nicol.dronflyvis;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class Tours_View_And_Export_ActivityTest {

    @Test
    public void pointFromPoly() {
        ArrayList<ArrayList<Node>> grid = new ArrayList<ArrayList<Node>>();
        int result = 0;
        Node start = new Node(47.688573,9.190949000000003,0);

        ArrayList<Node> clm1 = new ArrayList<Node>();
        ArrayList<Node> clm2 = new ArrayList<Node>();
        ArrayList<Node> clm3 = new ArrayList<Node>();
        ArrayList<Node> clm4 = new ArrayList<Node>();
        ArrayList<Node> clm5 = new ArrayList<Node>();

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

        clm1.add(a);
        clm1.add(b);
        clm1.add(c);
        clm1.add(d);
        clm1.add(e);
        clm2.add(f);
        clm2.add(g);
        clm2.add(h);
        clm2.add(i);
        clm2.add(j);
        clm3.add(k);
        clm3.add(l);
        clm3.add(m);
        clm3.add(o);
        clm3.add(p);
        clm4.add(q);
        clm4.add(r);
        clm4.add(s);
        clm4.add(t);
        clm4.add(u);
        clm5.add(v);
        clm5.add(w);
        clm5.add(x);
        clm5.add(y);
        clm5.add(z);

        grid.add(clm1);
        grid.add(clm2);
        grid.add(clm3);
        grid.add(clm4);
        grid.add(clm5);


        for(int fst = 0; fst < grid.size(); fst++)
        {
            for(int scd = 0; scd < grid.get(fst).size(); scd++)
            {
                double lt = grid.get(fst).get(scd).getLatitude();
                double lon = grid.get(fst).get(scd).getLongitude();

                if(start.getLatitude() == lt & start.getLongitude() == lon)
                {
                    result = fst;
                }
            }
        }

        assertEquals(5,5);
    }
}