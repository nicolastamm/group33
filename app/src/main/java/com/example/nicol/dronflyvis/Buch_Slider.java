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
            "The split modus",
            "Ready to export"
    };

    public String[] act_discr = {
            "While editing your polygon, we made several modi and buttons to greatly streamline your experience with our system.\n\nThese include:\n\n" +
                    "(1) Pin mode: sometimes you want to focus on a certain area of the map, without having to be mindful of not swiping away. With this enabled, the map shan't move, thus allowing better control of the editing tools.\n\n" +
                    "(2) Draw mode: By default, you start on this mode. As the name implies, it is in this mode that new points of your desired area can be added. \n\n" +
                    "(3) Erase mode: Counter to the draw mode, this mode enables the selective removal of any nodes of your choosing.\n\n" +
                    "(4) Dump Polygon: For those big mistakes. Press this, and you will allow yourself to start again from scratch.\n\n" +
                    "(5) Import Polygon: Old work shan't be simply thrown away! Now you can import your favourite polygons!",
            "There some other icons that have to be explained. On the top border you see a very familiar sight. It is indeed the search bar of Google Maps! Use it just as you know it!\n\n" +
                    "In the upper right corner of it you might see an orange square. This tells you whether you are in a Polygon splitting modus or not. Tapping it toggles the switch mode.\n\n" +
                    "Below are some self explanatory buttons. Of note is perhaps the middle one. This one simply lets you switch between different map types.\n\n" +
                    "Up to the right of this is the help button.\nYou know this one, you just pressed it!",
            "There are several things to note when drawing polygons with our software. You will notice that two nodes are always coloured orange.\n\n" +
                    "This color denotes between which 2 nodes the next one will be added. Best practice is to visualise the polygon you wish to draw, and go clockwise, or counter-clockwise through that border.\n\n" +
                    "And of course, you can press a node, drag it to a new position, and drop it there! Now keep in mind we are talking about the non-split mode here, notice the orange square in the upper right corner.",
            "Look at the top right corner. You are now in the split modus! Things work a little bit differently now.\n\n" +
                    "First of, you now see a preview of where the points will be, in addition to their colour, which denotes the sub-polygon to which it will belong.\n\n" +
                    "Secondly, if you drag a point (only the border points can be dragged), you will see some red grid. This grid lets you know where the splitlines " +
                    "for the polgons are!\nUse this information to your advantage.",
            "Now you have some candidates for your routes! Notice we have pre selected for you the start of your route. " +
                    "But if course you know best where to start your own route. This is why with a simple tap, you can rearrange the route with a start node of your liking!\n\n" +
                    "Finally, when you feel ready and satisfied with your routes, simply tap on the Export button, and you are ready to fly out, map out!"
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
