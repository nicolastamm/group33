package com.example.nicol.dronflyvis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class Warning
{
    private AlertDialog.Builder dBuilder;
    private String msg;
    private String title;
    private boolean cancelable;
    private String btnMsg;
    private Bundle bundle;

    public Warning(String message, String title, boolean cancelable, String btnMsg, Context ctx)
    {
        this.msg = message;
        this.title = title;
        this.cancelable = cancelable;
        this.btnMsg = btnMsg;
        dBuilder = new AlertDialog.Builder(ctx);
    }

    public AlertDialog createWarning()
    {
        dBuilder.setTitle(title);
        dBuilder.setMessage(msg);
        dBuilder.setCancelable(cancelable);
        dBuilder.setPositiveButton(btnMsg, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        return dBuilder.create();
    }
}
