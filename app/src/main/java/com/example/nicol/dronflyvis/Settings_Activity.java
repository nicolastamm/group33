package com.example.nicol.dronflyvis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Heiko
 * @author Hilmi
 * @author Artuk
 *
 * In this activity we are concernd with getting and validatating the user input. Some small calculations
 * based on the user input are needed.
 *
 */
public class Settings_Activity extends AppCompatActivity
{
    private boolean allReady = false;
    private ArrayList<EditText> inputTextList = new ArrayList<>();
    private int droneFlag = -1;
    private Hashtable tableOfRatios;
    private InputValidator generalInput;
    private InputValidator heightValidator;
    private InputValidator fovValidator;
    private InputValidator overlapValidator;
    private InputValidator pixelValidator;
    private InputValidator resWidthValidator;
    private InputValidator resHeightValidator;

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
        resWidthValidator = new InputValidator(Settings_Activity.this);
        resHeightValidator = new InputValidator(Settings_Activity.this);
        heightValidator =  new InputValidator(10f, 100f, Settings_Activity.this);
        fovValidator = new InputValidator(10f, 170f, Settings_Activity.this);
        overlapValidator = new InputValidator(1, 99, Settings_Activity.this);
        pixelValidator = new InputValidator(0.3f, 10f,Settings_Activity.this);

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

        /**
         * Adding all our EditTexts to the Array List
         * */
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
            /**
             * We add a onKeyListener to check whether certain texts have been filled out already and then we can calculate other text fields based on that
             * */
            text.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if(!isEmpty(inputTextList.get(0)) && !isEmpty(inputTextList.get(1)))
                    {

                        if(!isEmpty(inputTextList.get(4)) && !isEmpty(inputTextList.get(5)) && !inputTextList.get(6).hasFocus())
                        {

                                inputTextList.get(6).setText("" + calculatePixelSize(Float.parseFloat(inputTextList.get(5).getText().toString()),
                                        Float.parseFloat(inputTextList.get(4).getText().toString()),
                                        Float.parseFloat(inputTextList.get(0).getText().toString()),
                                        Float.parseFloat(inputTextList.get(1).getText().toString())));
                        }
                        else if(!isEmpty(inputTextList.get(4)) && !isEmpty(inputTextList.get(6)) && !inputTextList.get(5).hasFocus())
                        {
                            inputTextList.get(5).setText("" + calculateHeight(Float.parseFloat(inputTextList.get(4).getText().toString()),
                                    Float.parseFloat(inputTextList.get(1).getText().toString()),
                                    Float.parseFloat(inputTextList.get(0).getText().toString()),
                                    Float.parseFloat(inputTextList.get(6).getText().toString())));
                        }
                    }
                    if(isEmpty(inputTextList.get(0)) || isEmpty(inputTextList.get(1)) || isEmpty(inputTextList.get(5)) || isEmpty(inputTextList.get(4)))
                    {
                        inputTextList.get(6).setText("");
                    }
                    else if(isEmpty(inputTextList.get(0)) || isEmpty(inputTextList.get(1)) || isEmpty(inputTextList.get(6)) || isEmpty(inputTextList.get(4)))
                    {
                        inputTextList.get(5).setText("");
                    }
                    return false;
                    }

            });
            /**
             * Here we start the real validation
             * */
            text.addTextChangedListener(new CustomTextWatcher(text) {
                /**
                 * Set a tag to the text field, an onFocusListener and all the possible errors to null. Let InputValidator do the rest
                 * */
                @Override
                public void validateText(EditText text) {
                    if(isEmpty(text)){return;}
                    switch (currentId)
                    {
                        case R.id.editText3:
                            text.setTag(0);
                            text.setOnFocusChangeListener(heightValidator);
                            text.setError(null);
                            heightValidator.setCount(0);
                            break;
                        case R.id.editText4:
                            text.setTag(1);
                            text.setOnFocusChangeListener(fovValidator);
                            text.setError(null);
                            fovValidator.setCount(0);
                            break;
                        case R.id.editText6:
                        case R.id.editText7:
                            text.setTag(2);
                            text.setOnFocusChangeListener(overlapValidator);
                            overlapValidator.setCount(0);
                            text.setError(null);
                            break;
                        case R.id.editText:
                            text.setTag(3);
                            text.setError(null);
                            text.setOnFocusChangeListener(pixelValidator);
                            break;
                        /**
                         *
                         * The Fields for Resolution have certain behavior which has to be captured like this
                         */
                        case R.id.editText2:
                            text.setError(null);
                            text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View view, boolean b) {

                                    if(text.getText().toString().length()  > 4 && !isEmpty(text) && resWidthValidator.getCount() < 1)
                                    {
                                        resWidthValidator.setValid(false);
                                        resWidthValidator.setCount(1);
                                        resWidthValidator.createWarning("Resolution Width too large! The Resolution Width should be smaller than or equal to 4 Digits", "Resolution Width too large",
                                                "Ok", null,Settings_Activity.this, "Resolution Width too large",inputTextList.get(0));
                                        resWidthValidator.createVibration();
                                        return;
                                    }
                                    if(text.getText().toString().length()  < 2 && !isEmpty(text) && resWidthValidator.getCount() < 1)
                                    {
                                        resWidthValidator.setValid(false);
                                        resWidthValidator.setCount(1);
                                        resWidthValidator.createWarning("Resolution Width is too small! The Resolution Width should be bigger than 2 Digits", "Resolution Width too small",
                                                "Ok", null,Settings_Activity.this, "Resolution Width too small",inputTextList.get(0));
                                        resWidthValidator.createVibration();
                                        return;
                                    }
                                    if(!isEmpty(inputTextList.get(0)) && !isEmpty(inputTextList.get(1)) && resWidthValidator.getCount() < 1)
                                    {
                                        resWidthValidator.setCount(1);
                                        boolean res = checkResolution(Float.parseFloat(inputTextList.get(0).getText().toString()),
                                                Float.parseFloat(inputTextList.get(1).getText().toString()), tableOfRatios);
                                        if(!res)
                                        {
                                            resWidthValidator.setValid(false);
                                            int[] aspectRatios = getAspectRatios(Float.parseFloat(inputTextList.get(0).getText().toString()), Float.parseFloat(inputTextList.get(1).getText().toString()));
                                            resWidthValidator.createWarning("Choose another resolution! Depending on your settings you have an aspect ratio of " + aspectRatios[0] + ":" +aspectRatios[1] + " which is not valid",
                                                    "Resolution invalid", "Ok", null,Settings_Activity.this, "Resolution invalid",inputTextList.get(0));
                                            resWidthValidator.createVibration();
                                            return;
                                        }
                                    }
                                }
                            });
                            resWidthValidator.setCount(0);
                            resWidthValidator.setValid(true);
                         break;
                        case R.id.editText5:
                            text.setError(null);
                            text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View view, boolean b) {

                                    if(text.getText().toString().length()  > 4 && !isEmpty(text) && resHeightValidator.getCount() < 1)
                                    {
                                        resHeightValidator.setValid(false);
                                        resHeightValidator.setCount(1);
                                        resHeightValidator.createWarning("Resolution Height too large! The Resolution Height should be smaller than or equal to 4 Digits", "Resolution Height too large",
                                                "Ok", null,Settings_Activity.this, "Resolution Height too large",inputTextList.get(1));
                                        resHeightValidator.createVibration();
                                        return;
                                    }
                                    if(text.getText().toString().length()  < 2 && !isEmpty(text) && resHeightValidator.getCount() < 1)
                                    {
                                        resHeightValidator.setValid(false);
                                        resHeightValidator.setCount(1);
                                        resHeightValidator.createWarning("Resolution Height too low! The Resolution Height should be bigger than 2 Digits", "Resolution Height too small",
                                                "Ok", null,Settings_Activity.this, "Resolution Height too small",inputTextList.get(1));
                                        resHeightValidator.createVibration();
                                        return;
                                    }
                                    if(!isEmpty(inputTextList.get(0)) && !isEmpty(inputTextList.get(1))&& resHeightValidator.getCount() < 1)
                                    {
                                        resHeightValidator.setCount(1);
                                        boolean res = checkResolution(Float.parseFloat(inputTextList.get(0).getText().toString()),
                                                Float.parseFloat(inputTextList.get(1).getText().toString()), tableOfRatios);
                                        if(!res)
                                        {
                                            resHeightValidator.setValid(false);
                                            int[] aspectRatios = getAspectRatios(Float.parseFloat(inputTextList.get(0).getText().toString()), Float.parseFloat(inputTextList.get(1).getText().toString()));
                                            resHeightValidator.createWarning("Choose another resolution! Depending on your settings you have an aspect ratio of " + aspectRatios[0] + ":" +aspectRatios[1] + " which is not valid",
                                                    "Resolution invalid", "Ok", null,Settings_Activity.this, "Resolution invalid",inputTextList.get(1));
                                            resHeightValidator.createVibration();
                                            return;
                                        }
                                    }
                                }

                            });
                            resHeightValidator.setCount(0);
                            resHeightValidator.setValid(true);
                         break;
                    }
                }

            });
        }
        /**
         * Check which radio button out of our two has been clicked
         * */
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
    /**
     * function for clearing all the editTexts
     * */
    public void clearText(ArrayList<EditText> texts)
    {
        for(EditText text : texts)
        {
            text.setText("");
        }
    }
    /**
     * check whether one edittext is null or empty
     * */
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
    /**
     * Function for setting the, above mentioned, standard texts
     * */
    public void setText(ArrayList<EditText> texts, String txt,float...values)
    {
        int i = 0;
        for(EditText text : texts)
        {
            /**
             * If we see the Id of our editText for the flight height, we skip it
             * */
            if(text.getId() != R.id.editText3)
            {
                if(i >= values.length)
                {
                    break;
                }
                if(text.getId() == R.id.editText2 || text.getId() == R.id.editText5 || text.getId() == R.id.editText6 || text.getId() == R.id.editText7)
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
    /**
     * function for validation the resolution fields using a hashtable with aspect ratios
     * */
    public boolean checkResolution(float resWidth, float resHeight, Hashtable table)
    {
        float gcd = getGcd(resWidth, resHeight);
        int aspectRatioFirst = Math.round(resWidth/gcd);
        int aspectRatioSecond = Math.round(resHeight/gcd);
        if(table.contains(aspectRatioFirst + ":" + aspectRatioSecond))
        {
            return true;
        }
        return false;
    }
    /**
     * function for getting an array of the two values of which the aspect ratio consists
     * */
    public int[] getAspectRatios(float resWidth, float resHeight)
    {
        int[] ratios = new int[2];
        float gcd = getGcd(resWidth, resHeight);
        int aspectRatioFirst = Math.round(resWidth/gcd);
        int aspectRatioSecond = Math.round(resHeight/gcd);
        ratios[0] = aspectRatioFirst;
        ratios[1] = aspectRatioSecond;
        return ratios;
    }
    /**
     * Hashtable with standart Aspect Ratios
     * */
    public Hashtable setHashtable()
    {
        Hashtable<Integer, String> ratioTable = new Hashtable<Integer, String>();
        ratioTable.put(1, "1:1");
        ratioTable.put(2, "4:3");
        ratioTable.put(3, "3:2");
        ratioTable.put(6, "3:1");
        ratioTable.put(4, "5:3");
        ratioTable.put(5, "16:9");
        ratioTable.put(6, "950:797");
        return ratioTable;
    }
    /**
     * Get Values from the EditText Fields: pixelSize, flightHeight and FOV
     * */
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
    /**
     * Check whether one of the text fields is empty
     * */
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
    /**
     * get the values from the overlap fields
     * */
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
        /**
         * calculate the values for horizontal and vertical overlap
         * */
        overlapValues[0] = (100 - overlapValues[0])/100;
        overlapValues[1] = (100 - overlapValues[1])/100;
        return overlapValues;
    }
    /**
     * Function for getting the values form our Resolution Fields and calculate the aspect ratio
     * */
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
     * simple recursive gcd function
     * */
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
    /**
     * Calculate the pixelsize which has to be shown to the user
     * */
    public float calculatePixelSize(float altitude, float fov, float resWidth, float resHeight)
    {
        float gcd = (float)getGcd((int) resWidth,(int) resHeight);
        float fotoWidth = (float) ((2.0 * altitude) * Math.tan(Math.toRadians((fov/2.0))));
        float fotoHeight = fotoWidth * ((resHeight/gcd)/(resWidth/gcd));//fotoWidth times aspect ratio
        return (float) (Math.sqrt(((fotoWidth/ resWidth) * 100) * ((fotoHeight/resHeight) * 100))); // meters times 100 gets us centimeters
    }
    /**
     * Calculate the height in the case the user gives us the pixel size and fov
     * */
    public float calculateHeight(float fov, float resHeight, float resWidth, float pixelSize)
    {
        float gcd = getGcd(resWidth, resHeight);
        float fotoWidth =(float) Math.sqrt((Math.pow(pixelSize, 2)*resWidth*resHeight)/(10000 * (resHeight/gcd)/(resWidth/gcd)));//we get this formula by rearranging the above formula for pixelSize
        return (float)(fotoWidth/(2 * Math.tan(Math.toRadians(fov/2.0))));
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
    public void clearFocus(EditText text){
        text.clearFocus();
    }
    public void settings_next(View view)
    {
        float[] inputValues = new float[3];
        float aspectRatio = 0;
        float[] overlap = new float[2];
        if(inputEmpty(inputTextList))
        {
            generalInput.createVibration();
            Warning warning = new Warning("Fill in the fields correctly before you continue.", "Please fill in the missing values", true, "OK", this);
            android.app.AlertDialog alertDialog = warning.createWarning();
            alertDialog.setTitle("Missing Values");
            alertDialog.show();
            return;
        }
        else
        {
            for(EditText text : inputTextList)
            {
                text.requestFocus();
                text.clearFocus();
            }
            if(heightValidator.getValid() && fovValidator.getValid() && overlapValidator.getValid() && resWidthValidator.getValid() && resHeightValidator.getValid())
            {
                inputValues = getInputValues();
                aspectRatio = getAspectRatio();
                overlap = getOverlap();
                allReady = true;
                if(allReady)
                {
                    Intent intent = new Intent(getApplicationContext(), Main_Activity.class);
                    intent.putExtra("com.example.nicol.dronflyvis.INPUT_VALUES", inputValues);
                    intent.putExtra("com.example.nicol.dronflyvis.ASPECT_RATIO", aspectRatio);
                    intent.putExtra("com.example.nicol.dronflyvis.OVERLAP", overlap);
                    intent.putExtra("com.example.nicol.dronflyvis.RADIO_SELECTION", droneFlag);

                    startActivity(intent);
                }
            }
        }

    }

}

