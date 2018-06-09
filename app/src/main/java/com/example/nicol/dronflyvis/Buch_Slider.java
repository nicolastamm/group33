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

public class Buch_Slider extends PagerAdapter{
    Context context;
    LayoutInflater layoutInflater;

    public Buch_Slider(Context context){
        this.context = context;
    }

    public int[] images_toslide = {
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher
    };

    public String[] act_headers = {
            "Location selection",
            "Starting point",
            "Draw polygon",
            "Export"
    };


    public String[] act_discr = {
            "In this section you can search for an arbitrary location which you may want to map out later on. "
                    + "\n \nBelow you've got a change-button for changing the map type between a satellite, hybrid, normal and terrain aspect."
                    + "You can confirm the chosen location by clicking next.",
            "Here, in this section you can set your starting point from where you want to position yourself in the course of mapping out your chosen location."
                    + "\n \nIf you set your position, you can see a 300 meter radius around the marker which is meant for your orientation.",
            "On the left side of the Screen you can see four buttons. The button with the pin lets you pin down the map" +
                    " so you won't have the trouble of sudden movements while you're drawing.\nThe pencil button lets you set the corners of your polygon. The garbage can" +
                    " delets all nodes and with the eraser you have to possibility of deleting one arbitrary node.",
            "After some misterious calculations you can now see the route for your drone. If what you see isn't what you wanted you can go back and start again from the last section." +
                    "\n\nIf you're satisfied you can now export the csv file for litchi online."
    };


    @Override
    public int getCount() {
        return act_headers.length;
    }

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
        header.setTextSize(25);
        discr.setTextColor(Color.BLACK);
        discr.setTextSize(16);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
