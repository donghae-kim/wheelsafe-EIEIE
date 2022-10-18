package com.example.front;

public class Listviewitem {
    private int icon;
    private String name;
    private int icon2;

    public int getIcon(){return icon;}
    public String getName(){return name;}
    public int getIcon2(){return icon2;}

    public Listviewitem(int icon,String name,int icon2){
        this.icon=icon;
        this.name=name;
        this.icon2=icon2;
    }
}