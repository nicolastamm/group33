package com.example.nicol.dronflyvis;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
/**
 * @author Heiko Dreyer
 *
 * A abstract class concerned with "cleaning" up and better validation
 * */
public abstract class CustomTextWatcher implements TextWatcher {

    private EditText text;
    public CustomTextWatcher(EditText text)
    {
            this.text = text;
    }
    public abstract void validateText(EditText text);
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            /**
             * Don't care
             * */
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        /**
         * Don't care
         * */

    }

    @Override
    public void afterTextChanged(Editable editable) {
        /**
         * Put a Zero in front of a dot otherwise some EditText fields make the app crash
         * */
        if(this.text.getText().toString().startsWith("."))
        {
            this.text.setText("0.");
        }
        this.validateText(this.text);
    }
}
