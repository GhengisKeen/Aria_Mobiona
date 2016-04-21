package com.deus_tech.aria.ariaService;


import com.deus_tech.aria.MainActivity;

public class AriaServiceBinder extends android.os.Binder{


    private AriaService ariaService;
    private MainActivity mainActivity;


    public AriaServiceBinder(AriaService _ariaService){

        ariaService = _ariaService;

    }//constructor


    public AriaService getService(){

        return this.ariaService;

    }//getService


    public void setMainActivity(MainActivity _mainActivity){

        mainActivity = _mainActivity;

    }//setMainActivity


    public MainActivity getMainActivity(){

        return mainActivity;

    }//getMainActivity


}//LocalBinder
