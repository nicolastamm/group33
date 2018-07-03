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
    final ArrayList<EditText> inputTexts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Button nextBtn = (Button) findViewById(R.id.settings_next_button);
        Button aboutUs = (Button) findViewById(R.id.about_us_button);

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AboutUs_PopUp_Activity.class);
                startActivity(intent);
            }
        });

        EditText resText1 = (EditText) findViewById(R.id.editText2);
        EditText resText2 = (EditText) findViewById(R.id.editText5);

        EditText altitude = (EditText) findViewById(R.id.editText3);
        EditText fov = (EditText) findViewById(R.id.editText4);
        EditText pixelSize = (EditText) findViewById(R.id.editText);
        //pixelSize.setFocusable(false);

        EditText overlapH = (EditText) findViewById(R.id.editText6);
        EditText overlapV = (EditText) findViewById(R.id.editText7);

        inputTexts.add(altitude);
        inputTexts.add(fov);
        inputTexts.add(pixelSize);
        inputTexts.add(resText1);
        inputTexts.add(resText2);
        inputTexts.add(overlapH);
        inputTexts.add(overlapV);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                switch(checkedId)
                {
                    case R.id.radioButton4: fov.setText("" + 170); //checkedID for bepob
                        break;
                    case R.id.radioButton3: fov.setText("" + 78.8); //checkedId for mavic
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

                }
                @Override
                public void afterTextChanged(Editable editable)
                {
                    if(isEmpty(txt)){return;}
                    if(!isEmpty(inputTexts.get(0)) && !isEmpty(inputTexts.get(1)) && !isEmpty(inputTexts.get(3)) && !isEmpty(inputTexts.get(4))) {
                        txt.removeTextChangedListener(this);
                        inputTexts.get(2).setText("" + calculatePixelSize((Double.parseDouble(inputTexts.get(0).getText().toString())),
                                Double.parseDouble(inputTexts.get(1).getText().toString()),
                                Double.parseDouble(inputTexts.get(3).getText().toString()),
                                Double.parseDouble(inputTexts.get(4).getText().toString())));
                        txt.addTextChangedListener(this);
                    }
                    switch (txt.getId())
                    {
                        case R.id.editText2:
                        case R.id.editText5:
                            if(txt.getEditableText().toString().length() > 4)
                            {
                                Warning altitudeWarning = new Warning("Resolution not supported", "Resolution too large", true, "Ok", Settings_Activity.this);
                                AlertDialog alertDialog = altitudeWarning.createWarning();
                                alertDialog.setTitle("Resolution too large!");
                                alertDialog.show();
                                txt.removeTextChangedListener(this);
                                txt.setText("");
                                txt.addTextChangedListener(this);
                            }
                            break;
                        case R.id.editText6:
                        case R.id.editText7:
                            if(Double.parseDouble(txt.getEditableText().toString()) > 100)
                            {
                                Warning altitudeWarning = new Warning("Overlap can't be larger than 100%", "Overlap to large", true, "Ok", Settings_Activity.this);
                                AlertDialog alertDialog = altitudeWarning.createWarning();
                                alertDialog.setTitle("Overlap not supported");
                                alertDialog.show();
                                txt.removeTextChangedListener(this);
                                txt.setText("");
                                txt.addTextChangedListener(this);
                            }
                            break;
                        case R.id.editText3:
                            if(validateAlt(Double.parseDouble(txt.getEditableText().toString())))
                            {
                                Warning altitudeWarning = new Warning("Altitude is larger than 100 meters, are you sure you want to continue?", "Altitude too large", true, "Yes","No", Settings_Activity.this);
                                AlertDialog alertDialog = altitudeWarning.createWarning();
                                alertDialog.setTitle("Altitude larger than 100 meters");
                                alertDialog.show();
                                Button negativeBtn = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                negativeBtn.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        txt.setText(""); }
                                });
                            }
                            break;
                        case R.id.editText4:
                            if(txt.getEditableText().toString().length() >= 3)
                            {
                                if(Double.parseDouble(txt.getEditableText().toString()) >= 180 ||Double.parseDouble(txt.getEditableText().toString()) <= 40)
                                {
                                    Warning altitudeWarning = new Warning("FOV larger or equal to 180 or less than 40 degrees are not supported", "FOV not supported", true, "Ok", Settings_Activity.this);
                                    AlertDialog alertDialog = altitudeWarning.createWarning();
                                    alertDialog.setTitle("FOV not supported");
                                    alertDialog.show();
                                    txt.removeTextChangedListener(this);
                                    txt.setText("");
                                    txt.addTextChangedListener(this);
                                }

                            }
                            break;
                    }

                }
            });
        }
    }
    public boolean validateAlt(double altitude)
    {
        if(altitude != 0 & altitude > 100)
        {
            return true;
        }
        return false;
    }
    public boolean validateRes(double res)
    {
        int count = 0;
        while(res >= 0)
        {
            res /= 10;
            count++;
        }
        if(count < 2 || count > 4)
        {
            return false;
        }
        return true;
    }
    public double calculatePixelSize(double altitude, double fov, double pixelWidth, double pixelHeight)
    {
        double gcd = getGcd(pixelWidth, pixelHeight);
        double fotoWidth = 2 * altitude * Math.atan(Math.toRadians(fov/2.0));
        double fotoHeight = fotoWidth * ((pixelHeight/gcd)/(pixelWidth/gcd));
        return Math.sqrt(((fotoWidth/ pixelWidth) * 100) * ((fotoHeight/pixelHeight) * 100));
    }

    public double getGcd(double a,double b)
    {
        if(b == 0)
        {
            return a;
        }
        else
        {
            return getGcd(b, a % b);
        }
    }
    public void calculateFov(EditText editText1,EditText editText2, EditText resultText)
    {
        Editable altitudeEdit = editText1.getText();
        Editable pixelSizeEdit = editText2.getText();

        double altitude= Double.parseDouble(altitudeEdit.toString());
        double pixelSize = Double.parseDouble(pixelSizeEdit.toString());
    }
    public float[] getInputValues()
    {
        int[] inputIds = {R.id.editText, R.id.editText3, R.id.editText4, R.id.editText6, R.id.editText7, R.id.editText2, R.id.editText5};
        int i = 0;
        float[] inputValues = new float[]{-1,-1,-1,-1,-1,-1,-1};
        //getResInput();
        for(int id : inputIds)
        {
            EditText inputText = (EditText) findViewById(id);
            inputValues[i] = Float.parseFloat("0" + inputText.getText().toString());
            i++;
        }
        return inputValues;
    }
    public float[] getAspectRatio(double pixelWidth, double pixelHeight)
    {
        float[] aspectRatio = new float[2];
        double gcd = getGcd(pixelHeight, pixelWidth);
        aspectRatio[0] = (float) (pixelHeight /gcd);
        aspectRatio[1] = (float) (pixelWidth/ gcd);
        return aspectRatio;

    }
    public boolean inputEmpty(ArrayList<EditText> texts)
    {
        boolean isEmpty = false;
        for(EditText txt : texts)
        {
            if(isEmpty(txt))
            {
                isEmpty = true;
                txt.setError("missing input");
            }

        }
        return isEmpty;
    }

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
        float[] inputValues;
        float[] aspectRatio = new float[2];
        if(!inputEmpty(inputTexts))
        {
            inputValues = getInputValues();
            if(!contains(inputValues, invalidInput))
            {
                //Log.i("test", "" + getAspectRatio(inputValues[3], inputValues[4])[1]);
                Log.i("test", "" +inputValues[5]);
                aspectRatio = getAspectRatio(inputValues[5], inputValues[6]);
                inputOk = true;
            }
        }
        //inputValues[0] = pixelSize
        //inputValues[1] = altitude
        //inputValues[2] = fov
        //inputValues[3] = pixelWidth
        //inputValues[4] = pixelHeight
        //inputValues[5] = horizontal overlap
        //inputValues[6] = vertical overlap
        //aspectRatio[0] = 3.0
        if(inputOk) {
            Intent intent = new Intent(this, Main_Activity.class);
            intent.putExtra("com.example.nicol.dronflyvis.INPUT_VALUES", getInputValues());
            intent.putExtra("com.example.nicol.dronflyvis.RADIO_SELECTION", getRadioButton());
            intent.putExtra("com.example.nicol.dronflyvis.ASPECT_RATIO", aspectRatio);
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

/**if(txt == inputTexts.get(0) && !(editable.toString().equals("")))
 {
 if (editable.toString().equals(".")) {
 txt.setText("0" + editable.toString());
 }
 else if(Double.parseDouble(editable.toString()) > 100)
 {
 Warning altitudeWarning = new Warning("Altitude is larger than 100 meters, are you sure you want to continue?", "Altitude too large", true, "Yes","No", Settings_Activity.this);
 AlertDialog alertDialog = altitudeWarning.createWarning();
 alertDialog.setTitle("Altitude larger than 100 meters");
 alertDialog.show();
 Button negativeBtn = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
 negativeBtn.setOnClickListener(new View.OnClickListener() {
 public void onClick(View v) {
 alertDialog.dismiss();
 txt.setText(""); }
 });
 }
 }
 if(txt == inputTexts.get(1) && !(charSequence.toString().equals("")) && !(charSequence.toString().equals(".")))
 {
 if(Double.parseDouble(charSequence.toString()) > 175 || Double.parseDouble(charSequence.toString()) < 45 )
 {
 Warning altitudeWarning = new Warning("FOV of " + charSequence.toString() + " not supported", "FOV not supported", true, "Ok", Settings_Activity.this);
 AlertDialog alertDialog = altitudeWarning.createWarning();
 alertDialog.setTitle("FOV not supported");
 alertDialog.show();
 Button negativeBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
 negativeBtn.setOnClickListener(new View.OnClickListener() {
 public void onClick(View v) {
 alertDialog.dismiss();
 txt.setText("175");

 if(inputTexts.get(0).getText().toString().trim().length() > 0 && inputTexts.get(1).getText().toString().trim().length() > 0)
 {
 inputTexts.get(2).removeTextChangedListener(this);
 calculatePixelSize(inputTexts.get(0), inputTexts.get(1), inputTexts.get(3), inputTexts.get(4),inputTexts.get(2));
 inputTexts.get(2).addTextChangedListener(this);
 }
 }
 });
 }
 }
 if(txt == inputTexts.get(0) && !(editable.toString().equals(""))) {
 if (editable.toString().equals("."))
 {
 txt.setText("0" + editable.toString());
 }
 }
 else if(isEmpty(inputTexts.get(0)) || isEmpty(inputTexts.get(1)) || isEmpty(inputTexts.get(3)) || isEmpty(inputTexts.get(4)))
 {
 inputTexts.get(2).removeTextChangedListener(this);
 inputTexts.get(2).setText("");
 inputTexts.get(2).addTextChangedListener(this);
 }
 **/
/**public void calculatePixelSize(EditText editText1,EditText editText2, EditText editText3, EditText editText4,EditText resultText)
 {
 Editable altitudeEdit = editText1.getText();
 Editable fovEdit = editText2.getText();
 //Editable firstRes = editText3.getText();
 //Editable secondRes = editText4.getText();

 double altitude= Double.parseDouble(altitudeEdit.toString());
 double fov= Double.parseDouble(fovEdit.toString());
 //double first = Double.parseDouble(firstRes.toString());
 //double second = Double.parseDouble(secondRes.toString());
 //double arFirst = first / getGcd(first, second);
 //double arSecond = first / getGcd(first, second);
 double fotoWidth = 2* altitude * Math.tan(Math.toRadians(fov/2.0));
 double fotoHeight = fotoWidth * (3.0/4.0);

 double pixelSize = Math.sqrt(((fotoWidth/ 4000) * 100) * ((fotoHeight/3000) * 100));
 resultText.setText(pixelSize + "");
 }**/