package com.example.nicol.dronflyvis;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
/**
 * @author Heiko Dreyer
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

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(this.text.getText().toString().startsWith("."))
        {
            this.text.setText("0.");
        }
        this.validateText(this.text);
    }
}
