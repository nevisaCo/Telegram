package com.finalsoft.admob.models;

public class AdCountItem {
    private int count;
    private String name;

    public AdCountItem(int count, String name) {
        this.count = count;
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }
}
