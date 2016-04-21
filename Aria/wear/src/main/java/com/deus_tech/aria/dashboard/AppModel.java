package com.deus_tech.aria.dashboard;


import android.graphics.drawable.Drawable;

public class AppModel{


    private String name;
    private String packageName;
    private Drawable icon;
    private boolean isSelected;


    public AppModel(String _name, String _packageName, Drawable _icon){

        this.name = _name;
        this.packageName = _packageName;
        this.icon = _icon;

    }//constructor


    public String getName(){

        return name;

    }//getName


    public Drawable getIcon(){

        return icon;

    }//getIcon


    public String getPackageName(){

        return packageName;

    }//getPackageName


    public void setSelected(boolean _isSelected){

        isSelected = _isSelected;

    }//setSelected


    public boolean isSelected(){

        return isSelected;

    }//isSelected

}//App