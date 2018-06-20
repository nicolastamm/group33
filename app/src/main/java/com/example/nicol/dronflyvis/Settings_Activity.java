package com.example.nicol.dronflyvis;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;


public class Settings_Activity extends AppCompatActivity
{
    private Boolean inputOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        RadioButton bepob = (RadioButton) findViewById(R.id.radioButton4);
        RadioButton mavic = (RadioButton) findViewById(R.id.radioButton3);
        Button nextBtn = (Button) findViewById(R.id.settings_next_button);
        Button aboutUs = (Button) findViewById(R.id.about_us_button);


        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AboutUs_PopUp_Activity.class);
                startActivity(intent);
            }
        });



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
                            Warning altitudeWarning = new Warning("Altitude is larger than 100 meters, are you sure you want to continue?", "Altitude too large", true, "OK", Settings_Activity.this);
                            AlertDialog alertDialog = altitudeWarning.createWarning();
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

    }

    public float[] getInputValues()
    {
        int[] inputIds = {R.id.editText, R.id.editText3, R.id.editText4};
        int i = 0;
        float[] inputValues = new float[]{-1,-1,-1};
        for(int id : inputIds)
        {
            EditText inputText = (EditText) findViewById(id);
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

    public void settings_next(View view) {
        float invalidInput = -1.0f;
        float[] inputValues = getInputValues();
        if(!contains(inputValues, invalidInput))
        {
            inputOk = true;
        }

        if(inputOk) {
            Intent intent = new Intent(this, Main_Activity.class);
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

