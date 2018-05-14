package com.example.nicol.dronflyvis;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class startAct extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        RunAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent startIntent = new Intent(startAct.this,act1.class);
                startActivity(startIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

    public void RunAnimation()
    {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.startanim);
        TextView tv = (TextView) findViewById(R.id.startText);
        tv.startAnimation(a);
    }
}
