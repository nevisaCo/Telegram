package com.finalsoft.admob.models;

public class CountItem {
    private final String name;
    private final int count;
    private final int repeat;
    private final int start;
    private String style = "";


    public CountItem(int count, String name) {
        this(count, name, 0);
    }

    public CountItem(int count, String name, int repeatGap) {
        this(count, name, repeatGap, 0, "");
    }

    public CountItem(int count, String name, int repeatGap, int start, String style) {
        this.name = name;
        this.count = count;
        this.repeat = repeatGap;
        this.start = start;
        this.style = style;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    public int getRepeatGap() {
        return repeat;
    }

    public int getStart() {
        return start;
    }

    public String getStyle() {
        return style;
    }


}
