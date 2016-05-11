package com.deus_tech.aria2.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.deus_tech.aria2.R;

public class Util{


    public static void showAlert(Activity _activity, String _title, String _message){

        AlertDialog.Builder alert = new AlertDialog.Builder(_activity);
        alert.setTitle(_title);
        alert.setMessage(_message);
        alert.show();

    }//showAlert

    //R.string.gopro_ssid

    public static void saveStringPreference(Context _context, int _key, String _value){

        SharedPreferences sharedPref = _context.getSharedPreferences(_context.getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(_context.getString(_key), _value);
        editor.commit();

    }//saveStringPreference


    public static String loadStringPreference(Context _context, int _key){

        SharedPreferences sharedPref = _context.getSharedPreferences(_context.getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);

        String value = sharedPref.getString(_context.getString(_key), "");

        return value;

    }//loadStringPreference


}//Util
