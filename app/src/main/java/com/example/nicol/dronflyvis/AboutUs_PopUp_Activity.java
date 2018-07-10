package com.example.nicol.dronflyvis;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.text.Html;
import android.widget.TextView;

/**
 * @author Hilmi
 * @author Artuk
 *
 * This class is concerned with creating and displaying the about us window. */
public class AboutUs_PopUp_Activity extends Activity {
    Button closeInfo;

    private final String htmlText = "<body><h1> <big><font color=\"black\"> Welcome to Drone Tours!</big> </font> </h1>" +
            "<h2> <font color=\"black\"> This application is OpenSource available and was developed in collaboration" +
            " with the Max Planck Institute for Ornithology in the context of the software project. </font> </h2>" + "<br>" +
            "<h2> <font color=\"black\"> The purpose of this app is the quick and easy generation" +
            " of efficient drone tours for the mapping of surfaces.</font> </h2>"+ "<br>" +
            "<h2> <font color=\"black\"> Familiarize yourself with the laws of the country, before you fly the drone.</font> </h2>" +
            "<h2> <font color=\"black\"> Any liability is excluded.</font> </h2>" + "<br>" +
            "<div style=\"text-align: center\">" +
            "<small> Heiko Dreyer </small> <br>" +
            "<small> Johannes Gleichauf </small> <br>" +
            " <small> Artuk Kakhorov </small> <br>" +
            "<small> Martin Olles </small> <br>" +
            " <small> Nicolas-Andreas Tamm-Garetto </small> <br>" +
            " <small>  Hilmi-Can Yumak </small> </div>" + "</body>";


    /**
     * This method is creating the activity and shows the text inside it.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_pop_up_activity);
        closeInfo = (Button) findViewById(R.id.closeInfo1Button);

        closeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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


        TextView htmlTextView = (TextView) findViewById(R.id.html_text);
        htmlTextView.setText(Html.fromHtml(htmlText, 1));
    }
}
