package com.example.nicol.dronflyvis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.VibrationEffect;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.os.Vibrator;
/**
 * @author Heiko Dreyer
 *
 * Class concerned with Validation and issuing of Warnings
 * */
public class InputValidator implements View.OnFocusChangeListener {

    private float min;
    private float max;
    private int minLength;
    private int maxLength;
    private Context ctx;
    private int count = 0;
    private Vibrator vibrator;
    private boolean isValid;

    public InputValidator(float a, float b, Context ctx)
    {
        this.min = a;
        this.max = b;
        this.ctx = ctx;
        vibrator = (Vibrator) ctx.getSystemService(ctx.VIBRATOR_SERVICE);
        this.isValid = false;
    }
    public InputValidator(Context ctx)
    {
        this.ctx = ctx;
        vibrator = (Vibrator) ctx.getSystemService(ctx.VIBRATOR_SERVICE);
        this.isValid = false;
    }
    public InputValidator(int minLength, int maxLength, Context ctx, int flag)
    {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.ctx = ctx;
        vibrator = (Vibrator) ctx.getSystemService(ctx.VIBRATOR_SERVICE);
        this.isValid = false;
    }
    /**
     * We count the numbers of warnings we already issued, setting it back to zero if text changes
     * */
    public void setCount(int count)
    {
        this.count = count;
    }
    public boolean getValid()
    {
        return this.isValid;
    }
    /**
     * Method for making the phone vibrate in case of a input that is not supported
     * */
    public void createVibration()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else
        {
            vibrator.vibrate(500);
        }
    }
    /**
     * Creating warnings with making a new Warning Object out of the Warning Class, the parameters are self explanatory
     * */
    public Button createWarning(String warningText, String firstTitle, String btnMsgOne, String btnMsgTwo, Context ctx, String secondTitle, EditText inputText)
    {
        Warning warning = new Warning(warningText , firstTitle , true, btnMsgOne, btnMsgTwo,ctx);
        android.app.AlertDialog alertDialog = warning.createWarning();
        alertDialog.setTitle(secondTitle);
        alertDialog.show();
        Button negativeBtn = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                alertDialog.dismiss();
                inputText.setText("");
                if(inputText.requestFocus()) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        return negativeBtn;
    }
    /**
     * Where we finally validate all the possible input and issue a few different Warnings
     **/
    @Override
    public void onFocusChange(View view, boolean b)
    {
        EditText inputText = (EditText)view;
        if(!b && this.count < 1)
        {
            String inputValue = ((EditText)view).getText().toString();
                if(!TextUtils.isEmpty(inputValue))
                {
                    if(Float.valueOf(inputValue) < this.min)
                    {
                        this.count++;
                        if(((Integer)inputText.getTag()) == 0)
                        {
                            createVibration();
                            createWarning("Altitude is too low, choose another one! Ten meters is the lowest possible altitude.", "Alitute too low",
                                    "Ok",null, ctx, "Altitude is too low", inputText);
                            this.isValid = true;
                        }
                        else if(((Integer)inputText.getTag()) == 1)
                        {
                            createVibration();
                            createWarning("FOV is too low, choose another one! Ten degree is the lowest possible FOV.", "Fov too low",
                                    "Ok",null, ctx, "FOV is too low", inputText);
                            this.isValid = true;
                        }
                        else if(((Integer)inputText.getTag()) == 2)
                        {
                            createVibration();
                            createWarning("Overlap is too low, choose another one! One percent is the lowest possible Overlap.", "Overlap too low",
                                    "Ok",null, ctx, "Overlap is too low", inputText);
                            this.isValid = true;
                        }
                        else if((Integer)inputText.getTag() == 3)
                        {
                            createVibration();
                            createWarning("pixel Size is too small, choose another one! 0.5 cm is the lowest possible pixel Size", "Pixel Size too low"
                                            , "Ok", null, ctx, "Pixel Size ist too low", inputText);
                            this.isValid = true;
                        }
                    }
                    else if(Float.valueOf(inputValue) > this.max)
                    {
                        this.count++;
                        if(((Integer)inputText.getTag()) == 0)
                        {
                            createVibration();
                             createWarning("Altitude is over 100 meters. Are you sure you want to continue?", "Alitute too large",
                                    "No","Yes", ctx, "Altitude over 100 meters", inputText);
                             this.isValid = true;
                        }
                        else if(((Integer)inputText.getTag()) == 1)
                        {
                            createVibration();
                            createWarning("FOV is too high, choose another one! 170 degree is the highest possible FOV.", "FOV too large",
                                    "Ok",null, ctx, "FOV is too large", inputText);
                            this.isValid = true;
                        }
                        else if(((Integer)inputText.getTag()) == 2)
                        {
                            createVibration();
                            createWarning("Overlap is too high, choose another one! 99% is the highest possible FOV.", "Overlap too large",
                                    "Ok",null, ctx, "Overlap is too large", inputText);
                            this.isValid = true;
                        }
                    }
                    else
                    {
                        this.isValid = true;
                    }
                }

            }
        }
}
