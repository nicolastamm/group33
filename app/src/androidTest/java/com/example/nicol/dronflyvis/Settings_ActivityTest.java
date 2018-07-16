package com.example.nicol.dronflyvis;

import android.provider.Settings;
import android.widget.EditText;

import org.junit.Test;

import static org.junit.Assert.*;

public class Settings_ActivityTest {

    @Test
    public void getGcd1() {

        Settings_Activity act = new Settings_Activity();

        assertEquals(1000, act.getGcd(4000.0f,3000.0f));

    }

    @Test
    public void getGcd2() {

        Settings_Activity act = new Settings_Activity();

        assertEquals(4000, act.getGcd(4000.0f,0.0f));

    }

    @Test
    public void getGcd3() {

        Settings_Activity act = new Settings_Activity();

        assertEquals(3000, act.getGcd(0.0f,3000.0f));

    }

    @Test
    public void getInputValues() {

        Settings_Activity act = new Settings_Activity();

        //warte auf Antwort von Heiko
    }

    @Test
    public void getRadioButton() {

        int radioButtonId = -1;
        String selected = "Dji Mavic Pro";
        Settings_Activity act = new Settings_Activity();

        //warte auf Antwort von Heiko
    }

    @Test
    public void isEmpty1() {

        EditText text = null;

        Settings_Activity act = new Settings_Activity();

        assertEquals(true, act.isEmpty(text));
    }

    /*@Test
    public void isEmpty2() {

        EditText text = asdasda;

        Settings_Activity act = new Settings_Activity();

        assertEquals(true, act.isEmpty(text));
    }*/

}