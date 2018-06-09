package com.example.nicol.dronflyvis;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class buch_act extends Activity {
    private ViewPager mSlideViewPager;
    private buch_slider buchSlider;
    private Button close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buch_act);

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
        buchSlider = new buch_slider(this);
        mSlideViewPager.setAdapter(buchSlider);

        close = (Button)findViewById(R.id.close_buch_act);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
