package com.example.nicol.dronflyvis;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Artuk
 * @author Hilmi
 *
 * This class is preoccupide with creating and displaying the window for the book and set
 * dots to every viewpage.
 */
public class Buch_PopUp_Activity extends Activity {

    private ViewPager mSlideViewPager;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private Buch_Slider buchSlider;
    private Button close;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buch_activity);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.9));

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.x = 0;
        lp.y = -20;

        getWindow().setAttributes(lp);



        mSlideViewPager = (ViewPager)findViewById(R.id.slideViewPager);
        dotsLayout=(LinearLayout) findViewById(R.id.slideDots);

        buchSlider = new Buch_Slider(this);
        mSlideViewPager.setAdapter(buchSlider);


        close = (Button)findViewById(R.id.close_buch_act);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addDots(0);
        mSlideViewPager.addOnPageChangeListener(dotsChangeListener);
    }

    /**
     * Draws the dots below the text to show on which page the user is located and the current dot
     * will be shown in different color.
     * @param pos
     */
    @SuppressWarnings("deprecation")
    public void addDots(int pos){

        dots = new TextView[5];
        dotsLayout.removeAllViews();

        for(int i= 0; i<dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&middot;"));
            dots[i].setTextSize(80);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimary));

            dotsLayout.addView(dots[i]);
        }
        if(dots.length>0){
            dots[pos].setTextColor(Color.BLACK);
        }
    }

    ViewPager.OnPageChangeListener dotsChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            /**
             * Dont't care
             */
        }

        /**
         * set dots
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            addDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            /**
             * Dont't care
             */
        }
    };
}
