package com.example.nicol.dronflyvis;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class act1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act1);


    }

    public void act_1_next(View view){

            Intent Intent = new Intent(this, act2.class);
            startActivity(Intent);
        }
    }

