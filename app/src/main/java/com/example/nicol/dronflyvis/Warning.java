package com.example.nicol.dronflyvis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;


/**
 *
 * @author Heiko Dreyer
 *
 * In this class we are concerned with creating Warnings using AlertDialogs
 * */
public class Warning
{
    private AlertDialog.Builder dBuilder;
    private String msg;
    private String title;
    private boolean cancelable;
    private String btnMsg;
    private String btnMsgTwo;
    private int which;

    public Warning(String message, String title, boolean cancelable, String btnMsg, Context ctx)
    {
        this.msg = message;
        this.title = title;
        this.cancelable = cancelable;
        this.btnMsg = btnMsg;
        dBuilder = new AlertDialog.Builder(ctx);
    }

    public Warning(String message, String title, boolean cancelable, String btnMsg, String btnMsgTwo,Context ctx)
    {
        this.msg = message;
        this.title = title;
        this.cancelable = cancelable;
        this.btnMsg = btnMsg;
        dBuilder = new AlertDialog.Builder(ctx);
        this.btnMsgTwo = btnMsgTwo;
    }

    /**
     * In this Method we create a warning with the given Messages and buttons AlertDialog.Builder
     * */
    public AlertDialog createWarning()
    {
        dBuilder.setTitle(title);
        dBuilder.setMessage(msg);
        dBuilder.setCancelable(cancelable);
        dBuilder.setPositiveButton(btnMsg, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //setWhich(i);
                dialogInterface.cancel();
            }
        });
        /**
         * A Negative Button for when its needed
         * */
        if(btnMsgTwo != null)
        {
            dBuilder.setNegativeButton(btnMsgTwo, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //setWhich(i);
                    dialogInterface.cancel();
                }
            });
        }
        return dBuilder.create();
    }

    /***
     *
     * Getters & Setters to get and set the which value to check which button has been clicked
     * */
    public int getWhich()
    {
        return this.which;
    }
    public void setWhich(int which)
    {
        this.which = which;
    }
}
