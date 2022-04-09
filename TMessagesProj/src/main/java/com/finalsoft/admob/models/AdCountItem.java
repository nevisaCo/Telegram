package com.finalsoft.admob.models;

public class AdCountItem {
    private final String name;
    private final int count;
    private final int repeat;


    public AdCountItem(int count, String name) {
        this(count, name, 0);
    }

    public AdCountItem(int count, String name, int repeatGap) {
        this.name = name;
        this.count = count;
        this.repeat = repeatGap;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public int getRepeatGap() {
        return repeat;
    }
}
