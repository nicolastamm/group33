package com.example.nicol.dronflyvis;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * @author Hilmi
 *
 * When the app is started a startactivity will be shown.
 * While the splashtime has not yet elapsed the logo, our slogan and a loading indicator will be shown.
 */
public class Start_Activity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent startIntent = new Intent(Start_Activity.this,Settings_Activity.class);
                startActivity(startIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
