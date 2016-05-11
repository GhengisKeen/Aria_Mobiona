package com.deus_tech.aria2.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;


public class MyEditText extends EditText{


    public MyEditText(Context context){
        super(context);
    }//constructor1


    public MyEditText(Context context, AttributeSet attrs){
        super(context, attrs);
    }//constructor2


    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }//constructor3


    public boolean onKeyPreIme(int keyCode, KeyEvent event){

        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){

            clearFocus();
            return true;

        }

        return super.dispatchKeyEvent(event);

    }//onKeyPreIme


}//MyEditText
