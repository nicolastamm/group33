package com.example.nicol.dronflyvis;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class act1 extends AppCompatActivity
{
    private Button nextBtn;
    private EditText inputText;
    private Boolean inputOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act1);

        nextBtn = (Button) findViewById(R.id.act1next);
        //InfoButtonPopUp
        //bepobFlag
        final ArrayList<EditText> inputTexts = new ArrayList<>();

        EditText altitude = (EditText) findViewById(R.id.editText3);
        EditText fov = (EditText) findViewById(R.id.editText4);
        EditText pixelSize = (EditText) findViewById(R.id.editText);

        inputTexts.add(altitude);
        inputTexts.add(fov);
        inputTexts.add(pixelSize);

        for(final EditText txt : inputTexts)
        {
            txt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                    if(txt == inputTexts.get(0) && !(charSequence.toString().equals("")) && !(charSequence.toString().equals(".")))
                    {
                        if(Double.parseDouble(charSequence.toString()) > 100)
                        {
                            Warning altitudeWarning = new Warning("Altitude is larger than 100 meters, are you sure you want to continue?", "Altitude too large", true, "OK", act1.this);
                            android.app.AlertDialog alertDialog = altitudeWarning.createWarning();
                            alertDialog.setTitle("Altitude larger than 100 meters");
                            alertDialog.show();

                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable editable)
                {
                    if(txt == inputTexts.get(0))
                    {
                        if (editable.toString().equals("."))
                        {
                            txt.setText("0" + editable.toString());
                        }
                    }

                }
            });
        }

        Button infoButton = (Button) findViewById(R.id.act1info);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),infoPopUpAct.class);
                startActivity(intent);
            }
        });

        String[] cr = {"Choose your camera resolution","1280x960 (4:3)"," 1280x720 (16:9)", "1280x853.33 (3:2)","1280x548.57 (21:9)"};

        Spinner MySpinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> myAdapter = new
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cr);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MySpinner.setAdapter(myAdapter);

    }
    public double getFov(float fotoWidth, double height)
    {
        return 2 * Math.toDegrees((Math.atan(fotoWidth/(2*height))));
    }
    public double getPixelSize(float flightHeight, float fov, float imageRatio)
    {
        double fotoWidth = (2.0 * flightHeight) * (Math.tan(Math.toRadians(fov/2.0)));
        double fotoHeight = fotoWidth * (3.0/4.0);

        return (fotoWidth*fotoHeight)/(imageRatio);
    }
    public float[] getInputValues()
    {
        int[] inputIds = {R.id.editText, R.id.editText3, R.id.editText4};
        int i = 0;
        float[] inputValues = new float[]{-1,-1,-1};
        for(int id : inputIds)
        {
            inputText = (EditText) findViewById(id);
            if(isEmpty(inputText))
            {
                inputText.setError("missing input");
            }
            else
            {
                inputValues[i] = Float.parseFloat("0" + inputText.getText().toString());
                i++;
            }

        }

        return inputValues;
    }

    public boolean isEmpty(EditText text)
    {
        if(TextUtils.isEmpty(text.getText().toString()))
        {
            return true;
        }
        return false;
    }

    public boolean contains(float[] array, float value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return true;
            }
        }
        return false;
    }



    /**
     Der Skelet für Alelrt !
     Also Jungs, hier sit schon mal unser erster Allert.
     Dafür habe ich oben eine neue Variable hinzugefügt (inputOk).

     Wenn der Wert von dieser gleich False ist, dann kommt der Alert mit dem anderen Button zum wegclicken.
     Ist der Input ok, dann soll auch alles wie wir es hatten ablaufen.

     also To_Do hier :

     Input-man erstellt sich jetzt die neue Funktion(mit den alten, die der schon hatte) mit mehreren Abfragen in den Feldern.
     War alles ok? dann wird auch der inputOk auf "true" gesetzt und NUR dann.
     Und ja, um die Funktion überhaupt zu starten muss man erst auf next drücken, also ruf die einfach in act_1_next auf;

     Design-man kann sich jetzt gedanken machen über das Aussehen von Alert.
     Solche Sachen wie Bilder hinzufügen, Messege als Source speichern, farben und und und :)



     LG
     Android Integrator

     */

    public void act_1_next(View view)
    {
        // Also hier kann man die Überprüfungsfunktion Starten;
        // Also OBACHT hier habe ich manuel ein Mal allert Ausgegeben, beim erneut
        // drücken wird die Akt 2 gestartet(ich ersetze inputOK manuel)

        float invalidInput = -1.0f;
        float[] inputValues = getInputValues();
        if(!contains(inputValues, invalidInput))
        {
            inputOk = true;
        }

        if(inputOk) {
            Intent intent = new Intent(this, act2.class);
            intent.putExtra("com.example.nicol.dronflyvis.INPUT_VALUES", getInputValues());
            startActivity(intent);
        }
        else
            {

                Warning warning = new Warning("Fill in the empty fields before you continue.", "Please fill in missing values", true, "OK", this);
                android.app.AlertDialog alertDialog = warning.createWarning();
                alertDialog.setTitle("Missing Values");
                alertDialog.show();
            }
    }





}

