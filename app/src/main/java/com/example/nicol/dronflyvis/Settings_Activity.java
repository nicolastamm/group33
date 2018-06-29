package com.example.nicol.dronflyvis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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
        //String res = MySpinner.getSelectedItem().toString();

        inputTexts.add(altitude);
        inputTexts.add(fov);
        inputTexts.add(pixelSize);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked)
                {
                    fov.setText("" + checkedId);
                }
                Log.i("test", "" + checkedId);
                switch(checkedId)
                {
                    case 2131165321: fov.setText("" + 180); //checkedID for bepob
                    break;
                    case 2131165320: fov.setText("" + 78.8); //checkedId for mavic
                    break;
                    default: fov.setText("");
                    break;
                }
            }
        });
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
                            Warning altitudeWarning = new Warning("Altitude is larger than 100 meters, are you sure you want to continue?", "Altitude too large", true, "Yes","No", Settings_Activity.this);
                            AlertDialog alertDialog = altitudeWarning.createWarning();
                            alertDialog.setTitle("Altitude larger than 100 meters");
                            alertDialog.show();
                            Button negativeBtn = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            negativeBtn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    txt.setText("");
                                }
                            });
                        }
                    }
                    /**if(txt == inputTexts.get(1) && !(charSequence.toString().equals("")) && !(charSequence.toString().equals(".")))
                    {
                        if(Double.parseDouble(charSequence.toString()) > 175)
                        {
                            Warning altitudeWarning = new Warning("FOV bigger than 175 degrees not supported", "FOV too large", true, "Ok", Settings_Activity.this);
                            AlertDialog alertDialog = altitudeWarning.createWarning();
                            alertDialog.setTitle("FOV larger than 175 degrees");
                            alertDialog.show();
                            Button negativeBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            negativeBtn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    txt.setText("175");
                                }
                            });
                        }
                    }**/
                }
                @Override
                public void afterTextChanged(Editable editable)
                {
                    if(txt == inputTexts.get(0))
                    {
                        if (editable.toString().equals(".")) {
                            txt.setText("0" + editable.toString());
                        }
                    }
                    if(inputTexts.get(0).getText().toString().trim().length() > 0 && inputTexts.get(1).getText().toString().trim().length() > 0)
                    {
                            inputTexts.get(2).removeTextChangedListener(this);
                            calculatePixelSize(inputTexts.get(0), inputTexts.get(1), inputTexts.get(2));
                            inputTexts.get(2).addTextChangedListener(this);
                    }
                }
            });
        }

    }
    private void calculatePixelSize(EditText editText1,EditText editText2, EditText resultText)
    {
        Editable altitudeEdit = editText1.getText();
        Editable fovEdit = editText2.getText();

        double altitude= Double.parseDouble(altitudeEdit.toString());
        double fov= Double.parseDouble(fovEdit.toString());
        double fotoWidth = 2* altitude * Math.tan(Math.toRadians(fov/2.0));
        double fotoHeight = fotoWidth * (3.0/4.0);

        double pixelSize = Math.sqrt(((fotoWidth/ 4000) * 100) * ((fotoHeight/3000) * 100));
        resultText.setText(pixelSize + "");
    }

    private void calculateFov(EditText editText1,EditText editText2, EditText resultText)
    {
        Editable altitudeEdit = editText1.getText();
        Editable pixelSizeEdit = editText2.getText();

        double altitude= Double.parseDouble(altitudeEdit.toString());
        double pixelSize = Double.parseDouble(pixelSizeEdit.toString());
    }
    public float[] getInputValues()
    {
        int[] inputIds = {R.id.editText, R.id.editText3, R.id.editText4};
        int i = 0;
        float[] inputValues = new float[]{-1,-1,-1};
        //getResInput();
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
    /**
    public String[] getResInput()
    {
        Spinner resSpinner = (Spinner) findViewById(R.id.spinner);
        String[] selection = new String[2];
        float[] result = new float[2];
        if(!resSpinner.getSelectedItem().toString().contains("C"))
        {
            selection = resSpinner.getSelectedItem().toString().split(" ");
        }
        return selection;
    }
     */
    //get input from radio buttons
    public int getRadioButton()
    {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        if(radioButtonId != -1)
        {
            RadioButton selectedButton = (RadioButton) radioGroup.findViewById(radioButtonId);
            String selected = (String) selectedButton.getText();
            if(selected == "Dji Mavic Pro")
            {
                return 0;
            }
            return 1;
        }
        return -1;
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
       // String[] selected = getResInput();

        Log.i("test", "" + inputValues[1]);
        if(!contains(inputValues, invalidInput))// && selected != null)
        {
            inputOk = true;
        }
        if(inputOk) {
            Intent intent = new Intent(this, Main_Activity.class);
            intent.putExtra("com.example.nicol.dronflyvis.INPUT_VALUES", getInputValues());
            intent.putExtra("com.example.nicol.dronflyvis.RADIO_SELECTION", getRadioButton());
            //Log.i("test" , "" + inputValues[0]);
            //Log.i("test" , "" + inputValues[1]);
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

