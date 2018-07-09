package com.example.nicol.dronflyvis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author Heiko
 * @author Hilmi
 * @author Artuk
 *
 * In this activity we are concernd with getting and validatating the user input. Some small calculations
 * based on the user input are needed.
 */
public class Settings_Activity extends AppCompatActivity
{
    private boolean allReady = false;
    private ArrayList<EditText> inputTextList = new ArrayList<>();
    private int droneFlag = -1;
    private Hashtable tableOfRatios;
    InputValidator generalInput;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Button aboutUs = (Button) findViewById(R.id.about_us_button);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AboutUs_PopUp_Activity.class);
                startActivity(intent);
            }
        });
        generalInput = new InputValidator(Settings_Activity.this);
        /**
         * Get all the values from the the edit texts
         * */
        EditText resWidthText = (EditText) findViewById(R.id.editText2);
        EditText resHeightText = (EditText) findViewById(R.id.editText5);

        EditText overlapWidthText = (EditText) findViewById(R.id.editText6);
        EditText overlapHeightText = (EditText) findViewById(R.id.editText7);

        EditText altitudeText = (EditText) findViewById(R.id.editText3);
        EditText fovText = (EditText) findViewById(R.id.editText4);
        EditText pixelSizeText = (EditText) findViewById(R.id.editText);

        inputTextList.add(resWidthText);
        inputTextList.add(resHeightText);
        inputTextList.add(overlapWidthText);
        inputTextList.add(overlapHeightText);
        inputTextList.add(fovText);
        inputTextList.add(altitudeText);
        inputTextList.add(pixelSizeText);

        tableOfRatios = setHashtable();

        /**
         * Start Input Validation
         */
        for(EditText text : inputTextList)
        {
            int currentId = text.getId();
            InputFilter filter = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                    if(charSequence.length() == 0 && i2 < i3) {
                        inputTextList.get(6).setText("");
                    }
                    return charSequence;
                }
            };
            text.setFilters(new InputFilter[]{
                    filter
            });

            text.addTextChangedListener(new CustomTextWatcher(text) {
                @Override
                public void validateText(EditText text) {
                    if(isEmpty(text)){return;}
                    if(!isEmpty(inputTextList.get(0)) && !isEmpty(inputTextList.get(1)) && !isEmpty(inputTextList.get(4)) && !isEmpty(inputTextList.get(5)))
                    {
                        inputTextList.get(6).removeTextChangedListener(this);
                        inputTextList.get(6).setText("" + calculatePixelSize(Float.parseFloat(inputTextList.get(5).getText().toString()),
                                Float.parseFloat(inputTextList.get(4).getText().toString()),
                                Float.parseFloat(inputTextList.get(0).getText().toString()),
                                Float.parseFloat(inputTextList.get(1).getText().toString())));
                        inputTextList.get(6).addTextChangedListener(this);
                    }
                    switch (currentId)
                    {

                        case R.id.editText3:
                            text.setTag(0);
                            InputValidator heightValidator = new InputValidator(1f, 100f, Settings_Activity.this);
                            text.setOnFocusChangeListener(heightValidator);
                            heightValidator.setCount(0);
                            break;
                        case R.id.editText4:
                            text.setTag(1);
                            InputValidator fovValidator = new InputValidator(1f, 170f, Settings_Activity.this);
                            text.setOnFocusChangeListener(fovValidator);
                            fovValidator.setCount(0);
                            break;
                        case R.id.editText6:
                        case R.id.editText7:
                            text.setTag(2);
                            InputValidator overlapValidator = new InputValidator(1f, 99f, Settings_Activity.this);
                            text.setOnFocusChangeListener(overlapValidator);
                            overlapValidator.setCount(0);
                            break;
                        case R.id.editText2:
                        case R.id.editText5:
                             text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                 @Override
                                 public void onFocusChange(View view, boolean b) {
                                     if(text.getText().toString().length()  < 3)
                                     {
                                         //Toast.makeText(Settings_Activity.this, "Hi", Toast.LENGTH_SHORT).show();

                                     }
                                     else if(text.getText().toString().length() > 4)
                                     {
                                         //Toast.makeText(Settings_Activity.this, "Hi", Toast.LENGTH_SHORT).show();
                                     }
                                     if(!isEmpty(inputTextList.get(0)) && !isEmpty(inputTextList.get(1)))
                                     {
                                         boolean decider = checkResolution(Float.parseFloat(inputTextList.get(0).getText().toString()),Float.parseFloat(inputTextList.get(0).getText().toString()));
                                         if(decider)
                                         {
                                             Toast.makeText(Settings_Activity.this, "Hi", Toast.LENGTH_SHORT).show();
                                         }
                                     }
                                 }
                             });
                            break;
                    }
                }

            });

        }
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        String flightHeight = inputTextList.get(5).getText().toString();
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                /**
                 * radio button for bepop
                 * */
                if(checkedId == R.id.radioButton4)
                {
                    droneFlag = 1;
                    clearText(inputTextList);
                    /**
                     * set the EditText to standard values
                     * */
                    setText(inputTextList,flightHeight, 3800,3188,70,85,170);
                }
                /**
                 * radio button for mavic
                 * */
                else if(checkedId == R.id.radioButton3)
                {
                    droneFlag = 0;
                    clearText(inputTextList);
                    /**
                     * set the EditText to standard values
                     * */
                    setText(inputTextList, flightHeight,4000,3000,70,85,78.8f);
                }
            }
        });
    }

    /**
     * UTILS
     * */
    public void clearText(ArrayList<EditText> texts)
    {
        for(EditText text : texts)
        {
            if(text.getId() != R.id.editText3)
            {
                text.setText("");
            }
        }
    }
    public boolean isEmpty(EditText text)
    {
        String textToCheck = text.getText().toString();
        if(textToCheck == null)
        {
            return true;
        }
        else if(textToCheck.matches(""))
        {
            return true;
        }
        return false;
    }
    public void setText(ArrayList<EditText> texts, String txt,float...values)
    {
        int i = 0;
        Log.i("test", "" + txt);
        for(EditText text : texts)
        {
            if(text.getId() != R.id.editText3)
            {
                if(i >= values.length)
                {
                    break;
                }
                if(Math.round(values[i]) == values[i])
                {
                    text.setText("" + Math.round(values[i]));
                }
                else
                {
                    text.setText("" + values[i]);
                }
                i++;
            }
        }

    }
    public boolean checkResolution(float resWidth, float resHeight)
    {
        float gcd = getGcd(resWidth, resHeight);
        int aspectRatioFirst = Math.round(resWidth/gcd);
        int aspectRationSecond = Math.round(resHeight/gcd);
        if(tableOfRatios.contains(aspectRatioFirst + ":" + aspectRationSecond))
        {
            return true;
        }
        return false;
    }
    public Hashtable setHashtable()
    {
        Hashtable<Integer, String> ratioTable = new Hashtable<Integer, String>();
        ratioTable.put(1, "1:1");
        ratioTable.put(2, "4:3");
        ratioTable.put(3, "3:2");
        ratioTable.put(6, "3:1");
        ratioTable.put(4, "5:3");
        ratioTable.put(5, "16:9");
        return ratioTable;
    }
    public float[] getInputValues()
    {
        int[] inputIds = {R.id.editText, R.id.editText3, R.id.editText4};
        int i = 0;

        float[] inputValues = new float[]{-1,-1,-1,-1,-1,-1,-1};
        for(int id : inputIds)
        {
            EditText inputText = (EditText) findViewById(id);
            inputValues[i] = Float.parseFloat("0" + inputText.getText().toString());
            i++;
        }
        return inputValues;
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
            else{
                txt.setError(null);
            }

        }
        return isEmpty;
    }
    public float[] getOverlap()
    {
        int[] inputIds = {R.id.editText6, R.id.editText7};
        int i = 0;
        float[] overlapValues = new float[]{-1,-1};
        for(int id : inputIds)
        {
            EditText inputText = (EditText) findViewById(id);
            overlapValues[i] = Float.parseFloat("0" + inputText.getText().toString());
            i++;
        }
        overlapValues[0] = (100 - overlapValues[0])/100;
        overlapValues[1] = (100 - overlapValues[1])/100;
        return overlapValues;
    }
    public float getAspectRatio()
    {
        int[] inputIds = {R.id.editText2, R.id.editText5};
        float[] aspectRatios = new float[]{-1,-1};
        float[] result = new float[]{-1,-1};
        int i = 0;
        for(int id : inputIds)
        {
            EditText inputText = (EditText) findViewById(id);
            aspectRatios[i] = Float.parseFloat("0" + inputText.getText().toString());
            i++;
        }
        result[0] = calcAspectRatios(aspectRatios[0], aspectRatios[1])[0];
        result[1] = calcAspectRatios(aspectRatios[0], aspectRatios[1])[1];
        return (result[1]/result[0]);
    }

    /**
     * NECESSARY MATHEMATICS
     **/
    public float getGcd(float first,float second)
    {
        if(second == 0)
        {
            return first;
        }
        else
        {
            return getGcd(second, first % second);
        }
    }
    public float calculateFov(float fotoWidth, float altitude)
    {
        float res = (float)(2 * Math.toDegrees((Math.atan(fotoWidth/(2 * altitude)))));
        if(res >= 170)
        {
            return 170f;
        }
        return res;
    }
    public float calculatePixelSize(float altitude, float fov, float resWidth, float resHeight)
    {
        float gcd = (float)getGcd((int) resWidth,(int) resHeight);
        float fotoWidth = (float) ((2.0 * altitude) * Math.tan(Math.toRadians((fov/2.0))));
        float fotoHeight = fotoWidth * ((resHeight/gcd)/(resWidth/gcd));
        return (float) (Math.sqrt(((fotoWidth/ resWidth) * 100) * ((fotoHeight/resHeight) * 100))); // meters times 100 gets us centimeters
    }
    public float calculateHeight(float fov, float pixelSize, float resWidth, float resHeight)
    {

        return 0;
    }
    public float[] calcAspectRatios(float resWidth, float resHeight)
    {
        float[] aspectRatio = new float[2];
        float gcd = getGcd(resWidth, resHeight);
        aspectRatio[0] = (resWidth/gcd);
        aspectRatio[1] = (resHeight/gcd);
        return aspectRatio;
    }

    /**
     * function handling click event on "next"
     **/
    public void settings_next(View view)
    {
        float[] inputValues = new float[3];
        float aspectRatio = 0;
        float[] overlap = new float[2];
        if(!inputEmpty(inputTextList))
        {
            inputValues = getInputValues();
            aspectRatio = getAspectRatio();
            overlap = getOverlap();
            allReady = true;
        }
        else
        {
            allReady = false;
        }
        if(allReady)
        {
            Intent intent = new Intent(getApplicationContext(), Main_Activity.class);
            intent.putExtra("com.example.nicol.dronflyvis.INPUT_VALUES", inputValues);
            intent.putExtra("com.example.nicol.dronflyvis.ASPECT_RATIO", aspectRatio);
            intent.putExtra("com.example.nicol.dronflyvis.OVERLAP", overlap);
            intent.putExtra("com.example.nicol.dronflyvis.RADIO_SELECTION", droneFlag);

            startActivity(intent);
        }
        else
        {
            Warning warning = new Warning("Fill in the empty fields before you continue.", "Please fill in missing values", true, "OK", this);
            android.app.AlertDialog alertDialog = warning.createWarning();
            alertDialog.setTitle("Missing Values");
            alertDialog.show();
            generalInput.createVibration();
        }
    }

}

/**if(!isEmpty(inputTexts.get(0)) && !isEmpty(inputTexts.get(1)) && !isEmpty(inputTexts.get(3)) && !isEmpty(inputTexts.get(4))) {
 txt.removeTextChangedListener(this);
 inputTexts.get(2).setText("" + calculatePixelSize((Double.parseDouble(inputTexts.get(0).getText().toString())),
 Double.parseDouble(inputTexts.get(1).getText().toString()),
 Double.parseDouble(inputTexts.get(3).getText().toString()),
 Double.parseDouble(inputTexts.get(4).getText().toString())));
 txt.addTextChangedListener(this);
 }**/

/**InputFilter filter = new InputFilter() {
@Override
public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
if(charSequence.length() == 0 && i2 < i3) {
inputTexts.get(2).setText("");
}
return charSequence;
}
};
 txt.setFilters(new InputFilter[]{
 filter
 });
 **/
/**
 RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
 radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
 {
 @Override
 public void onCheckedChanged(RadioGroup group, int checkedId)
 {
 RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
 switch(checkedId)
 {

 case R.id.radioButton4: fov.setText("" + 170);
 resText1.setText("3800");
 resText2.setText("3188");
 overlapV.setText("85");
 overlapH.setText("70");
 break;

 case R.id.radioButton3: fov.setText("" + 78.8);
 resText1.setText("4000");
 resText2.setText("3000");
 overlapV.setText("85");
 overlapH.setText("70");
 break;
 default: fov.setText("");
 resText1.setText("");
 resText2.setText("");
 break;
 }
 }
 });
 else
 {
 Warning warning = new Warning("Fill in the empty fields before you continue.", "Please fill in missing values", true, "OK", this);
 android.app.AlertDialog alertDialog = warning.createWarning();
 alertDialog.setTitle("Missing Values");
 alertDialog.show();
 }

 if(isEmpty(text)){return;}
 if(!isEmpty(inputTextList.get(0)) && !isEmpty(inputTextList.get(1)) && !isEmpty(inputTextList.get(4)))
 {
 //inputTextList.get(5).removeTextChangedListener(this);
 //inputTextList.get(5).setText("" + inputTextList.size());
 //inputTextList.get(5).addTextChangedListener(this);
 }

 if(!isEmpty(inputTextList.get(0)) && !isEmpty(inputTextList.get(1)) && !isEmpty(inputTextList.get(4)) && !isEmpty(inputTextList.get(5))) {
 inputTextList.get(6).removeTextChangedListener(this);
 inputTextList.get(2).setText("" + calculatePixelSize(Float.parseFloat(inputTextList.get(4).toString()), Float.parseFloat(inputTextList.get(5).toString()),
 Float.parseFloat(inputTextList.get(0).toString()), Float.parseFloat(inputTextList.get(1).toString())));
 inputTextList.get(6).addTextChangedListener(this);
 }

 **/