package com.example.nicol.dronflyvis;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Artuk
 * @author Hilmi
 * @author Nico
 *
 * This class concerned with creating and displaying the "book" content
 */
public class Buch_Slider extends PagerAdapter{
    Context context;
    LayoutInflater layoutInflater;

    public Buch_Slider(Context context){
        this.context = context;
    }

    public int[] images_toslide = {
            R.drawable.leftbariconspic,
            R.drawable.othericons,
            R.drawable.simplepolygon,
            R.drawable.splitpolygon,
            R.drawable.export
    };

    public String[] act_headers = {
            "Edit your polygon ",
            "Other icons worth mentioning",
            "A simple polygon",
            "The split mode",
            "Ready to export"
    };

    public String[] act_discr = {
            "While editing your polygon, we created several modi and buttons to guide your experience with our system.\n\nThis include:\n\n" +
                    "(1) Pin mode: Sometimes you want to focus on a certain area on the map and capture your map to a certain area. With this feature enabled, the map will not change its place, thus allowing better control of the editing tools.\n\n" +
                    "(2) Draw mode: This is the first step you take to create your polygon. The draw mode is already set at this point. As the name implies, it is in this mode that new points of your desired area can be added. \n\n" +
                    "(3) Erase mode: Counter to the draw mode, this mode enables the selective removal of any nodes of your choosing.\n\n" +
                    "(4) Dump Polygon: If you decide to delete the entire polygon, press this and you will be able to start again from scratch.\n\n" +
                    "(5) Import Polygon: With this feature you can import your old polygons.",
            "There are some other icons that have to be explained: In the top right corner you see the search bar of Google Maps. You  can use it as you are used to.\n\n" +
                    "In the upper right corner you might see an orange square. This tells you whether you are in a Polygon splitting mode or not. Tapping it enables the switch mode, this means that you can switch between the splitting mode and the non-splitting mode.\n\n" +
                    "At the bottom of the app are some self explanatory buttons. With the middle button you can change the map type.\n\n" +
                    "Left  to this is the help button (the red book)",
            "There are several things to pay attention to when creating polygons with our app. You will notice that two nodes are always coloured orange.\n\n" +
                    "This colour indicates between which two nodes the next one will be added. A tip is to visualise the polygon you want to create and go clockwise (or counter-clockwise)\n\n" +
                    "And of course, you can press a node, drag it to a new position, and drop it there. Now keep in mind, we are talking about the non-split mode. Keep in mind the orange square in the upper right corner.",
            "Look at the top right corner. You are now in the split mode. Things work a little bit differently here.\n\n" +
                    "Firstly, you can see a preview of where the nodes are going to be, in addition to their colour, which indicates the sub-polygon to which it will belong.\n\n" +
                    "Secondly, if you drag a node (only the border nodes can be dragged), you will see a red grid. This grid indicates where the splitlines " +
                    "for the polygons are!\nUse this information to your advantage.",
            "Now you have some options for your routes! Notice we have pre selected the start node of your route." +
                    "You can change this starting point by clicking on the node you want to start from and thus rearrange the route.\n\n" +
                    "Finally, when you feel ready and content with your routes, simply tap on the export button, and you are ready to fly out, map out!"
    };

    /**
     * count of how many pages the book has
     * @return viewpager length
     */
    @Override
    public int getCount() {
        return act_headers.length;
    }

    /**
     * Depending on the viewpager id the content of the page will be refreshed.
     * @param view
     * @param object
     * @return view
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.buch_slide_view,container,false);

        ImageView slideImage = (ImageView)view.findViewById(R.id.buch_act_image);
        TextView header = (TextView)view.findViewById(R.id.buch_act_header);
        TextView discr = (TextView)view.findViewById(R.id.buch_act_discription);

        slideImage.setImageResource(images_toslide[position]);
        header.setText(act_headers[position]);
        discr.setText(act_discr[position]);
        header.setTextColor(Color.BLACK);

        discr.setTextColor(Color.BLACK);



        container.addView(view);

        return view;
    }

    /**
     * If you leave the current page viewer the id gets changed and so the previous view has to be
     * destroyed and a new one has to be created.
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
