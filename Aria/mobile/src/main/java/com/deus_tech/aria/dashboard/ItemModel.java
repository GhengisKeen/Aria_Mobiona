package com.deus_tech.aria.dashboard;


public class ItemModel{

    private String name;
    private boolean isSelected;


    public ItemModel(String _name){

        this.name = _name;

    }//constructor


    public String getName(){

        return name;

    }//getName


    public void setSelected(boolean _isSelected){

        isSelected = _isSelected;

    }//setSelected


    public boolean isSelected(){

        return isSelected;

    }//isSelected

}//ItemModel
