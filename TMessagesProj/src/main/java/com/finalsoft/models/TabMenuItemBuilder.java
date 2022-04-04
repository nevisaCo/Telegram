package com.finalsoft.models;

public class TabMenuItemBuilder {
    public int id   ;
    public int icon;
    public  String name;

    public TabMenuItemBuilder(int id, int icon, String name) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}
